from mysql.connector.abstracts import MySQLConnectionAbstract
from mysql.connector.pooling import PooledMySQLConnection
import random

select_tourspot_review = 'SELECT * FROM tour_spot_review'
select_user_sql = 'SELECT * FROM user'
delete_all_ourspot_review_like_sql = 'DELETE FROM tour_spot_review_like'
delete_tourspot_review_like_sql = 'DELETE FROM tour_spot_review_like WHERE tour_spot_review_id=%s'
insert_tourspot_review_like_sql = 'INSERT INTO tour_spot_review_like (user_id, tour_spot_review_id) VALUES (%s, %s)'

def insert_tourspot_review_like(
        conn: PooledMySQLConnection | MySQLConnectionAbstract,
        target_tourspot_review,
        num
    ):
    print(f"[INFO] {num}개의 테스트 관광지 후기 좋아요 데이터를 DB에 삽입합니다.")

    try:
        with conn.cursor(dictionary=True) as cursor:
            cursor.execute(delete_tourspot_review_like_sql, (target_tourspot_review,))
            print(f"[INFO] {cursor.rowcount}개의 기존 데이터가 삭제되었습니다.")

            cursor.execute(select_user_sql)
            users = cursor.fetchall()
            
            print(f"[INFO] 후기를 남길 회원 선정")
            users = random.sample(users, num)

            data = []
            for user in users:
                data.append((
                    user['id'],
                    target_tourspot_review
                ))
            for i in range(0, len(data), 10000):
                cursor.executemany(insert_tourspot_review_like_sql, data[i:i+10000])
                print(f"[INFO] {cursor.rowcount}개의 더미 데이터가 삽입되었습니다.")

            conn.commit()
    except Exception as err:
        print(f"Error: {err}")

def insert_random_tourspot_review_like(
        conn: PooledMySQLConnection | MySQLConnectionAbstract,
        user_num,
        multi
    ):
    print(f"[INFO] {user_num * multi}개의 테스트 관광지 후기 좋아요 데이터를 DB에 삽입합니다.")

    try:
        with conn.cursor(dictionary=True) as cursor:
            cursor.execute(delete_all_ourspot_review_like_sql)
            print(f"[INFO] {cursor.rowcount}개의 기존 데이터가 삭제되었습니다.")

            cursor.execute(select_user_sql)
            users = cursor.fetchall()

            cursor.execute(select_tourspot_review)
            tourspot_reviews = cursor.fetchall()
            
            print(f"[INFO] 후기를 남길 회원 선정")
            users = random.sample(users, user_num)

            data = []
            for user in users:
                tmp_tourspot_reviews = random.sample(tourspot_reviews, multi)
                for tourspot_review in tmp_tourspot_reviews:
                    data.append((
                        user['id'],
                        tourspot_review['id']
                    ))
            for i in range(0, len(data), 10000):
                cursor.executemany(insert_tourspot_review_like_sql, data[i:i+10000])
                print(f"[INFO] {cursor.rowcount}개의 더미 데이터가 삽입되었습니다.")

            conn.commit()
    except Exception as err:
        print(f"Error: {err}")
