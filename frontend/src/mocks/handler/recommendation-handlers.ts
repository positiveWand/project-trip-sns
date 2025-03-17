import { http, HttpResponse } from 'msw';
import { TourSpot, TEST_TOUR_SPOTS } from '../database/tour-spot';

type TourSpotOverview = Omit<TourSpot, 'description' | 'phoneNumber' | 'reviews'>;

function toOverview(tourSpot: TourSpot): TourSpotOverview {
  return {
    id: tourSpot.id,
    name: tourSpot.name,
    address: tourSpot.address,
    lat: tourSpot.lat,
    lng: tourSpot.lng,
    imageUrl: tourSpot.imageUrl,
    tags: tourSpot.tags.map(tagView),
  };
}

function tagView(tag: string) {
  const map: Record<string, string> = {
    nature: '자연',
    history: '역사',
    rest: '휴양',
    experience: '체험',
    industry: '산업',
    architecture: '건축/조형',
    culture: '문화',
    festival: '축제',
    concert: '공연/행사',
  };
  return map[tag];
}

export const recommendationHandlers = [
  http.get('/api/recommendations/:recommendationType', async ({ params }) => {
    const { recommendationType } = params;

    if (recommendationType != 'main') {
      return HttpResponse.json(
        {
          error: 'NO_RECOMMENDATION',
          message: '추천종류(recommendationType)에 해당하는 추천이 존재하지 않습니다.',
        },
        { status: 404 },
      );
    }

    let result = [];

    for (let i = 0; i < 4; i++) {
      const randomIndex = Math.floor(Math.random() * (TEST_TOUR_SPOTS.length - 1));
      result.push(TEST_TOUR_SPOTS[randomIndex]);
    }

    result = result.map(toOverview);

    return HttpResponse.json(result, { status: 200 });
  }),
];
