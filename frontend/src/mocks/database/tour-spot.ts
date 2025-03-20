export interface TourSpot {
  id: string;
  name: string;
  address: string;
  lat: number;
  lng: number;
  imageUrl: string;
  description: string;
  phoneNumber: string;
  tags: string[];
}

export const TEST_TOUR_SPOTS: TourSpot[] = [
  {
    id: '1918643',
    name: '한대오름',
    address: '제주특별자치도 제주시 애월읍 화전길 201',
    lat: 33.34829492,
    lng: 126.385669091,
    imageUrl: '',
    description:
      '한대악이라고도 불리는 기생화산으로 이름의 유래는 전해지지 않았다. 전체적으로 완만한 경사를 갖고 있으며 비고가 낮고 2개의 봉우리가 산 정상에서 이어져 있는 형태이다. 전사면에 해송, 삼나무가 주종을 이루며 잡목이 우거지고 진달래, 꽝꽝나무, 청미래덩굴 등이 자라고 있다. 오름 서쪽 지역에는 곰취군락이 있고, 동쪽 자락에는 꽤 넓은 습원을 이루면서 주변에 물웅덩이가 많다. 한대오름은 제주 한라산 기슭 깊숙한 곳에 위치해 있으며 제주만의 풍경을 갖고 있다. 특히 단풍이 아름답기로 유명한 오름으로, 한라산국립공원의 해발 1100m 고지를 지나는 1100도로에서 이 오름까지 이르는 숲길은 제주도에서 단풍이 가장 아름다운 장소로 손꼽힌다. \
한대오름에 가려면 탐라각 휴게소에서 표고밭길을 지나 표고밭 관리사에 이르고 그곳에서 약 1km 안으로 걸어 들어가면 평평한 습지와 함께 가로누워 있는 한대오름이 나온다.',
    phoneNumber: '064-728-2742',
    tags: ['NATURE'],
  },
  {
    id: '1918421',
    name: '폭낭오름',
    address: '제주특별자치도 제주시 애월읍 봉성리',
    lat: 33.3522277635,
    lng: 126.3822415228,
    imageUrl: 'http://tong.visitkorea.or.kr/cms/resource/39/3478039_image2_1.jpg',
    description:
      '폭낭오름은 제주도 제주시 애월읍 봉성리에 있는 오름으로, 효명목장 진입로 근처에 있다. 오름의 이름은 오름에 큰 폭낭(팽나무)이 있어서 붙여졌다고 하는데, 지금은 그 나무를 찾을 수 없다고 한다. 오름의 정상부에는 하나의 말굽형 화구와 남서쪽에 두 개의 원추형 화구로 이루어진 복합형 화산체이며, 높이는 645.5m, 비고는 76m이다. 오름 전사면이 완만하면서 사면을 따라 풀밭을 이루고, 주요 식생은 산뽕나무, 분단나무, 자귀나무, 보리수나무, 꽝꽝나무 등이 있고, 오름 정상부에는 가운데가 얕게 우물져 가시덤불과 잡초가 우거져 있다.',
    phoneNumber: '',
    tags: ['NATURE'],
  },
  {
    id: '2819599',
    name: '고배기동산',
    address: '제주특별자치도 서귀포시 안덕면 광평리, 산67',
    lat: 33.3308301199,
    lng: 126.3817114923,
    imageUrl: 'http://tong.visitkorea.or.kr/cms/resource/23/2818823_image2_1.jpg',
    description:
      '서귀포 안덕면 광평리에 위치한 고배기 동산은 제주 4.3 사건 당시 사라졌던 광평리 마을을 1955년 재건하면서 한라산 아래 첫 마을 사람들에게 삶의 터를 내어준 자연에 감사하는 마음을 담아 만든 숲이다. 과거에는 ‘고백’이라는 사람이 살았다고 해서 ‘고백이 동산’이라고 불렸다고 한다. 고배기 동산 곳곳에 편안하게 휴식을 취할 수 있는 그물망 침대와 벤치도 마련되어 있다. 아무도 없는 숲 속에서 바람에 흔들리는 나뭇잎 소리, 지저귀는 새소리를 들으며 조용히 시간을 보내기 좋다. 구두를 신고도 걸을 수 있을 만큼 산책로가 잘 정비되어 있고 상대적으로 한적해서 데이트 코스로도 제격이다.',
    phoneNumber: '',
    tags: ['NATURE'],
  },
  {
    id: '1926379',
    name: '정물오름',
    address: '제주특별자치도 제주시 한림읍 금악리, 산52-1',
    lat: 33.3380257364,
    lng: 126.3302158047,
    imageUrl: 'http://tong.visitkorea.or.kr/cms/resource/89/3077589_image2_1.jpg',
    description:
      '정물오름(해발 466m)은 제주리 한림읍에 있는 말굽형 화구를 가진 기생화산이다. 오름 동남쪽에 당오름이 이웃해 있다. 오름의 형태는 남서쪽에서 다소 가파르게 솟아올라 꼭대기에서 북서쪽으로 완만하게 뻗어 내린다. 정물오름 서쪽에는 조그만 알오름이 있는데, 이를 [정물알오름]이라 한다. 오름 북서쪽 비탈 아래쪽 기슭에 [정물]이라는 쌍둥이 샘이 있는데 이 샘 이름에서 오름 이름을 따왔다. 제주도는 전국 연평균 강수량에 비해 훨씬 많은 강수량에도 불구하고 마실 물이 귀했다. 이 샘은 물이 깨끗하고 양이 많아 이곳에서 꽤 먼 곳의 중산간마을 사람들도 물을 길러다 마셨다고 한다.\
정물오름은 억새뿐 아니라 노을 명소로도 손색이 없는 곳이다. 억새가 아름다운 시기에 일몰시간에 맞춰 이곳을 방문하는 것도 가을 제주 여행 추천 코스로 손꼽아도 부족함이 없다. 오름의 동녘 자락에 있는 들판은 정물오름을 모태로 하여 예로부터 으뜸가는 목장지대로 이용되고 있으며, 오름에는 [개가 가리켜 준 옥녀금차형의 명당터]가 있다는 이야기가 전해지고 있다.',
    phoneNumber: '064-728-2742',
    tags: ['NATURE'],
  },
  {
    id: '1890152',
    name: '거린사슴',
    address: '제주특별자치도 서귀포시 1100로 823',
    lat: 33.3073171985,
    lng: 126.4543696003,
    imageUrl: 'http://tong.visitkorea.or.kr/cms/resource/97/3477397_image2_1.jpg',
    description:
      '거린사슴은 기생화산으로 분화구는 서쪽으로 벌어진 말굽형의 형태이다. 한라산 중턱에 있는 오름 중에 사슴과 관련된 이름으로 지어진 오름 중의 하나이며 실제로 사슴이 살았다고 해서 거린사슴이라 불리고 있으며, 거리다는 갈리다(갈라지다)의 옛말로 오름이 두 개의 봉우리로 갈리어져 있음을 말한다.\
1100도로가 오름을 관통하여 지나며 오름의 기슭에는 휴게소 겸 전망대가 만들어져 있다. 이 전망대는 탁 트인 풍경과 서귀포 시내를 한눈에 내려다볼 수 있다. 오름의 전사면 둘레에는 많은 나무가 자라 무성한 숲을 이루며, 동쪽에 갯거리오름이 있고 서귀포 자연휴양림이 위치한다. 거린사슴은 오름 등반, 서귀포 시내 조망, 피톤치드 가득한 숲길 산책까지 일석삼조를 얻을 수 있는 관광지이다.',
    phoneNumber: '',
    tags: ['NATURE'],
  },
  {
    id: '3026604',
    name: '효명사',
    address: '제주특별자치도 서귀포시 516로 815-41 효명사',
    lat: 33.3233287582,
    lng: 126.5941765584,
    imageUrl: 'http://tong.visitkorea.or.kr/cms/resource/93/3026593_image2_1.jpg',
    description:
      '효명사는 서귀포시 남원읍에 위치한 울창한 숲 속에 아담한 집처럼 보이는 소박한 법당이다. 네비게이션에 효명사를 찍고 큰 도로를 타고 가다가 효명사 표지판이 있어 쉽게 찾아 갈 수 있지만 큰 도로에서 외길로 나 있는 길을 따라 가다 보면 구름다리처럼 생긴 산신각이 보인다. 계단을 올라가면 조그만 공간에 한라산을 볼 수 있게 통창이 설치되어 있다. 산신각을 통과하여 마주한 효명사는 여느 절과 달리 조그마한 건물 서너 채만 있어서 약간은 생경한 느낌을 준다.',
    phoneNumber: '',
    tags: ['HISTORY'],
  },
  {
    id: '1621165',
    name: '정의현성',
    address: '제주특별자치도 서귀포시 표선면 성읍정의현로 104',
    lat: 33.3916995221,
    lng: 126.8013237169,
    imageUrl: 'http://tong.visitkorea.or.kr/cms/resource/56/1618156_image2_1.jpg',
    description:
      '서귀포시 표선면에 있는 조선 시대의 성곽이다. 본래 이 성은 지금의 성산읍 고성리에 있었지만, 우도와 가까워서 새벽과 밤에 북과 나발 소리가 들리고 여러 번 태풍으로 흉년이 들었을 뿐만 아니라 왜적이 침범했기 때문에 세종 5년(1423) 9월에 안무사 정간이 진사리로 이전하여 축성했다. 당시 성의 둘레는 2,520척, 높이 13척이었다. 동, 서, 남에 각각 문이 있었으며, 문 위에는 초루가 있었다. 정의현성 안에는 110호에 달하는 가옥이 있고 성 밖으로도 많은 가옥이 있었으며 관아 문밖에 12개의 돌하르방을 세워 성안으로 출입하는 사람들을 감시하고 성을 지키는 수문장 역할을 했다.',
    phoneNumber: '',
    tags: ['HISTORY'],
  },
  {
    id: '3008007',
    name: '무민랜드 제주',
    address: '제주특별자치도 서귀포시 병악로 420',
    lat: 33.3070133076,
    lng: 126.3817036921,
    imageUrl: 'http://tong.visitkorea.or.kr/cms/resource/88/3007988_image2_1.jpg',
    description:
      '무민은 핀란드 국민 작가 토베 얀손의 작품으로 75년이 넘도록 많은 사랑을 받는 캐릭터이다. 무민랜드 제주는 무민이야기가 예술로 살아 숨쉬는 감성 공간으로 무민하우스동과 무민라운지 동으로 2개의 공간이 있다.\
무민하우스동은 지하 1층부터 4층까지이고 지하 1층이 입구이며 기프트샵과 웰컴포토존이 있다. 1층은 토베얀손 히스토리, 무민 캐릭터소개, 무민하우스 전시공간이다. 2층은 영상관, 3층은 미디어 아트와 체험존으로 매직볼풀장과 무민 3D MR 체험존이 있으며 4층은 무민정원이다.\
무민라운지 동은 1층에는 무민 기프트 샵과 힐링파크 2층에는 북카페가 있다.\
파스텔톤의 공간 구성이 무민의 다양한 캐릭터와 함께 어디서 사진을 찍더라도 포토존이다. 실내로 이루어져 비가 오거나 날이 궂을 때 방문하기 좋고, 카페만 이용하는 것도 가능하다. \
가까운 거리에 건축물로 유명한 방주교회와 본태박물관이 있다.',
    phoneNumber: '',
    tags: ['REST'],
  },
  {
    id: '2661514',
    name: '휴림',
    address: '제주특별자치도 제주시 애월읍 광령남서길 40',
    lat: 33.4438100372,
    lng: 126.4287993085,
    imageUrl: 'http://tong.visitkorea.or.kr/cms/resource/64/3017664_image2_1.jpg',
    description:
      '휴림은 2017년 개장한 에코힐링파크로 남녀노소 온가족이 함께 즐길 수 있는 숲속 체험을 제공한다. 제주시산림조합에서 운영하며 산림자원의 새로운 부가가치를 창출하기 위해 제주의 산림자원을 활용하여 조성되어있어 제주의 숲내음을 온몸으로 맡으며 휴식을 즐길 수 있다.\
양묘체험, 임산물 생산체험, 숲속캠핑, 글램핑, 카라반 등을 체험할 수 있으며, 유치원 등의 단체가 이용할 수 있는 유아숲속 놀이터를 운영하고 있다.',
    phoneNumber: '',
    tags: ['REST'],
  },
  {
    id: '2853484',
    name: '삼무공원',
    address: '제주특별자치도 제주시 신대로10길 48-9',
    lat: 33.4914863265,
    lng: 126.4924649191,
    imageUrl: 'http://tong.visitkorea.or.kr/cms/resource/77/2853477_image2_1.JPG',
    description:
      '삼무공원은 제주 시내 연동에 있다. 1978년 도시 근린공원으로 조성하였으며 삼무의 의미는 제주에 도둑, 대문, 거지가 없다는 삼무에서 따 온 이름이다. 삼무공원은 베두리오름에 조성된 공원으로, 베두리오름은 높이 85m, 폭 240m, 둘레 617m의 작은 언덕으로 삼무공원이 곧 베두리오름이다. 공원에는 증기기관차가 있는데, 1978년 박정희 대통령이 섬에 사는 아이들에게 기차를 보여주려고 사용이 중단된 기차를 흑산도와 제주도에 보낸 것이다. 현재는 제주도에만 미카형 증기기관차 304호가 남아있다. 연동 주민들이 산책과 운동을 즐기는 공원으로 봄에는 능수 벚꽃이 아름다워 여행자들이 찾아온다.',
    phoneNumber: '',
    tags: ['REST'],
  },
  {
    id: '2763820',
    name: '아날로그감귤밭',
    address: '제주특별자치도 제주시 해안마을8길 46',
    lat: 33.4557007379,
    lng: 126.4508804542,
    imageUrl: 'http://tong.visitkorea.or.kr/cms/resource/39/3397039_image2_1.jpg',
    description:
      '아날로그감귤밭은 그 이름처럼 아날로그 감성으로 가득 채워진 감귤 체험장 & 카페다. 귤이 나는 철에는 농약을 치지 않은 귤 밭에서 직접 귤을 따는 체험을 해볼수 있다. 귤 밭 곳곳에 꾸며진 포토 스팟은 제주에서의 감귤 체험을 특별한 감성으로 채울 수 있게 공간을 선물한다. 유기농 음료를 마실 수 있는 카페의 내부 공간도 카메라 셔터를 유발할 만큼 깔끔하고 감각 있게 꾸며져 있다. 애견 동반과 외부 음식은 반입이 금지된다.',
    phoneNumber: '',
    tags: ['EXPERIENCE'],
  },
  {
    id: '2704353',
    name: '신비의도로',
    address: '제주특별자치도 제주시 1100로 2894-63 (노형동)',
    lat: 33.4513184714,
    lng: 126.4875107028,
    imageUrl: 'http://tong.visitkorea.or.kr/cms/resource/75/3396575_image2_1.jpg',
    description:
      '신비의 도로 혹은 도깨비 도로로 불리는 이곳은, 내리막길에 차를 세워두면 아래로 내려가야하는 차가 내려가지 않고 오히려 오르막쪽으로 뒷걸음치는 기이한 곳이다. 사실 이는 오르막길이 보이는 쪽이 경사 3도 가량의 내리막 길이여서 단순 착시 현상에 의해 올라가는 것처럼 보이는 것이다. 이러한 현상 때문에 입소문을 타 도깨비도로는 관광 명소가 되어 점점 많은 이들이 방문을 하는 추세로 현재 이로 인한 사고를 방지하기 위해 각종 안전 시설들이 추가되고 있고 조만간 도깨비도로를 기점으로 관광 공원이 생겨날 전망이다.\
\
(출처 : 제주 문화관광 홈페이지)',
    phoneNumber: '',
    tags: ['EXPERIENCE'],
  },
  {
    id: '128150',
    name: '제주 서광다원',
    address: '제주특별자치도 서귀포시 안덕면 신화역사로 15, (오설록티뮤지엄)',
    lat: 33.3059564173,
    lng: 126.289381178,
    imageUrl: 'http://tong.visitkorea.or.kr/cms/resource/41/3024741_image2_1.jpg',
    description:
      '제주 서광다원은 서귀포시 서광리에 조성된 국내 최대 규모의 차 생산지로 서귀포의 도순다원, 한남다원과 함께 아모레퍼시픽그룹이 운영하는 3개 다원 중의 하나이다. 드넓은 평야에 펼쳐진 차밭의 면적이 660,000㎡에 이른다.\
산방산 근처에 조성되어 있는데 연평균 기온 15℃, 연 강수량 1,800㎜에 일조량이 적어서 차를 재배하기에 최적의 조건을 갖췄다. 오설록이 1983년부터 20여 년간 개간하여 현재는 우리나라에서 가장 광활한 유기농 차밭이 되었다. 차밭 주변에 우리나라 최대 규모의 차 문화 박물관인 오설록 티뮤지엄, 추사 김정희 선생의 추사유배지, 제주의 특별한 숲 곶자왈 등이 인접하고 있어 이 지역 전체가 제주의 대표적인 랜드마크로 거듭났다.\
서광 다원은 산간 지대 개발의 성공 모델로 오설록티뮤지엄과 함께 제주녹차문화의 중심이 되고 있다.',
    phoneNumber: '',
    tags: ['INDUSTRY'],
  },
  {
    id: '2704412',
    name: '아침미소목장',
    address: '제주특별자치도 제주시 첨단동길 160-20, (월평동)',
    lat: 33.4548279883,
    lng: 126.5862409359,
    imageUrl: '',
    description:
      '아침미소목장은 지난 1978년 설립되어 많은 젖소들을 건강하게 길러내고 2008년 낙농체험목장으로 선정되어 친환경 목장으로 인정되었다. 아침미소목장에서는 젖소와 송아지에게 먹이도 주고, 아이스크림과 치즈를 직접 만들어 볼 수도 있다. 또 목장에서 직접 만든 유제품을 맛볼 수도 있다. \
도시에서 나고 자란 아이들에게는 더 없이 좋은 체험학습현장이 될 것이며 어른들에게는 추억의 장소와 삶의 휴식처가 될 것이다. 맑고 푸른 자연속에서 송아지에게 우유를 직접 먹여주며 생명의 소중함을 배우고 동물의 체온을 직접 느낄 수 있는 기회가 될 것이다.',
    phoneNumber: '',
    tags: ['INDUSTRY'],
  },
  {
    id: '3026711',
    name: '가시리국산화풍력발전단지',
    address: '제주특별자치도 서귀포시 표선면 녹산로 464-78',
    lat: 33.3939370848,
    lng: 126.7341100468,
    imageUrl: 'http://tong.visitkorea.or.kr/cms/resource/95/3026695_image2_1.jpg',
    description:
      '제주도 서귀포시 동남쪽 끝에 위치한 표선면 가시리는 제주도 내에서 아직 개발되지 않은 지역이라고 할 수 있는데 2009년 1월 국내 최초 지역주민 참여 방식으로 진행된 풍력발전단지  부지 선정 공모에서 최종 부지로 낙점됨에 따라 마을이 새롭게 변화되어 가고 있다. 가시리국산화풍력발전단지는 광활한 평원에 유채꽃단지가 조성되어 있는 가시리 공동목장에 들어서 있다. 봄이 되면 유채꽃과 벚꽃의 콜라보레이션을 즐길 수 있는 가시리마을 10경 중 제1경으로 꼽히는 드라이브 코스로 유명한 녹산로 유채꽃길과 가까운 곳에 있다. 이곳 바람들판의 가을녁엔 빛나는 억새의 향연과 커다란 풍력발전기가 돌아가는 풍경을 볼 수 있는데 이국적인 아름다움을 선사한다.\
사람보다 큰 빨강 파랑 의자와 액자 사진 포인트는 SNS 포토존으로 인기가 있다. 오름들이 붉게 물드는 저녁의 가을 분위기를 만끽하고 싶다면 이곳에 들러 멋진 인생 사진도 남길 수 있으니 표선면 여행시 방문해 보길 추천한다.',
    phoneNumber: '',
    tags: ['INDUSTRY'],
  },
  {
    id: '2469467',
    name: '맥파이 브루어리',
    address: '제주특별자치도 제주시 동회천1길 23',
    lat: 33.5031897997,
    lng: 126.6172288105,
    imageUrl: 'http://tong.visitkorea.or.kr/cms/resource/88/3396588_image2_1.jpg',
    description:
      '경리단길 골목서 동네 친구 4명이 맥주를 마시다 시작하게 된 사업이 바로 맥파이 브루잉 컴퍼니이다. 피맥집으로도 유명한 맥파이는 물 좋고 공기 좋은 제주도에 양조장을 두고 있다. 맥파이 제주 양조장투어는 예약 없이도 가능하나, 인원이 많다면 사전 예약을 추천한다. 예약은 이메일과 전화로 접수할 수 있다. 또한 시간대별로 총 4번 주말에만 투어가 가능하니 참고하자. 이곳에서는 맥주가 만들어지는 모든 과정을 처음부터 끝까지 직접 확인할 수 있으며, 투어를 위해 따로 마련된 전시장이 아니기 때문에 양조설비 사이를 직접 돌아보며 설명을 듣고 질문을 할 수 있다. 1인당 지급하는 참가비에는 투어를 포함해 맥주 한 잔 가격이 포함되어 있다. 현장결제만 가능하며, 만 8세 미만의 아동은 입장이 불가하다. 이밖에도 수제 맥주에 관한 다양한 교육 프로그램을 진행하고 있다. 맥주뿐만 아니라 맥주에 담긴 문화를 함께 나눌 수 있는 프로그램도 제공하고 있어 맥주를 좋아하는 사람들에게 인기 있는 양조장이다. 탑동에 맥파이 펍이 있으니 서귀포까지 가는 길이 멀다면 가까운 시내에서 간단하게 맥파이 맥주를 한 잔 즐겨보자.',
    phoneNumber: '',
    tags: ['INDUSTRY'],
  },
  {
    id: '2778948',
    name: '성이시돌목장 테쉬폰',
    address: '제주특별자치도 제주시 한림읍 금악동길 38',
    lat: 33.3476274194,
    lng: 126.328076887,
    imageUrl: '',
    description:
      '성이시돌목장은 제주에서도 손꼽히는 사진 명소다. 국내에서는 보기 드문 이국적인 형태의 건축물인 테쉬폰 덕분이다. 테쉬폰 양식은 2,000여 년 전 이라크의 수도인 바그다드에서 가까운 테쉬폰이란 지역에서 만들어진 건축 형식에서 그 유래를 찾을 수 있다. 곡선으로 이뤄진 건물 외형은 태풍과 같은 자연재해에 강하다. 또 기둥 없이 내부 공간을 넓게 활용할 수 있다는 점도 특징이다. 그 때문에 이시돌목장이 조성될 당시 숙소를 짓는데 이 같은 건축양식을 활용했다고 한다. 테쉬폰 주택은 이후 다른 지방에도 보급되었으나 현재까지 남아 있는 건 제주가 유일하다. 건축양식도 독특한 데다 세월의 흐름에 따라 자연스럽게 낡은 모습이 제주의 푸른 자연과도 그림처럼 어우러진다.\
목장을 끼고 있어 신선한 우유로 만든 아이스크림을 파는 카페도 근처에 운영하고 있다.',
    phoneNumber: '',
    tags: ['ARCHITECTURE'],
  },
  {
    id: '988441',
    name: '선임교',
    address: '제주특별자치도 서귀포시 중문로105번길 37',
    lat: 33.247055995,
    lng: 126.5547483594,
    imageUrl: 'http://tong.visitkorea.or.kr/cms/resource/47/3477147_image2_1.jpg',
    description:
      '선임교는 서귀포시 중문동에 있는 천제연 폭포와 중문관광단지를 이어 주는 아치형 철제다리이다. 선임교는 [하늘에서 칠선녀가 내려온 다리]라는 뜻으로 선녀다리, 구름다리, 칠선녀다리 등으로 부르기도 한다.\
천제연의 제2단과 제3단 폭포 중간쯤에 위치한 오작교 형태의 다리이다. 다리 양쪽 옆면에 칠선녀의 전설을 살려 각각 다른 악기를 든 선녀들이 구름을 타고 하늘로 올라가는 모습이 조각된 일곱 선녀상이 있다. 야간 관광을 위해 1백 개 난간 사이에 34개의 석등을 설치해 밤에는 색다른 분위기를 즐길 수 있다.\
천제연 폭포를 찾는 관광객뿐만 아니라 제주 올레 8코스를 걷는 올레꾼들이 많이 찾는 관광 명소이다.',
    phoneNumber: '',
    tags: ['ARCHITECTURE'],
  },
  {
    id: '1621118',
    name: '제주해녀항일운동기념탑',
    address: '제주특별자치도 제주시 구좌읍 해녀박물관길 26',
    lat: 33.5225027447,
    lng: 126.8639930584,
    imageUrl: 'http://tong.visitkorea.or.kr/cms/resource/07/3384607_image2_1.jpg',
    description:
      '제주해녀항일운동기념탑은 제주해녀항일운동의 흔적과 저항 정신을 기리고 있는 곳이다. 제주해녀항일운동은 1932년 1월 제주시 구좌읍과 성산읍, 우도면 일대에서 일제의 식민지 수탈 정책과 민족적 차별에 항거한 국내 최대 규모의 항일운동이다. 이 운동은 여성들이 주도한 유일한 여성항일운동으로 그 의의가 매우 크다고 할 수 있다. 제주해녀항일운동기념탑은 당시 항일운동에 참여했던 해녀들의 2차 집결지인 이곳에 해녀 항일 운동 정신을 기리고자 조성하였다. 선열들의 자주독립 정신을 바탕으로 후세들에게 올바른 역사의식과 애국, 애향정신 함양을 위한 교육장으로 활용되고 있다.',
    phoneNumber: '',
    tags: ['ARCHITECTURE'],
  },
  {
    id: '2778809',
    name: '글라스하우스',
    address: '제주특별자치도 서귀포시 성산읍 고성리',
    lat: 33.4291280857,
    lng: 126.9340141417,
    imageUrl: 'http://tong.visitkorea.or.kr/cms/resource/77/3096277_image2_1.jpg',
    description:
      '제주 섭지코지에 자리한 글라스하우스는 현대건축의 거장으로 꼽히는 안도 다다오의 작품이다. 일본 출신의 건축가인 그는 노출 콘크리트를 시적으로 승화시킨 독창적인 아이디어와 자연, 빛과 조화롭게 어우러진 건축물로 국내에서도 뜨거운 인기를 누리고 있다. 글라스하우스는 곧게 뻗은 선과 네모반듯한 면이 작가의 상징처럼 느껴진다. 건축물의 모양새는 바다를 향해 기지개를 켜듯 꺾쇠 형태로 뻗어 나간다. 간결해 보이지만 안도 다다오만의 섬세한 매력이 곳곳에 숨어 있다. 글라스하우스는 민트카페, 민트 가든, 민트 레스토랑으로 이루어져 있다. 특히 민트 레스토랑은 탁 트인 통유리 너머 제주 바다의 아름다움까지 즐길 수 있어 낭만적인 분위기를 즐기기에 좋다. 해가 질 무렵이면 레스토랑 전체가 붉은 노을에 물든다. ',
    phoneNumber: '',
    tags: ['ARCHITECTURE'],
  },
  {
    id: '1556005',
    name: '그리스신화박물관',
    address: '제주특별자치도 제주시 한림읍 광산로 942',
    lat: 33.3481028325,
    lng: 126.357665972,
    imageUrl: 'http://tong.visitkorea.or.kr/cms/resource/71/3053171_image2_1.jpg',
    description:
      '그리스신화박물관은 아름다운 섬 제주도에서 그리스신화와 관련된 200여점의 명화와 대리석을 눈으로 보고 손으로 만져볼 수 있는 체험형 박물관이다. 박물관은 루브르와 바티칸 박물관의 명화와 대리석 조각상 재현 작품을 전시한 공간과 관람객 모두 그리스인으로 변신하는 그리스 마을로 구성되어 있다. 창조관, 올림포스관, 신탁관, 영웅관, 휴먼관, 사랑관, 그리스 마을에서 기존 유물 중심의 수동적인 관람 방식에서 벗어나 전시물과 관람객이 교감할 수 있도록 다양한 체험형 전시를 제공한다.\
대표 체험으로는 그리스의상 체험, 아테네시민권 발급체험, 올림포스 12신 가면 만들기 체험 등이 있다.',
    phoneNumber: '',
    tags: ['CULTURE'],
  },
  {
    id: '130512',
    name: '제주아트서커스',
    address: '제주특별자치도 서귀포시 안덕면 동광로 214',
    lat: 33.3143952464,
    lng: 126.3449344975,
    imageUrl: 'http://tong.visitkorea.or.kr/cms/resource/62/3034562_image2_1.jpg',
    description:
      '공연 문화의 새로운 장을 열고자 과거 해피타운 공연장 자리에 2004년 1월에 개장한 국내 최초의 중국 서커스 및 오토바이 쇼 상설 공연장이다. 서커스 공연장에서는 오토바이 쇼와 자전거 기예, 중국 기예 공연을 60분 동안 펼치고 있으며 특히 국제 서커스대회에서 우수작으로 선정된 종목만을 엄선하여 최고의 공연을 선보이고 있다. \
40여 명의 출연자로 구성된 공연은 최첨단 효과와 환상적인 빛과 음악 연출, 화려한 안무로 긴장과 탄성이 절로 나오는 세계적 규모의 서커스 쇼다. 공연문화의 새로운 패러다임을 만들어 가는 아트서커스는 중국에서도 인정받는 최고의 기예 단원들이 펼치는 국내 최고의 공연이다.',
    phoneNumber: '',
    tags: ['CULTURE'],
  },
  {
    id: '526666',
    name: '제주유리박물관',
    address: '제주특별자치도 서귀포시 중산간서로 1403, (상예동)',
    lat: 33.2784468371,
    lng: 126.3701775526,
    imageUrl: 'http://tong.visitkorea.or.kr/cms/resource/62/3034562_image2_1.jpg',
    description:
      '제주유리박물관은 한국의 유일한 유리 전문 박물관으로 8,000여 평의 넓은 공간에 전시된 유리작품을 감상할 수 있는 공간이다. 2008년 3월 1일 제주도에 개관했으며 유리 전시실, 가마 작업실, 브로잉실, 유리 가공실, 강의실, 카페 등을 갖추고 있다. \
다양한 유리로 만들어진 작품이 제주의 자연과 어우러져 투명한 아름다움을 선사한다. 정월을 돌아보는 동안 자연과 예술 작품이 주는 힐링을 만끽할 수 있다. 유리블로잉 및 캔들 만들기 등 직접 유리 공예품을 만들 수 있는 체험 프로그램과 관람 중 마음에 드는 작품을 구입할 수 있는 기념품 숍이 준비되어 있다.\
가족이나 친구, 지인과 함께 방문해 특별한 유리 체험을 즐길 수 있는 이색 박물관이다.',
    phoneNumber: '',
    tags: ['CULTURE'],
  },
];
