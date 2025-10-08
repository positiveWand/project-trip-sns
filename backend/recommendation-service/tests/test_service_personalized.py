import unittest
from datetime import datetime, timedelta
from sklearn.neighbors import NearestNeighbors

import infrasturcture.mongodb
from event.event import Event
import service.personalized as personalized_service

class TestServiceTrend(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.mongo = infrasturcture.mongodb.get_mongo()
        cls.tourspot_collection = cls.mongo['tourin']['tourspot']
        cls.user_rec_collection = cls.mongo['tourin']['user_recommendation']
    
    def tearDown(self):
        self.user_rec_collection.delete_many({})
    
    def test_get_personalized_topk(self):
        # given - 테스트용 추천 아이템
        expected_topk = []

        test_recommendations = []
        for i in range(6):
            similar_items = [f't{10*i}', f't{10*i+1}']
            test_recommendations.append({
                'created_at': datetime.now()-timedelta(minutes=10*i),
                'type': 'text_embedding_similarity',
                'user_id': 'testuser1',
                'query_item_id': f't{i}',
                'source_event': None,
                'similar_items': similar_items
            })
            expected_topk += similar_items
        test_recommendations.append({
            'created_at': datetime.now(),
            'type': 'text_embedding_similarity',
            'user_id': 'testuser2',
            'query_item_id': '',
            'source_event': None,
            'similar_items': ['200', '201']
        })
        self.user_rec_collection.insert_many(test_recommendations)
        
        self.assertEqual(
            expected_topk[:5],
            personalized_service.get_personalized_topk('testuser1', 5)
        )
        self.assertEqual(
            expected_topk[:20],
            personalized_service.get_personalized_topk('testuser1', 20)
        )
    
    def test_retreive_similar_topk(self):
        personalized_service._tourspot_ids = [str(i) for i in range(10)]
        personalized_service._tourspot_embeddings = [
            [0, i]
            for i in range(10)
        ]
        personalized_service._knn_model = NearestNeighbors(metric='cosine', algorithm='brute', n_neighbors=11)
        personalized_service._knn_model.fit(personalized_service._tourspot_embeddings)

        self.assertEqual(
            ['1', '2', '3', '4', '5'],
            personalized_service.retrieve_similar_topk('0', 5),
        )
        self.assertEqual(
            ['1', '2', '3', '4', '5', '6', '7', '8', '9'],
            personalized_service.retrieve_similar_topk('0', 20),
        )

    def test_append_similar_items(self):
        # given - 테스트용 아이템
        personalized_service._tourspot_ids = ['t'+str(i) for i in range(10)]
        personalized_service._tourspot_embeddings = [
            [0, i]
            for i in range(10)
        ]
        personalized_service._knn_model = NearestNeighbors(metric='cosine', algorithm='brute', n_neighbors=11)
        personalized_service._knn_model.fit(personalized_service._tourspot_embeddings)

        test_recommendations = [
            {
                'created_at': datetime.now()-timedelta(minutes=10),
                'type': 'text_embedding_similarity',
                'user_id': 'testuser1',
                'query_item_id': 't1',
                'source_event': None,
                'similar_items': ['t2', 't3']
            },
            {
                'created_at': datetime.now()-timedelta(minutes=20),
                'type': 'text_embedding_similarity',
                'user_id': 'testuser1',
                'query_item_id': 't3',
                'source_event': None,
                'similar_items': ['t5', 't7']
            }
        ]
        self.user_rec_collection.insert_many(test_recommendations)

        test_source_event = Event(id='test', type='test', data=None, timestamp=datetime.now())
        test_source_event.timestamp = test_source_event.timestamp.replace(microsecond=(test_source_event.timestamp.microsecond // 1000) * 1000)
        personalized_service.append_similar_items('testuser1', 't0', test_source_event)

        new_recommendation = self.user_rec_collection.find({'user_id': 'testuser1'}).sort('created_at', -1).limit(1).next()
        self.assertEqual(
            'testuser1',
            new_recommendation['user_id']
        )
        self.assertEqual(
            't0',
            new_recommendation['query_item_id']
        )
        self.assertEqual(
            ['t4', 't6'],
            new_recommendation['similar_items']
        )
        self.assertEqual(
            test_source_event.model_dump(),
            new_recommendation['source_event']
        )
