import os
import infrasturcture.redis
from .model import TrendItem

TREND_KEY_PREFIX = 'recommendation:trend'
TREND_WINDOW = TREND_KEY_PREFIX + ':window'
TREND_WINDOW_EPOCH = TREND_WINDOW + ':epoch'
TREND_WINDOW_EPOCH_LIST = TREND_WINDOW_EPOCH + ':list'
TREND_WINDOW_CURRENT_EPOCH = TREND_WINDOW_EPOCH + ':current'

TREND_WINDOW_SIZE = int(os.getenv('TREND_WINDOW_SIZE'))
TREND_WINDOW_EPOCH_DURATION = int(os.getenv('TREND_WINDOW_EPOCH_DURATION'))

_redis = infrasturcture.redis.get_redis()

def clear_trend():
    for key in _redis.scan_iter(match=f'{TREND_KEY_PREFIX}*'):
        _redis.delete(key)

def increment_trend_score(tourspot_id: str, delta: int):
    _redis.zincrby(TREND_WINDOW_CURRENT_EPOCH, delta, tourspot_id)

def get_trend_topk(k) -> list[TrendItem]:
    topk = _redis.zrevrange(
        name=TREND_WINDOW,
        start=0,
        end=k-1,
        withscores=True
    )
    topk = list(map(lambda x: TrendItem(item_id=x[0], trend_score=x[1]), topk))

    return topk

def slide_trend_window():
    # current epoch에 부여할 새로운 epoch id
    new_epoch_key = get_new_epoch_key()

    # 제거할 epoch 선정
    to_expire_keys = _redis.lrange(TREND_WINDOW_EPOCH_LIST, 0, -TREND_WINDOW_SIZE)

    if not _redis.exists(TREND_WINDOW_CURRENT_EPOCH):
        return

    pipe = _redis.pipeline()

    # 새로운 epoch를 sliding window에 추가
    pipe.rename(TREND_WINDOW_CURRENT_EPOCH, new_epoch_key)
    pipe.rpush(TREND_WINDOW_EPOCH_LIST, new_epoch_key)

    # 오래된 epoch을 sliding window에서 제거
    if to_expire_keys:
        pipe.delete(*to_expire_keys)
    pipe.ltrim(TREND_WINDOW_EPOCH_LIST, -TREND_WINDOW_SIZE, -1)

    pipe.execute()

    # 집계 결과 갱신
    aggregate_and_store_trend_window()

def aggregate_and_store_trend_window():
    aggregated = aggregate_trend_window()

    pipe = _redis.pipeline()
    pipe.delete(TREND_WINDOW)
    pipe.zadd(TREND_WINDOW, aggregated)
    pipe.execute()

def aggregate_trend_window() -> dict[str, float]:
    aggregated = {}

    epoch_keys = _redis.lrange(TREND_WINDOW_EPOCH_LIST, 0, -1)
    for epoch_key in epoch_keys:
        for tourspot_id, score in _redis.zscan_iter(epoch_key):
            if tourspot_id not in aggregated:
                aggregated[tourspot_id] = 0
            
            aggregated[tourspot_id] += score
    
    return aggregated

def get_new_epoch_key() -> str:
    last_epoch_key = _redis.lindex(TREND_WINDOW_EPOCH_LIST, -1)
    if not last_epoch_key:
        return build_epoch_key(0)
    
    last_id = int(last_epoch_key[len(TREND_WINDOW_EPOCH)+1:])
    new_id = (last_id + 1) % 1000
    
    return build_epoch_key(new_id)

def build_epoch_key(id):
    return TREND_WINDOW_EPOCH + ':' + str(id)