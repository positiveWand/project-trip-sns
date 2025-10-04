import unittest
import random
from concurrent.futures import ThreadPoolExecutor, as_completed
import infrasturcture.redis
import service.trend as trend_service

class TestServiceTrend(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls._redis = infrasturcture.redis.get_redis()

    def set_test_trend_window(self):
        trend_service.clear_trend()

        score = {}

        # 1번째 epoch
        epoch_key = trend_service.build_epoch_key(0)
        self._redis.zincrby(epoch_key, 4000, '100000')
        self._redis.zincrby(epoch_key, 3000, '100001')
        self._redis.zincrby(epoch_key, 2000, '100002')
        self._redis.zincrby(epoch_key, 1000, '100003')
        score['100000'] = 4000
        score['100001'] = 3000
        score['100002'] = 2000
        score['100003'] = 1000

        self._redis.rpush(trend_service.TREND_WINDOW_EPOCH_LIST, epoch_key)

        # 1번째 이후 epoch
        for i in range(1, trend_service.TREND_WINDOW_SIZE):
            epoch_key = trend_service.build_epoch_key(i)
            for tourspot_id in range(100):
                tourspot_id = str(tourspot_id)
                inc = random.randint(1, 100)
                self._redis.zincrby(epoch_key, inc, tourspot_id)

                if tourspot_id not in score:
                    score[tourspot_id] = 0
                score[tourspot_id] += inc

            self._redis.rpush(trend_service.TREND_WINDOW_EPOCH_LIST, epoch_key)
        
        trend_service.aggregate_and_store_trend_window()
        
        return score

    def rank_items(self, score: dict[str, float]):
        ranking = [(i, s) for i, s in score.items()]
        ranking.sort(key=lambda x: (x[1], x[0]), reverse=True)
        
        return ranking

    def test_트렌드_상위_k개_관광지가_조회된다(self):
        # given/when - 트렌드 초기화(관광지 트렌드 무작위 생성)
        expected_score = self.set_test_trend_window()

        # then - 기대되는 순위와 집계된 순위가 일치
        expected_topk = self.rank_items(expected_score)
        expected_topk = list(map(lambda x: x[0], expected_topk))
        print('[기대 관광지 점수 순 정렬]')
        print(expected_topk)

        aggregated_topk = trend_service.get_trend_topk(150)
        aggregated_topk = list(map(lambda x: x[0], aggregated_topk))
        print('[실제 관광지 점수 순 정렬]')
        print(aggregated_topk)

        self.assertEqual(expected_topk, aggregated_topk)

    def test_슬라이딩_윈도우_전진이_정확히_이뤄진다(self):
        before_score = self.set_test_trend_window()
        
        # before
        aggregated_score = trend_service.aggregate_trend_window()
        self.assertEqual(before_score, aggregated_score)

        before_epoch_list = self._redis.lrange(trend_service.TREND_WINDOW_EPOCH_LIST, 0, -1)
        self.assertEqual(
            list(map(trend_service.build_epoch_key, list(range(trend_service.TREND_WINDOW_SIZE)))),
            before_epoch_list
        )
        
        # event - 슬라이딩 윈도우 전진
        after_score = {}
        for tourspot_id in before_score:
            if len(tourspot_id) > 4:
                continue

            inc = random.randint(1, 100)
            trend_service.increment_trend_score(tourspot_id, inc)

            after_score[tourspot_id] = before_score[tourspot_id] + inc
        trend_service.slide_trend_window()

        # after
        ## 기대 순위 = 집계 순위
        aggregated_score = trend_service.aggregate_trend_window()
        self.assertEqual(after_score, aggregated_score)

        ## epoch 목록 갱신
        after_epoch_list = self._redis.lrange(trend_service.TREND_WINDOW_EPOCH_LIST, 0, -1)
        self.assertEqual(
            list(map(trend_service.build_epoch_key, list(range(1, trend_service.TREND_WINDOW_SIZE+1)))),
            after_epoch_list
        )
        
        ## current epoch는 비어있음
        card = self._redis.zcard(trend_service.TREND_WINDOW_CURRENT_EPOCH)
        self.assertEqual(0, card)

    def test_슬라이딩_윈도우_집계가_정확히_이뤄진다(self):
        expected_score = self.set_test_trend_window()
        aggregated_score = trend_service.aggregate_trend_window()

        self.assertEqual(set(expected_score.keys()), set(aggregated_score.keys()))

        for ts_id in expected_score:
            self.assertEqual(expected_score[ts_id], int(aggregated_score[ts_id]), f'tourspot_id={ts_id}')
    
    def test_슬라이딩_윈도우가_전진해도_트렌드_점수는_누락되지_않는다(self):
        # 슬라이딩 윈도우 전진 및 집계 중 트렌드 점수가 누락되지 않음을 검증
        CLIENT_COUNT = 30

        expected_score = self.set_test_trend_window()

        def client_job():
            job_score = {}
            for _ in range(200):
                tourspot_id = str(random.randint(0, 99))
                inc = random.randint(1, 100)
                trend_service.increment_trend_score(tourspot_id, inc)

                if tourspot_id not in job_score:
                    job_score[tourspot_id] = 0
                job_score[tourspot_id] += inc

            return job_score

        with ThreadPoolExecutor(max_workers=CLIENT_COUNT) as executor:
            futures = []
            for i in range(CLIENT_COUNT):
                result = executor.submit(client_job)
                futures.append(result)
                if i == CLIENT_COUNT // 3:
                    trend_service.slide_trend_window()
            
            for f in as_completed(futures):
                job_score = f.result()
                for tourspot_id in job_score:
                    expected_score[tourspot_id] += job_score[tourspot_id]
        
        del expected_score['100000']
        del expected_score['100001']
        del expected_score['100002']
        del expected_score['100003']

        aggregated_score = trend_service.aggregate_trend_window()
        
        for tourspot_id, score in self._redis.zscan_iter(trend_service.TREND_WINDOW_CURRENT_EPOCH):
            if tourspot_id not in aggregated_score:
                aggregated_score[tourspot_id] = 0
            
            aggregated_score[tourspot_id] += score
        
        self.assertEqual(expected_score, aggregated_score)