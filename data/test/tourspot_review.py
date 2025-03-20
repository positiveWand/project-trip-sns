from mysql.connector.abstracts import MySQLConnectionAbstract
from mysql.connector.pooling import PooledMySQLConnection
import random
from datetime import datetime, timedelta
import faker

select_tourspot_sql = 'SELECT * FROM tour_spot'
select_user_sql = 'SELECT * FROM user'
delete_all_tourspot_review_sql = 'DELETE FROM tour_spot_review'
delete_tourspot_review_sql = 'DELETE FROM tour_spot_review WHERE id=%s'
insert_tourspot_review_sql = "INSERT INTO tour_spot_review (user_id, tour_spot_id, content, created_at, likes) VALUES (%s, %s, %s, %s, %s)"

def insert_tourspot_review(
        conn: PooledMySQLConnection | MySQLConnectionAbstract,
        target_tourspot,
        num
    ):
    print(f"[INFO] {num}개의 테스트 관광지 후기 데이터를 DB에 삽입합니다.")

    fake = faker.Faker('ko_KR')

    try:
        with conn.cursor(dictionary=True) as cursor:
            cursor.execute(delete_tourspot_review_sql, (target_tourspot,))
            print(f"[INFO] {cursor.rowcount}개의 기존 데이터가 삭제되었습니다.")

            cursor.execute(select_user_sql)

            users = cursor.fetchall()
            
            print(f"[INFO] 후기를 남길 회원 선정")
            users = random.sample(users, num)

            data = []
            for user in users:
                content = fake.sentence(20)
                created_at = fake.date_time_between_dates(datetime.now() - timedelta(days=30), datetime.now())
                data.append((
                    user['id'],
                    target_tourspot,
                    content,
                    created_at.strftime("%Y-%m-%d %H:%M:%S"),
                    0
                ))
            for i in range(0, len(data), 10000):
                cursor.executemany(insert_tourspot_review_sql, data[i:i+10000])
                print(f"[INFO] {cursor.rowcount}개의 더미 데이터가 삽입되었습니다.")

            conn.commit()
    except Exception as err:
        print(f"Error: {err}")
            
def insert_random_tourspot_review(
        conn: PooledMySQLConnection | MySQLConnectionAbstract,
        user_num,
        multi
    ):
    print(f"[INFO] {user_num * multi}개의 테스트 관광지 후기 데이터를 DB에 삽입합니다.")

    fake = faker.Faker('ko_KR')

    try:
        with conn.cursor(dictionary=True) as cursor:
            cursor.execute(delete_all_tourspot_review_sql)
            print(f"[INFO] {cursor.rowcount}개의 기존 데이터가 삭제되었습니다.")

            cursor.execute(select_user_sql)
            users = cursor.fetchall()

            cursor.execute(select_tourspot_sql)
            tourspots = cursor.fetchall()
            
            print(f"[INFO] 후기를 남길 회원 선정")
            users = random.sample(users, user_num)

            data = []
            for user in users:
                tmp_tourspots = random.sample(tourspots, multi)
                for tourspot in tmp_tourspots:
                    content = fake.sentence(20)
                    created_at = fake.date_time_between_dates(datetime.now() - timedelta(days=30), datetime.now())
                    data.append((
                        user['id'],
                        tourspot['id'],
                        content,
                        created_at.strftime("%Y-%m-%d %H:%M:%S"),
                        0
                    ))
            for i in range(0, len(data), 10000):
                cursor.executemany(insert_tourspot_review_sql, data[i:i+10000])
                print(f"[INFO] {cursor.rowcount}개의 더미 데이터가 삽입되었습니다.")

            conn.commit()
    except Exception as err:
        print(f"Error: {err}")
