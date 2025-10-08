from datetime import datetime, timedelta

import service.personalized as personalized_service
from infrasturcture.ratelimit import *
from event.event import EventHandleResult, UserTourspotEvent

def handle_personalized_event(event) -> EventHandleResult:
    # 이벤트 모델 변환
    event = UserTourspotEvent.model_validate(event)

    # 이벤트 만료 및 멱등성 처리
    if event.timestamp < datetime.now() - timedelta(minutes=30):
        return 'EXPIRED'
    if not try_single_consume(f'idem:recommendation:personalized:{event.id}', 3600):
        return 'DUPLICATE'

    # 이벤트 처리
    personalized_service.append_similar_items(user_id=event.data.user_id, item_id=event.data.tourspot_id, event=event)

    return 'CONSUMED'