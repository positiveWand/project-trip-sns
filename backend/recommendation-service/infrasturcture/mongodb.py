import os
import pymongo

_mongo = None

def get_mongo() -> pymongo.MongoClient:
    global _mongo

    if _mongo is None:
        MONGO_HOST = os.getenv('MONGO_HOST')
        MONGO_PORT = os.getenv('MONGO_PORT')
        MONGO_USERNAME = os.getenv('MONGO_USERNAME')
        MONGO_PASSWORD = os.getenv('MONGO_PASSWORD')
        MONGO_DB = os.getenv('MONGO_DB')

        mongo_url = f'mongodb://{MONGO_USERNAME}:{MONGO_PASSWORD}@{MONGO_HOST}:{MONGO_PORT}/?authSource={MONGO_DB}'

        _mongo = pymongo.MongoClient(mongo_url)
    
    return _mongo