from pydantic import BaseModel, ConfigDict

from .event import Event

class UserTourspotData(BaseModel):
    user_id: str
    tourspot_id: str

class UserTourspotEvent(Event):
    data: UserTourspotData
    model_config = ConfigDict(from_attributes=True)