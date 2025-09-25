from mysql.connector.abstracts import MySQLConnectionAbstract
from mysql.connector.pooling import PooledMySQLConnection
import json
from tourspot_model import TourSpot

select_tourspot_sql = 'SELECT * FROM tour_spot'
insert_tourspot_sql = 'INSERT INTO tour_spot (id,name,description,image_url,full_address,address1,address2,province_code,district_code,phone_number,lat,lng,tags) VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)'
insert_tourspot_tag_sql = 'INSERT INTO tour_spot_tag (tour_spot_id, tag) VALUES (%s, %s)'
delete_tourspot_sql = 'DELETE FROM tour_spot'
delete_tourspot_tag_sql = 'DELETE FROM tour_spot_tag'
delete_tourspot_review_like_sql = 'DELETE FROM tour_spot_review_like'
delete_tourspot_review_sql = 'DELETE FROM tour_spot_review'
delete_bookmark_sql = 'DELETE FROM bookmark'

def select_tourspot_by_ids_sql(num):
    query_string = 'SELECT * FROM tour_spot WHERE id IN ('
    for i in range(num):
        query_string += '%s'
        if i != num-1:
            query_string += ','
    query_string += ')'
    return query_string


def delete_tourspots(conn: PooledMySQLConnection | MySQLConnectionAbstract):
    if not conn.is_connected():
        raise Exception('DB 커넥션이 만료되었습니다.')

    try:
        with conn.cursor() as cursor:
            cursor.execute(delete_tourspot_tag_sql)
            cursor.execute(delete_bookmark_sql)
            cursor.execute(delete_tourspot_review_like_sql)
            cursor.execute(delete_tourspot_review_sql)
            cursor.execute(delete_tourspot_sql)
        conn.commit()
    except Exception as e:
        conn.rollback()
        raise e

def insert_tourspots_from_dataframe(conn: PooledMySQLConnection | MySQLConnectionAbstract, tourspots: list[TourSpot]):
    if not conn.is_connected():
        raise Exception('DB 커넥션이 만료되었습니다.')

    try:
        with conn.cursor(dictionary=True) as cursor:
            batch_size = 10
            for offset in range(0, len(tourspots), batch_size):
                tourspot_batch = tourspots[offset:offset+batch_size]
                id_batch = tuple(map(lambda t: t.id, tourspot_batch))
                cursor.execute(select_tourspot_by_ids_sql(len(id_batch)), id_batch)
                exist_ids = map(lambda t: t['id'], cursor.fetchall())

                new_tourspots = list(filter(lambda t: t.id not in exist_ids, tourspot_batch))
                
                new_tourspots = list(map(lambda t: (
                    t.id,
                    t.name,
                    t.description,
                    t.image_url,
                    t.full_address,
                    t.address1,
                    t.address2,
                    t.province_code,
                    t.district_code,
                    t.phone_number,
                    t.lat,
                    t.lng,
                    t.tags
                ), new_tourspots))
                
                cursor.executemany(insert_tourspot_sql, new_tourspots)
        conn.commit()
    except Exception as e:
        conn.rollback()
        raise e

def insert_tourspot_tags(conn: PooledMySQLConnection | MySQLConnectionAbstract):
    if not conn.is_connected():
        raise Exception('DB 커넥션이 만료되었습니다.')
    
    try:
        with conn.cursor(dictionary=True) as cursor:
            cursor.execute(delete_tourspot_tag_sql)
            cursor.execute(select_tourspot_sql)
            tourspots = cursor.fetchall()

            data = []
            for tourspot in tourspots:
                for tag in json.loads(tourspot['tags']):
                    data.append((tourspot['id'], tag))

            batch_size = 1000
            for i in range(0, len(data), batch_size):
                cursor.executemany(insert_tourspot_tag_sql, data[i:i+batch_size])
        conn.commit()
    except Exception as e:
        conn.rollback()
        raise e