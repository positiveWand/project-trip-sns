from pydantic import BaseModel

class TourSpot(BaseModel):
    _id: str
    text_embedding: list[float]