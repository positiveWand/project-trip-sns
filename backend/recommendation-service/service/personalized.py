from sklearn.neighbors import NearestNeighbors
from datetime import datetime

from event.event import Event

import infrasturcture.mongodb

_tourspot_ids = None
_tourspot_embeddings = None
_knn_model = None

def init_model():
    global _tourspot_ids, _tourspot_embeddings, _knn_model
    mongo = infrasturcture.mongodb.get_mongo()

    _tourspot_ids = []
    _tourspot_embeddings = []

    for tourspot in mongo['tourin']['tourspot'].find():
        _tourspot_ids.append(tourspot['_id'])
        _tourspot_embeddings.append(tourspot['text_embedding'])
    
    _knn_model = NearestNeighbors(metric='cosine', algorithm='brute', n_neighbors=11)
    _knn_model.fit(_tourspot_embeddings)

def get_personalized_topk(user_id: str, k: int) -> list[str]:
    # created_at 기준으로 top-k 정렬
    mongo = infrasturcture.mongodb.get_mongo()
    collection = mongo['tourin']['user_recommendation']

    topk = []
    for rec in collection.find({'user_id': user_id}).sort('created_at', -1):
        topk += rec['similar_items']

    return topk[:k]

def retrieve_similar_topk(query_item_id: str, k: int) -> list[str]:
    # top-k 유사 아이템 반환
    global _tourspot_ids, _tourspot_embeddings, _knn_model
    if _tourspot_ids is None or _tourspot_embeddings is None or _knn_model is None:
        init_model()
    
    query_item_idx = _tourspot_ids.index(query_item_id)

    topk = _knn_model.kneighbors(
        X=_tourspot_embeddings,
        n_neighbors=min(len(_tourspot_embeddings), k+1),
        return_distance=False
    )

    topk = topk[query_item_idx]

    return [_tourspot_ids[t] for t in topk[1:]]

def append_similar_items(user_id: str, item_id: str, event: Event):
    # top-k 유사 아이템 얻기
    topk = retrieve_similar_topk(item_id, 10)
    
    # 중복된 아이템 필터링
    mongo = infrasturcture.mongodb.get_mongo()
    collection = mongo['tourin']['user_recommendation']

    previous_items = set(get_personalized_topk(user_id, 10))
    for rec in collection.find({'user_id': user_id}):
        previous_items.add(rec['query_item_id'])
    
    filtered_topk = []
    for item in topk:
        if item not in previous_items:
            filtered_topk.append(item)
    
    if not filtered_topk:
        return

    # 아이템 추천 확정 및 삽입
    collection.insert_one({
        'created_at': datetime.now(),
        'type': 'text_embedding_similarity',
        'user_id': user_id,
        'query_item_id': item_id,
        'source_event': event.model_dump(),
        'similar_items': filtered_topk[:2]
    })