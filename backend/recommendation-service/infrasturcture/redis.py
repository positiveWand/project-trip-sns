import os
import redis

_redis = None

def get_redis() -> redis.Redis:
    global _redis

    if _redis is None:
        REDIS_HOST = os.getenv('REDIS_HOST')
        REDIS_PORT = os.getenv('REDIS_PORT')

        _redis = redis.Redis(
            host=REDIS_HOST,
            port=REDIS_PORT,
            decode_responses=True
        )
    
    return _redis