from datetime import datetime, timedelta

import service.trend as trend_service
from infrasturcture.ratelimit import *
from event.event import EventHandleResult, UserTourspotEvent

TOURSPOT_VIEW_SCORE = 1
TOURSPOT_BOOKMARK_SCORE = 2

def handle_trend_event(event) -> EventHandleResult:
    # 이벤트 모델 변환
    event = UserTourspotEvent.model_validate(event)

    # 이벤트 만료 및 멱등성 처리
    if event.timestamp < datetime.now() - timedelta(minutes=trend_service.TREND_WINDOW_EPOCH_DURATION):
        return 'EXPIRED'
    if not try_single_consume(f'idem:{event.id}', 3600):
        return 'DUPLICATE'
    
    # 이벤트 처리
    if event.type == 'user.viewTourspot':
        if try_single_consume(f'trend:view:{event.data.user_id}:{event.data.tourspot_id}', 3600):
            trend_service.increment_trend_score(event.data.tourspot_id, TOURSPOT_VIEW_SCORE)
    elif event.type == 'user.bookmarkTourspot':
        if try_single_consume(f'trend:bookmark:{event.data.user_id}:{event.data.tourspot_id}', 3600):
            trend_service.increment_trend_score(event.data.tourspot_id, TOURSPOT_BOOKMARK_SCORE)
    
    return 'CONSUMED'