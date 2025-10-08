import unittest
from unittest.mock import patch
from datetime import datetime, timedelta
from pydantic import ValidationError

import event.trend as trend_event

class TestRatelimit(unittest.TestCase):
    @patch('event.trend.try_single_consume', return_value=True)
    @patch('event.trend.trend_service.increment_trend_score')
    def test_event_model_validation(self, mock_trend_service, mock_ratelimit):
        # 필드 누락
        with self.assertRaises(ValidationError):
            trend_event.handle_trend_event({
                'id': '5129e0b9-23de-4416-b55f-332869389149',
                'type': 'missingField',
            })
        with self.assertRaises(ValidationError):
            trend_event.handle_trend_event({
                'id': '5129e0b9-23de-4416-b55f-332869389149',
                'type': 'missingField',
                'timestamp': datetime.now().isoformat(timespec='seconds')
            })
        with self.assertRaises(ValidationError):
            trend_event.handle_trend_event({
                'id': '5129e0b9-23de-4416-b55f-332869389149',
                'type': 'missingField',
                'data': {
                    'user_id': 'testuser'
                },
                'timestamp': datetime.now().isoformat(timespec='seconds')
            })

        # 잘못된 타입
        with self.assertRaises(ValidationError):
            trend_event.handle_trend_event({
                'id': 232323232323,
                'type': 34343434,
                'data': {
                    'user_id': 'testuser',
                    'tourspot_id': '2020202'
                },
                'timestamp': datetime.now().isoformat(timespec='seconds')
            })
        
        # 성공 케이스
        trend_event.handle_trend_event({
            'id': '5129e0b9-23de-4416-b55f-332869389149',
            'type': 'excessiveField',
            'data': {
                'user_id': 'testuser',
                'tourspot_id': '2020202'
            },
            'timestamp': datetime.now().isoformat(timespec='seconds'),
            'testfield': 'blah, blah'
        })
        
        trend_event.handle_trend_event({
            'id': '5129e0b9-23de-4416-b55f-332869389149',
            'type': 'user.viewTourspot',
            'data': {
                'user_id': 'testuser',
                'tourspot_id': '2020202'
            },
            'timestamp': datetime.now().isoformat(timespec='seconds')
        })

    @patch('event.trend.try_single_consume', return_value=True)
    @patch('event.trend.trend_service.increment_trend_score')
    def test_expired_event(self, mock_trend_service, mock_ratelimit):
        # 만료된 이벤트 처리
        result = trend_event.handle_trend_event({
            'id': '5129e0b9-23de-4416-b55f-332869389149',
            'type': 'user.viewTourspot',
            'data': {
                'user_id': 'testuser',
                'tourspot_id': '2020202'
            },
            'timestamp': (datetime.now() - timedelta(days=3)).isoformat(timespec='seconds')
        })
        
        self.assertEqual(result, 'EXPIRED', '오래된 이벤트는 만료되어 처리되지 않는다.')
        mock_trend_service.assert_not_called()

        result = trend_event.handle_trend_event({
            'id': '5129e0b9-23de-4416-b55f-332869389149',
            'type': 'user.viewTourspot',
            'data': {
                'user_id': 'testuser',
                'tourspot_id': '2020202'
            },
            'timestamp': datetime.now().isoformat(timespec='seconds')
        })

        self.assertEqual(result, 'CONSUMED', '유효한 이벤트는 정상적으로 처리된다.')
        mock_trend_service.assert_called_once()

    @patch('event.trend.try_single_consume', return_value=False)
    @patch('event.trend.trend_service.increment_trend_score')
    def test_duplicate_event(self, mock_trend_service, mock_ratelimit):
        # 중복된 이벤트 처리
        result = trend_event.handle_trend_event({
            'id': '5129e0b9-23de-4416-b55f-332869389149',
            'type': 'user.viewTourspot',
            'data': {
                'user_id': 'testuser',
                'tourspot_id': '2020202'
            },
            'timestamp': datetime.now().isoformat(timespec='seconds')
        })

        self.assertEqual(result, 'DUPLICATE', '중복된 이벤트는 처리되지 않는다.')
        mock_trend_service.assert_not_called()
    
    @patch('event.trend.try_single_consume', return_value=True)
    @patch('event.trend.trend_service.increment_trend_score')
    def test_view_event_consume(self, mock_trend_service, mock_ratelimit):
        # 조회 이벤트 처리 테스트
        result = trend_event.handle_trend_event({
            'id': '5129e0b9-23de-4416-b55f-332869389149',
            'type': 'user.viewTourspot',
            'data': {
                'user_id': 'testuser',
                'tourspot_id': '2020202'
            },
            'timestamp': datetime.now().isoformat(timespec='seconds')
        })

        self.assertEqual(result, 'CONSUMED', '관광지 조회 이벤트가 정상 처리된다.')
        mock_trend_service.assert_called_once_with('2020202', trend_event.TOURSPOT_VIEW_SCORE)
    
    @patch('event.trend.try_single_consume', return_value=True)
    @patch('event.trend.trend_service.increment_trend_score')
    def test_bookmark_event_consume(self, mock_trend_service, mock_ratelimit):
        # 북마크 이벤트 처리 테스트
        result = trend_event.handle_trend_event({
            'id': '5129e0b9-23de-4416-b55f-332869389149',
            'type': 'user.bookmarkTourspot',
            'data': {
                'user_id': 'testuser',
                'tourspot_id': '2020202'
            },
            'timestamp': datetime.now().isoformat(timespec='seconds')
        })

        self.assertEqual(result, 'CONSUMED', '관광지 북마크 이벤트가 정상 처리된다.')
        mock_trend_service.assert_called_once_with('2020202', trend_event.TOURSPOT_BOOKMARK_SCORE)
    
    @patch('event.trend.try_single_consume', return_value=True)
    @patch('event.trend.trend_service.increment_trend_score')
    def test_ooc_event_consume(self, mock_trend_service, mock_ratelimit):
        # out-of-concern 이벤트 처리 테스트
        result = trend_event.handle_trend_event({
            'id': '5129e0b9-23de-4416-b55f-332869389149',
            'type': 'event.ooc',
            'data': {
                'user_id': 'testuser',
                'tourspot_id': '2020202'
            },
            'timestamp': datetime.now().isoformat(timespec='seconds')
        })

        self.assertEqual(result, 'CONSUMED', '그 외 이벤트가 정상 처리된다.')
        mock_trend_service.assert_not_called()