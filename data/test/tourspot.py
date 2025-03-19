from mysql.connector.abstracts import MySQLConnectionAbstract
from mysql.connector.pooling import PooledMySQLConnection
import pandas as pd
import json
import random
import faker

select_tourspot_sql = 'SELECT * FROM tour_spot'
insert_tourspot_sql = 'INSERT INTO tour_spot (id,name,description,image_url,full_address,address1,address2,province_code,district_code,lat,lng,tags) VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)'
insert_tourspot_tag_sql = 'INSERT INTO tour_spot_tag (tour_spot_id, tag) VALUES (%s, %s)'
delete_tourspot_sql = 'DELETE FROM tour_spot'
delete_tourspot_tag_sql = 'DELETE FROM tour_spot_tag'

def insert_tourspot_from_csv(conn: PooledMySQLConnection | MySQLConnectionAbstract):
    print(f"[INFO] 관광지 데이터를 DB에 삽입합니다.")
    df = pd.read_csv('./data/tourspot_dataset.csv')

    data = []
    for index, row in df.iterrows():
        data.append((
            row['id'], 
            row['name'], 
            row['description'], 
            row['image_url'], 
            row['full_address'], 
            row['address1'], 
            row['address2'], 
            row['province_code'], 
            row['district_code'], 
            row['lat'], 
            row['lng'], 
            row['tags']
        ))
    
    try:
        with conn.cursor() as cursor:
            cursor.execute(delete_tourspot_sql)
            print(f"[INFO] {cursor.rowcount}개의 기존 데이터가 삭제되었습니다.")

            cursor.executemany(insert_tourspot_sql, data)
            print(f"[INFO] {cursor.rowcount}개의 더미 데이터가 삽입되었습니다.")

            conn.commit()
    except Exception as err:
        print(f"Error: {err}")

def insert_tourspot(
        conn: PooledMySQLConnection | MySQLConnectionAbstract, 
        num):
    print(f"[INFO] 관광지 데이터를 DB에 삽입합니다.")

    print(f"[INFO] 관광지 데이터 생성")
    fake = faker.Faker('ko_KR')
    data = []
    for i in range(num):
        data.append((
            i,
            fake.place_name(),
            fake.paragraph(5),
            fake.image_url(),
            fake.address(),
            fake.street_address(),
            fake.address_detail(),
            random.choice([1, 2, 3, 4, 5, 6, 7, 8, 31, 32, 33, 34, 35, 36, 37, 38, 39]),
            random.choice([1, 2, 3, 4, 5, 6, 7, 8, 9, 10]),
            random.uniform(37.75401923009092, 38.572892069426906),
            random.uniform(127.42648943343362, 128.08932513385972),
            random.choice([
                '["NATURE"]',
                '["HISTORY"]',
                '["REST"]',
                '["EXPERIENCE"]',
                '["INDUSTRY"]',
                '["ARCHITECTURE"]',
                '["CULTURE"]',
                '["FESTIVAL"]',
                '["CONCERT"]'
            ])
        ))
    
    try:
        with conn.cursor() as cursor:
            cursor.execute(delete_tourspot_sql)
            print(f"[INFO] {cursor.rowcount}개의 기존 데이터가 삭제되었습니다.")

            cursor.executemany(insert_tourspot_sql, data)
            print(f"[INFO] {cursor.rowcount}개의 더미 데이터가 삽입되었습니다.")

            conn.commit()
    except Exception as err:
        print(f"Error: {err}")


def insert_tourspot_tag(conn: PooledMySQLConnection | MySQLConnectionAbstract):
    print(f"[INFO] 관광지 태그 데이터를 DB에 삽입합니다.")

    try:
        with conn.cursor(dictionary=True) as cursor:
            cursor.execute(delete_tourspot_tag_sql)
            print(f"[INFO] {cursor.rowcount}개의 기존 데이터가 삭제되었습니다.")

            cursor.execute(select_tourspot_sql)
            tourspots = cursor.fetchall()

            data = []
            for tourspot in tourspots:
                for tag in json.loads(tourspot['tags']):
                    data.append((tourspot['id'], tag))

            for i in range(0, len(data), 10000):
                cursor.executemany(insert_tourspot_tag_sql, data[i:i+10000])
                print(f"[INFO] {cursor.rowcount}개의 더미 데이터가 삽입되었습니다.")
            
            conn.commit()
    except Exception as err:
        print(f"Error: {err}")