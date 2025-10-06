import threading
import json
import time
import os
import logging

import pika
import pika.channel
import pika.spec

logger = logging.getLogger('infrasturcture.rabbitmq')

_HANDLER = {}
handler_lock = threading.Lock()

_QUEUE_TO_EVENT_STREAM = {}

def start_consuming():
    while True:
        try:
            connection = pika.BlockingConnection(
                parameters=pika.ConnectionParameters(
                    host=os.getenv('RABBIT_HOST'),
                    port=os.getenv('RABBIT_PORT'),
                    credentials=pika.PlainCredentials(
                        username=os.getenv('RABBIT_USERNAME'),
                        password=os.getenv('RABBIT_PASSWORD')
                    )
                )
            )

            channel = connection.channel()
            
            channel.exchange_declare(exchange='event.user.ex', exchange_type='fanout')
            channel.queue_declare(
                queue='recommendation.trend',
                arguments={
                    'x-message-ttl': 600000,
                    'x-dead-letter-exchange': 'event.user.dlx',
                    'x-dead-letter-routing-key': 'recommendation.trend'
                }
            )
            channel.queue_bind(exchange='event.user.ex', queue='recommendation.trend')

            channel.exchange_declare(exchange='event.user.dlx', exchange_type='direct', durable=True)
            channel.queue_declare(queue='recommendation.trend.dlq', durable=True)
            channel.queue_bind(exchange='event.user.dlx', queue='recommendation.trend.dlq', routing_key='recommendation.trend')

            channel.basic_qos(prefetch_count=5)

            consumer_tag = channel.basic_consume(
                queue='recommendation.trend',
                on_message_callback=dispatch_event,
                auto_ack=False,
                exclusive=True
            )
            _QUEUE_TO_EVENT_STREAM[consumer_tag] = 'recommendation.trend'

            logger.info('이벤트 소비 시작!')
            channel.start_consuming()
        except Exception as e:
            logger.error('연결 중 오류 발생', exc_info=True)
        finally:
            try:
                connection.close()
            except Exception as e:
                logger.error('연결 해제 중 오류 발생', exc_info=True)
        
        time.sleep(5)

def subscribe(event_stream_key: str, handler: callable):
    handler_lock.acquire()    
    if event_stream_key not in _HANDLER:
        _HANDLER[event_stream_key] = []
    _HANDLER[event_stream_key].append(handler)
    handler_lock.release()

def unsubscribe(event_stream_key: str, handler: callable):
    handler_lock.acquire()
    if event_stream_key in _HANDLER:
        _HANDLER[event_stream_key].remove(handler)
        if not _HANDLER[event_stream_key]:
            del _HANDLER[event_stream_key]
    handler_lock.release()

def dispatch_event(
        channel: pika.channel.Channel,
        method: pika.spec.Basic.Deliver,
        properties: pika.BasicProperties,
        body: bytes
):
    try:
        # 이벤트 디스패치
        event_stream = _QUEUE_TO_EVENT_STREAM.get(method.consumer_tag)
        if not event_stream:
            logger.warning(f'컨슈머({method.consumer_tag}) 맵핑 실패')
            channel.basic_nack(delivery_tag=method.delivery_tag, requeue=False)
            return
        
        for handler in _HANDLER[event_stream]:
            result = handler(json.loads(body.decode()))
            logger.debug(f'msg_body={body.decode()}, result={result}')

        channel.basic_ack(delivery_tag=method.delivery_tag)
    except Exception as e:
        logger.error(f'이벤트 스트림({event_stream}) 이벤트 핸들링 중 오류 발생', exc_info=True)
        channel.basic_nack(delivery_tag=method.delivery_tag, requeue=False)