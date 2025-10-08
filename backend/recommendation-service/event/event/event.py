from pydantic import BaseModel
import datetime
from typing import Any, Literal

class Event(BaseModel):
    id: str
    type: str
    data: Any
    timestamp: datetime.datetime

EventHandleResult = Literal['EXPIRED', 'DUPLICATE', 'CONSUMED']