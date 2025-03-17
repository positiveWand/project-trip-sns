export interface TourSpotReview {
  id: string;
  tourSpotId: string;
  userId: string;
  content: string;
  likes: number;
  time: string;
}

export interface TourSpotReviewLike {
  userId: string;
  tourSpotReviewId: string;
  liked: boolean;
}

export const TEST_TOUR_SPOT_REVIEWS: TourSpotReview[] = [
  {
    id: '1',
    tourSpotId: '',
    userId: 'testuser1',
    content: '너무 좋아요~',
    likes: 143,
    time: '2025-02-23T18:12',
  },
  {
    id: '2',
    tourSpotId: '',
    userId: 'testuser2',
    content: '너무 이것저것 이리보고 저리보고~',
    likes: 181,
    time: '2025-02-23T17:12',
  },
  {
    id: '3',
    tourSpotId: '',
    userId: 'testuser3',
    content: '아니 어떻게 이런 풍경이~',
    likes: 200,
    time: '2025-02-22T18:12',
  },
  {
    id: '4',
    tourSpotId: '',
    userId: 'testuser4',
    content: '입이 다물어지지 않는 경관~',
    likes: 118,
    time: '2025-02-22T17:10',
  },
  {
    id: '5',
    tourSpotId: '',
    userId: 'testuser5',
    content: '너무 싫어요~',
    likes: 85,
    time: '2025-02-21T18:12',
  },
  {
    id: '6',
    tourSpotId: '',
    userId: 'testuser1',
    content: '풍경이 너무 아름다워요! 사진 찍기 딱 좋은 곳이에요.',
    likes: 62,
    time: '2025-02-20T18:12',
  },
  {
    id: '7',
    tourSpotId: '',
    userId: 'testuser2',
    content: '생각보다 사람이 많았지만, 분위기가 좋아서 만족스러웠어요.',
    likes: 146,
    time: '2025-02-19T17:12',
  },
  {
    id: '8',
    tourSpotId: '',
    userId: 'testuser3',
    content: '야경이 특히 환상적이에요. 꼭 밤에도 방문해보세요!',
    likes: 18,
    time: '2025-02-18T18:12',
  },
  {
    id: '9',
    tourSpotId: '',
    userId: 'testuser4',
    content: '조용하고 한적해서 힐링하기 딱 좋아요.',
    likes: 91,
    time: '2025-02-17T17:10',
  },
  {
    id: '10',
    tourSpotId: '',
    userId: 'testuser5',
    content: '근처에 맛집도 많아서 하루 종일 즐길 수 있어요.',
    likes: 182,
    time: '2025-02-16T18:12',
  },
  {
    id: '11',
    tourSpotId: '',
    userId: 'testuser1',
    content: '기대 이상으로 멋진 곳이었어요! 꼭 가보세요.',
    likes: 48,
    time: '2025-02-15T18:12',
  },
  {
    id: '12',
    tourSpotId: '',
    userId: 'testuser2',
    content: '주차 공간이 부족해서 조금 불편했어요. 대중교통 이용 추천!',
    likes: 116,
    time: '2025-02-14T17:12',
  },
  {
    id: '13',
    tourSpotId: '',
    userId: 'testuser3',
    content: '자연경관이 정말 멋져요. 하이킹 코스도 강추합니다.',
    likes: 180,
    time: '2025-02-13T18:12',
  },
  {
    id: '14',
    tourSpotId: '',
    userId: 'testuser4',
    content: '사진으로만 봤을 땐 별로였는데, 직접 가보니 감동이었어요!',
    likes: 139,
    time: '2025-02-12T17:10',
  },
  {
    id: '15',
    tourSpotId: '',
    userId: 'testuser5',
    content: '주말에는 사람이 너무 많아서 평일 방문을 추천합니다.',
    likes: 23,
    time: '2025-02-11T18:12',
  },
];

export const TEST_TOUR_SPOT_REVIEW_LIKES: Omit<TourSpotReviewLike, 'liked'>[] = [
  {
    userId: '',
    tourSpotReviewId: '4',
  },
  {
    userId: '',
    tourSpotReviewId: '10',
  },
  {
    userId: '',
    tourSpotReviewId: '11',
  },
  {
    userId: '',
    tourSpotReviewId: '12',
  },
  {
    userId: '',
    tourSpotReviewId: '14',
  },
];
