from dotenv import load_dotenv
load_dotenv('./config.env')

import infrasturcture.rabbitmq as rabbitmq
import event.trend as trend_event

if __name__ == '__main__':
    # 메시지 큐 작업 실행
    rabbitmq.subscribe('recommendation.trend', trend_event.handle_trend_event)
    rabbitmq.start_consuming()