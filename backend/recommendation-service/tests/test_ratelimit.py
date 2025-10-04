import unittest
import time
import infrasturcture.redis
import infrasturcture.ratelimit as ratelimit

class TestRatelimit(unittest.TestCase):
    def setUp(self):
        _redis = infrasturcture.redis.get_redis()

        for key in _redis.scan_iter(match='ratelimit:*'):
            _redis.delete(key)
    
    def tearDown(self):
        _redis = infrasturcture.redis.get_redis()

        for key in _redis.scan_iter(match='ratelimit:*'):
            _redis.delete(key)

    def test_try_single_consume(self):
        self.assertTrue(ratelimit.try_single_consume('test', 3))

        for _ in range(3):
            self.assertFalse(ratelimit.try_single_consume('test', 3))
            
        time.sleep(5)

        self.assertTrue(ratelimit.try_single_consume('test', 3))
