import infrasturcture.redis

_redis = infrasturcture.redis.get_redis()

def try_single_consume(key: str, duration: int) -> bool:
    result = _redis.set(
        name=f'ratelimit:single:{key}',
        value='CONSUMED',
        nx=True,
        ex=duration
    )

    return bool(result)