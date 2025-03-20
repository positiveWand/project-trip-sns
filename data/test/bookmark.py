from mysql.connector.abstracts import MySQLConnectionAbstract
from mysql.connector.pooling import PooledMySQLConnection
import random

select_tourspot_sql = 'SELECT * FROM tour_spot'
select_user_sql = 'SELECT * FROM user'
delete_bookmark_sql = 'DELETE FROM bookmark WHERE user_id=%s'
delete_all_bookmark_sql = 'DELETE FROM bookmark'
insert_bookmark_sql = 'INSERT INTO bookmark (user_id, tour_spot_id) VALUES (%s, %s)'

def insert_bookmark(
        conn: PooledMySQLConnection | MySQLConnectionAbstract,
        target_user,
        num
    ):
    print(f"[INFO] {num}개의 테스트 북마크 데이터를 DB에 삽입합니다.")

    try:
        with conn.cursor(dictionary=True) as cursor:
            cursor.execute(delete_bookmark_sql, (target_user,))
            print(f"[INFO] {cursor.rowcount}개의 기존 데이터가 삭제되었습니다.")

            cursor.execute(select_tourspot_sql)
            tourspots = cursor.fetchall()
            
            print(f"[INFO] 북마크할 관광지 선정")
            tourspots = random.sample(tourspots, num)

            data = []
            for tourspot in tourspots:
                data.append((target_user, tourspot['id']))
            
            for i in range(0, len(data), 1000):
                cursor.executemany(insert_bookmark_sql, data[i:i+1000])
                print(f"[INFO] {cursor.rowcount}개의 더미 데이터가 삽입되었습니다.")

            conn.commit()
    except Exception as err:
        print(f"Error: {err}")

def insert_random_bookmark(
        conn: PooledMySQLConnection | MySQLConnectionAbstract,
        tourspot_num,
        multi
    ):
    print(f"[INFO] {tourspot_num * multi}개의 테스트 북마크 데이터를 DB에 삽입합니다.")

    try:
        with conn.cursor(dictionary=True) as cursor:
            cursor.execute(delete_all_bookmark_sql)
            print(f"[INFO] {cursor.rowcount}개의 기존 데이터가 삭제되었습니다.")

            cursor.execute(select_tourspot_sql)
            tourspots = cursor.fetchall()

            cursor.execute(select_user_sql)
            users = cursor.fetchall()
            
            print(f"[INFO] 북마크할 관광지 선정")
            tourspots = random.sample(tourspots, tourspot_num)

            data = []
            for tourspot in tourspots:
                tmp_users = random.sample(users, multi)
                for user in tmp_users:
                    data.append((user['id'], tourspot['id']))
            

            for i in range(0, len(data), 10000):
                cursor.executemany(insert_bookmark_sql, data[i:i+10000])
                print(f"[INFO] {cursor.rowcount}개의 더미 데이터가 삽입되었습니다.")

            conn.commit()
    except Exception as err:
        print(f"Error: {err}")