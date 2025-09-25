import requests
import math
from tourspot_model import RawTourSpot, RawTourSpotDetail

def fetch_tourspots(api_key: str, lat: float, lng: float, content_type: int, radius=20000) -> tuple[list[RawTourSpot], int]:
    try:
        URL = 'http://apis.data.go.kr/B551011/KorService2/locationBasedList2'
        response = requests.get(url=URL, params={
            'MobileOS': 'ETC',
            'MobileApp': 'TEST',
            '_type': 'json',
            'serviceKey': api_key,
            'contentTypeId': content_type,
            'mapX': lng,
            'mapY': lat,
            'radius': radius
        })
    except requests.exceptions.RequestException as e:
        raise Exception('요청 중 실패 발생') from e
    
    try:
        data = response.json()
        result_code = data['response']['header']['resultCode']
        result_msg = data['response']['header']['resultMsg']
        if result_code != '0000':
            raise Exception(result_code + '-' + result_msg)

        page_size = 500
        total_count = data['response']['body']['totalCount']
        total_page = math.ceil(total_count / page_size)

        items = []

        # 페이지는 1부터 시작
        for page in range(1, total_page+1):
            response = requests.get(url=URL, params={
                'numOfRows': page_size,
                'pageNo': page,
                'MobileOS': 'ETC',
                'MobileApp': 'TEST',
                '_type': 'json',
                'serviceKey': api_key,
                'contentTypeId': content_type,
                'mapX': lng,
                'mapY': lat,
                'radius': radius
            })
            data = response.json()
            result_code = data['response']['header']['resultCode']
            result_msg = data['response']['header']['resultMsg']
            if result_code != '0000':
                raise Exception(result_code + '-' + result_msg)

            items += list(map(lambda item: RawTourSpot(**item), data['response']['body']['items']['item']))
    except Exception as e:
        raise Exception('응답 처리 중 실패 발생', '응답 메시지: ' + response.content.decode()) from e
        
    return (items, total_count)


def fetch_tourspot_detail(api_key: str, content_id: int | str):
    # time.sleep(0.05)
    # return RawTourSpotDetail(**{
    #                     "contentid": str(content_id),
    #                     "contenttypeid": "12",
    #                     "title": "동촌유원지",
    #                     "createdtime": "20031105090000",
    #                     "modifiedtime": "20250425092225",
    #                     "tel": "",
    #                     "telname": "",
    #                     "homepage": "<a href=\"https://tour.daegu.go.kr/index.do?menu_id=00002942&menu_link=/front/tour/tourMapsView.do?tourId=KOATTR_115\" target=\"_blank\" title=\"새창 : 홈페이지로 이동\">https://tour.daegu.go.kr</a>",
    #                     "firstimage": "http://tong.visitkorea.or.kr/cms/resource/86/3488286_image2_1.JPG",
    #                     "firstimage2": "http://tong.visitkorea.or.kr/cms/resource/86/3488286_image3_1.JPG",
    #                     "cpyrhtDivCd": "Type3",
    #                     "areacode": "4",
    #                     "sigungucode": "4",
    #                     "lDongRegnCd": "27",
    #                     "lDongSignguCd": "140",
    #                     "lclsSystm1": "VE",
    #                     "lclsSystm2": "VE03",
    #                     "lclsSystm3": "VE030500",
    #                     "cat1": "A02",
    #                     "cat2": "A0202",
    #                     "cat3": "A02020700",
    #                     "addr1": "대구광역시 동구 효목동",
    #                     "addr2": "산 234-29",
    #                     "zipcode": "41179",
    #                     "mapx": "128.6506352387",
    #                     "mapy": "35.8826195757",
    #                     "mlevel": "6",
    #                     "overview": "동촌유원지는 대구시 동쪽 금호강변에 있는 44만 평의 유원지로 오래전부터 대구 시민이 즐겨 찾는 곳이다. 각종 위락시설이 잘 갖춰져 있으며, 드라이브를 즐길 수 있는 도로가 건설되어 있다. 수량이 많은 금호강에는 조교가 가설되어 있고, 우아한 다리 이름을 가진 아양교가 걸쳐 있다. 금호강(琴湖江)을 끼고 있어 예로부터 봄에는 그네뛰기, 봉숭아꽃 구경, 여름에는 수영과 보트 놀이, 가을에는 밤 줍기 등 즐길 거리가 많은 곳이다. 또한, 해맞이다리, 유선장, 체육시설, 실내 롤러스케이트장 등 다양한 즐길 거리가 있어 여행의 재미를 더해준다."
    #                 })
    try:
        URL = 'http://apis.data.go.kr/B551011/KorService2/detailCommon2'
        response = requests.get(url=URL, params={
            'MobileOS': 'ETC',
            'MobileApp': 'TEST',
            '_type': 'json',
            'serviceKey': api_key,
            'contentId': content_id,
        })
    except requests.exceptions.RequestException as e:
        raise Exception('요청 중 실패 발생') from e
    
    try:
        data = response.json()

        result_code = data['response']['header']['resultCode']
        result_msg = data['response']['header']['resultMsg']
        if result_code != '0000':
            raise Exception(result_code + '-' + result_msg)
        
        return RawTourSpotDetail(**data['response']['body']['items']['item'][0])
    except Exception as e:
        raise Exception('응답 처리 중 실패 발생', '응답 메시지: ' + response.content.decode()) from e