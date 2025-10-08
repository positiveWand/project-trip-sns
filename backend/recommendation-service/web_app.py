from dotenv import load_dotenv
load_dotenv('./config.env')

from fastapi import FastAPI, Query
import service.trend as trend_service
import service.personalized as personalized_service

app = FastAPI()

@app.get('/api/recommendations/trend')
async def get_trend_recommendation(
    k: int = Query(5, ge=0)
):
    topk = trend_service.get_trend_topk(k)
    return topk

@app.get('/api/recommendations/personalized')
async def get_personalized_recommendation(
    user_id: str,
    k: int = Query(5, ge=0)
):
    topk = personalized_service.get_personalized_topk(user_id, k)
    return topk