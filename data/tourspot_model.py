from pydantic import BaseModel

class RawTourSpot(BaseModel):
    contentid: str
    contenttypeid: str
    title: str
    createdtime: str
    modifiedtime: str
    tel: str
    addr1: str
    addr2: str
    zipcode: str
    areacode: str
    cat1: str
    cat2: str
    cat3: str
    dist: str
    firstimage: str
    firstimage2: str
    cpyrhtDivCd: str
    mapx: float
    mapy: float
    mlevel: str
    sigungucode: str
    lDongRegnCd: str
    lclsSystm1: str
    lclsSystm2: str
    lclsSystm3: str
    
class RawTourSpotDetail(BaseModel):
    contentid: str
    contenttypeid: str
    title: str
    createdtime: str
    modifiedtime: str
    tel: str
    telname: str
    homepage: str
    firstimage: str
    firstimage2: str
    cpyrhtDivCd: str
    areacode: str
    sigungucode: str
    lDongRegnCd: str
    lDongSignguCd: str
    lclsSystm1: str
    lclsSystm2: str
    lclsSystm3: str
    cat1: str
    cat2: str
    cat3: str
    addr1: str
    addr2: str
    zipcode: str
    mapx: float
    mapy: float
    mlevel: str
    overview: str

class TourSpot(BaseModel):
    id: int
    name: str
    description: str
    image_url: str | None
    full_address: str
    address1: str
    address2: str | None
    province_code: float | None
    district_code: float | None
    phone_number: str | None
    lat: float
    lng: float
    tags: str

class TourSpotEmbedding(BaseModel):
    id: int
    embedding: list[float]