from pydantic import BaseModel

class TrendItem(BaseModel):
    item_id: str
    trend_score: float