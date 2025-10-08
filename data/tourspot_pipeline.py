from prefect import flow, task
from prefect.states import Completed
from prefect.blocks.system import Secret
from prefect.variables import Variable
from prefect.logging import get_run_logger
import os
import datetime
import pandas as pd
import mysql.connector
import json
from pydantic import BaseModel
from prefect.flow_runs import pause_flow_run

import tourspot_data_fetch
import tourspot_data_load
from tourspot_model import *

@task
def fetch_tourspots(content_type: int | str, location: tuple[float, float]) -> list[RawTourSpot]:
    KTO_API_KEY = Secret.load('kto-api-key').get()

    fetched, fetched_count =  tourspot_data_fetch.fetch_tourspots(
        api_key=KTO_API_KEY, 
        content_type=content_type, 
        lat=location[0], 
        lng=location[1]
    )

    return Completed(
        message=f'관광지 기본 정보((content_type={content_type}, location={location})) {fetched_count}개 가져오기 완료.',
        data=fetched
    )


@task(
    persist_result=True,
    cache_key_fn=lambda _, params: f'TOURSPOT_DETAIL(KEY={params['key']},OFFSET={params['offset']},LIMIT={params['limit']})',
    result_serializer='json'
)
def fetch_tourspot_details(df: pd.DataFrame, key: str, offset: int, limit: int) -> list[RawTourSpotDetail]:
    KTO_API_KEY = Secret.load('kto-api-key').get()

    tourspot_details = []
    failed_tourspot_details = []
    for content_id in df.iloc[offset:offset+limit]['contentid']:
        try:
            detail = tourspot_data_fetch.fetch_tourspot_detail(
                api_key=KTO_API_KEY, 
                content_id=content_id
            )
            tourspot_details.append(detail)
        except Exception as e:
            logger = get_run_logger()
            logger.error('관광지 상세 정보 조회 실패(content_id=%s)', content_id, e, exc_info=e)

            failed_tourspot_details.append(content_id)
            if len(failed_tourspot_details) >= limit // 10:
                raise Exception('관광지 상세 정보 실패 임계를 초과했습니다.')

    return Completed(
        message=f'관광지 상세 정보 가져오기(KEY={key}, OFFSET={offset}, LIMIT={limit}) 완료.',
        data=tourspot_details
    )

@flow
def fetch_all_tourspot_details():
    if os.path.exists('./assets/tourspot_details.csv'):
        return Completed(message='이미 tourspot_details.csv 파일이 존재합니다.', name='Skipped')

    logger = get_run_logger()
    tourspots_df = pd.read_csv('./assets/tourspots.csv')
    updated_at = datetime.datetime.fromtimestamp(os.path.getmtime('./assets/tourspots.csv'))

    tourspot_details = list()

    total_count = len(tourspots_df)
    limit = 200
    logger.info('총 관광지 개수=%s', total_count)

    for offset in range(0, total_count, limit):
        fetched_details = fetch_tourspot_details(tourspots_df, updated_at.strftime("%Y%m%d-%H%M%S"), offset, limit, return_state=True).result()
        
        tourspot_details += list(map(lambda t: t.model_dump(), fetched_details))
    
    details_df = pd.DataFrame(tourspot_details)
    details_df.drop_duplicates(subset=['contentid'], inplace=True)
    details_df.to_csv('./assets/tourspot_details.csv', encoding='utf-8-sig', index=False)
    logger.info('tourspot_details.csv 파일 생성됨.')


@flow
def fetch_all_tourspots(content_types: list[int], locations: list[tuple[float, float]]):
    if os.path.exists('./assets/tourspots.csv'):
        return Completed(message='이미 tourspots.csv 파일이 존재합니다.', name='Skipped')
    
    logger = get_run_logger()
    
    tourspots = list()
    for content_type in content_types:
        for location in locations:
            fetched_tourspots = fetch_tourspots(content_type, location, return_state=True).result()
            tourspots += list(map(lambda t: t.model_dump(), fetched_tourspots))

    df = pd.DataFrame(tourspots)
    df.sort_values(by='contentid', key=lambda col: col.astype(int), ascending=True, inplace=True)

    os.makedirs("./assets", exist_ok=True)
    df.to_csv('./assets/tourspots.csv', encoding='utf-8-sig', index=False)
    logger.info('tourspots.csv 파일 생성됨.')

@task
def join_tourspot_data():
    if os.path.exists('./assets/tourspots_full.csv'):
        return Completed(message='이미 tourspots_full.csv 파일이 존재합니다.', name='Skipped')

    logger = get_run_logger()

    tourspots = pd.read_csv('./assets/tourspots.csv')
    tourspot_details = pd.read_csv('./assets/tourspot_details.csv')

    merged = tourspots.merge(tourspot_details.loc[:,['contentid', 'overview']], on='contentid')
    
    merged.drop_duplicates(subset=['contentid'], inplace=True)
    merged.to_csv('./assets/tourspots_full.csv', encoding='utf-8-sig')
    logger.info('tourspots_full.csv 파일 생성됨.')

def extract_tags(row):
    if row['cat1'] == 'A01':
        return json.dumps(['NATURE'], ensure_ascii=False)
    elif row['cat2'] == 'A0201':
        return json.dumps(['HISTORY'], ensure_ascii=False)
    elif row['cat2'] == 'A0202':
        return json.dumps(['REST'], ensure_ascii=False)
    elif row['cat2'] == 'A0203':
        return json.dumps(['EXPERIENCE'], ensure_ascii=False)
    elif row['cat2'] == 'A0204':
        return json.dumps(['INDUSTRY'], ensure_ascii=False)
    elif row['cat2'] == 'A0205':
        return json.dumps(['ARCHITECTURE'], ensure_ascii=False)
    elif row['cat2'] == 'A0206':
        return json.dumps(['CULTURE'], ensure_ascii=False)
    elif row['cat2'] == 'A0207':
        return json.dumps(['FESTIVAL'], ensure_ascii=False)
    elif row['cat2'] == 'A0208':
        return json.dumps(['CONCERT'], ensure_ascii=False)
    else:
        return json.dumps(['ETC'], ensure_ascii=False)

def calc_full_address(row):
    if pd.isna(row['addr1']):
        return ''
    elif pd.isna(row['addr2']):
        return row['addr1']
    else:
        return row['addr1'] + ', ' + row['addr2']

@task
def transform_tourspot_data():
    logger = get_run_logger()
    df = pd.read_csv('./assets/tourspots_full.csv')

    transformed = pd.DataFrame()
    transformed['id'] = df['contentid']
    transformed['name'] = df['title']
    transformed['description'] = df['overview']
    transformed['image_url'] = df['firstimage']
    transformed['full_address'] = df.apply(calc_full_address, axis=1)
    transformed['address1'] = df['addr1']
    transformed['address2'] = df['addr2']
    transformed['province_code'] = df['areacode']
    transformed['district_code'] = df['sigungucode']
    transformed['phone_number'] = df['tel']
    transformed['lat'] = df['mapy']
    transformed['lng'] = df['mapx']
    transformed['tags'] = df.apply(extract_tags, axis=1)

    transformed = transformed.dropna(subset=['id', 'name', 'full_address', 'address1', 'province_code', 'district_code', 'lat', 'lng', 'tags'])

    transformed.to_csv('./assets/tourspots_full_transformed.csv', encoding='utf-8-sig', index=False)
    logger.info('tourspots_full_transformed.csv 파일 생성됨.')

@task
def load_data(host: str, port: int, user: str, password: str, database: str, append: bool):
    logger = get_run_logger()
    df = pd.read_csv('./assets/tourspots_full_transformed.csv')

    tourspots = []
    for _, row in df.iterrows():
        tourspots.append(TourSpot(
            id=row['id'] if not pd.isna(row['id']) else None, 
            name=row['name'] if not pd.isna(row['name']) else None,
            description=row['description'] if not pd.isna(row['description']) else None,
            image_url=row['image_url'] if not pd.isna(row['image_url']) else None,  
            full_address=row['full_address'] if not pd.isna(row['full_address']) else None, 
            address1=row['address1'] if not pd.isna(row['address1']) else None, 
            address2=row['address2'] if not pd.isna(row['address2']) else None, 
            province_code=row['province_code'] if not pd.isna(row['province_code']) else None, 
            district_code=row['district_code'] if not pd.isna(row['district_code']) else None, 
            phone_number=row['phone_number'] if not pd.isna(row['phone_number']) else None,
            lat=row['lat'] if not pd.isna(row['lat']) else None, 
            lng=row['lng'] if not pd.isna(row['lng']) else None, 
            tags=row['tags'] if not pd.isna(row['tags']) else None
        ))

    with mysql.connector.connect(
        host=host,
        port=port,
        user=user,
        password=password,
        database=database
    ) as conn:
        if not append:
            tourspot_data_load.delete_tourspots(conn)
            logger.info('DB 관광지 데이터 완전 삭제 완료.')
        tourspot_data_load.insert_tourspots_from_dataframe(conn, tourspots)
        logger.info('DB 관광지 데이터 추가 완료.')
        tourspot_data_load.insert_tourspot_tags(conn)
        logger.info('DB 관광지 태그 데이터 추가 완료.')

class LoadDataForm(BaseModel):
    load_to_db: bool
    db_append: bool

@flow
async def pipe_tourspot(content_types: list[int], locations: list[tuple[float, float]]):
    fetch_all_tourspots(content_types=content_types, locations=locations)
        
    fetch_all_tourspot_details()

    join_tourspot_data()
    transform_tourspot_data()

    form = await pause_flow_run(wait_for_input=LoadDataForm)
    if not form.load_to_db:
        return

    load_data(
        host=await Variable.get('db-host'),
        port=await Variable.get('db-port'),
        user=await Variable.get('db-username'),
        password=(await Secret.aload('db-password')).get(),
        database=await Variable.get('db-database'),
        append=form.db_append
    )