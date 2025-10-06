import os
from datetime import datetime
import json

import pandas as pd
from prefect import flow, task, get_run_logger
from prefect.states import Completed
from prefect.blocks.system import Secret
from prefect.variables import Variable
from prefect.flow_runs import pause_flow_run
from prefect.concurrency.sync import rate_limit
from google import genai
from google.genai import types
from pymongo import MongoClient

from tourspot_model import *

@task(
    persist_result=True,
    cache_key_fn=lambda _, params: f'TOURSPOT_EMBEDDINGS(KEY={params['key']},OFFSET={params['offset']},LIMIT={params['limit']})',
    result_serializer='json',
    retries=3, 
    retry_delay_seconds=300
)
def embed_tourspots(df: pd.DataFrame, key: str, offset: int, limit: int) -> list[TourSpotEmbedding]:
    GOOGLE_API_KEY = Secret.load('google-api-key').get()
    client = genai.Client(api_key=GOOGLE_API_KEY)

    sliced = df.iloc[offset:offset+limit][['id', 'description']]
    filtered = sliced.dropna(subset=['description']).loc[lambda d: d['description'].str.strip() != '']

    tourspot_ids = filtered['id'].to_list()
    texts = filtered['description'].to_list()

    if not texts:
        return Completed(
            message=f'관광지 임베딩 생성하기(KEY={key}, OFFSET={offset}, LIMIT={limit}) 완료.',
            data=[]
        )

    rate_limit('google-api-rate-limit')
    result = client.models.embed_content(
        model='gemini-embedding-001',
        contents=texts,
        config=types.EmbedContentConfig(
            task_type='SEMANTIC_SIMILARITY',
            output_dimensionality=1536
        )
    ).embeddings

    tourspot_embeddings = []
    for tourspot_id, embedding in zip(tourspot_ids, result):
        tourspot_embeddings.append(TourSpotEmbedding(id=tourspot_id, embedding=embedding.values))
    
    return Completed(
        message=f'관광지 임베딩 생성하기(KEY={key}, OFFSET={offset}, LIMIT={limit}) 완료.',
        data=tourspot_embeddings
    )

@flow
def embed_all_tourspots():
    if os.path.exists('./assets/tourspot_embeddings.csv'):
        return Completed(message='이미 tourspot_embeddings.csv 파일이 존재합니다.', name='Skipped')
    
    logger = get_run_logger()

    tourspots_df = pd.read_csv('./assets/tourspots_full_transformed.csv')
    updated_at = datetime.datetime.fromtimestamp(os.path.getmtime('./assets/tourspots_full_transformed.csv'))
    
    tourspot_embeddings = list()

    total_count = len(tourspots_df)
    limit = 100
    logger.info('총 관광지 개수=%s', total_count)

    for offset in range(0, total_count, limit):
        embeddings = embed_tourspots(tourspots_df, updated_at.strftime("%Y%m%d-%H%M%S"), offset, limit, return_state=True).result()
        
        tourspot_embeddings += list(map(lambda t: t.model_dump(), embeddings))
    
    logger.info('임베딩 개수=%s', len(tourspot_embeddings))
    embeddings_df = pd.DataFrame(tourspot_embeddings)
    embeddings_df.drop_duplicates(subset=['id'], inplace=True)
    embeddings_df.to_csv('./assets/tourspot_embeddings.csv', encoding='utf-8-sig', index=False)
    logger.info('tourspot_embeddings.csv 파일 생성됨.')

@task
async def load_data(append: bool):
    logger = get_run_logger()
    df = pd.read_csv('./assets/tourspot_embeddings.csv')

    tourspot_embeddings: list[TourSpotEmbedding] = []
    for _, row in df.iterrows():
        tourspot_embeddings.append(TourSpotEmbedding(
            id=row['id'] if not pd.isna(row['id']) else None, 
            embedding=json.loads(row['embedding']) if not pd.isna(row['embedding']) else None
        ))

    host=await Variable.get('embeddb-host')
    port=await Variable.get('embeddb-port')
    username=await Variable.get('embeddb-username')
    password=(await Secret.aload('embeddb-password')).get()
    database=await Variable.get('embeddb-database')

    with MongoClient(f'mongodb://{username}:{password}@{host}:{port}/?authSource={database}') as client:
        db = client[database]
        tourspot_collection = db['tourspot']

        with client.start_session() as session:
            with session.start_transaction():
                if not append:
                    tourspot_collection.delete_many({})
                    logger.info('DB 관광지 임베딩 데이터 완전 삭제 완료.')
                
                batch_size = 200
                for offset in range(0, len(tourspot_embeddings), batch_size):
                    tourspot_collection.insert_many([{'_id': str(ts_em.id), 'text_embedding': ts_em.embedding} for ts_em in tourspot_embeddings[offset:offset+batch_size]])
        
        logger.info('DB 관광지 임베딩 데이터 삽입 완료.')
    

class LoadDataForm(BaseModel):
    load_to_db: bool
    db_append: bool

@flow
async def tourspot_embed_flow():
    embed_all_tourspots()

    form = await pause_flow_run(wait_for_input=LoadDataForm)
    if not form.load_to_db:
        return
    
    await load_data(append=form.db_append)

if __name__ == '__main__':
    tourspot_embed_flow.serve(name='관광지 데이터 임베딩')