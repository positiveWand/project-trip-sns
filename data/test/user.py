from mysql.connector.abstracts import MySQLConnectionAbstract
from mysql.connector.pooling import PooledMySQLConnection
import faker
import bcrypt

delete_user_sql = 'DELETE FROM user'
insert_user_sql = 'INSERT INTO user (username, password, name, email) VALUES (%s, %s, %s, %s)'

def insert_user(conn: PooledMySQLConnection | MySQLConnectionAbstract, num):
    print(f"[INFO] {num}개의 테스트 회원 데이터를 DB에 삽입합니다.")

    try:
        with conn.cursor() as cursor:
            cursor.execute(delete_user_sql)
            print(f"[INFO] {cursor.rowcount}개의 기존 데이터가 삭제되었습니다.")

            print(f"[INFO] 테스트 회원 데이터 생성")
            fake = faker.Faker('ko_KR')
            salt = bcrypt.gensalt()
            data = []
            for i in range(num):
                data.append((
                    fake.user_name() + str(i),
                    fake.password(15),
                    # bcrypt.hashpw(fake.password(length=12, special_chars=True).encode('utf-8'), salt=salt),
                    fake.name(),
                    fake.email()
                ))

            for i in range(0, len(data), 10000):
                cursor.executemany(insert_user_sql, data[i:i+10000])
                print(f"[INFO] {cursor.rowcount}개의 더미 데이터가 삽입되었습니다.")

            conn.commit()
    except Exception as err:
        print(f"Error: {err}")
