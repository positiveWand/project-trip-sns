import { http, HttpResponse } from 'msw';
import { TourSpot, TEST_TOUR_SPOTS } from '../database/tour-spot';
import { TourSpotReview, TEST_TOUR_SPOT_REVIEWS } from '../database/tour-spot-review';
import { AUTHORITY, AUTHORIZED } from '../config';

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

export const tourSpotHandlers = [
  http.get('/tour-spots', async ({ request }) => {
    const url = new URL(request.url);

    const tags = url.searchParams.getAll('tags');
    const sort = url.searchParams.get('sort');
    const pageNoString = url.searchParams.get('pageNo');
    const pageSizeString = url.searchParams.get('pageSize');

    const pageNo = pageNoString ? parseInt(pageNoString) : 1;
    const pageSize = pageSizeString ? parseInt(pageSizeString) : 10;

    const result = TEST_TOUR_SPOTS.filter((tourSpot) => {
      if (tags.length == 0) {
        return true;
      }

      for (const tag of tags) {
        if (tourSpot.tags.includes(tag)) {
          return true;
        }
      }
      return false;
    }).sort((a, b) => {
      if (sort == 'name-asc') {
        return a.name < b.name ? -1 : 1;
      } else if (sort == 'name-desc') {
        return a.name > b.name ? -1 : 1;
      } else {
        return 0;
      }
    });
    const page = result.slice((pageNo - 1) * pageSize, pageNo * pageSize).map(toOverview);

    return HttpResponse.json(page, {
      status: 200,
      headers: {
        'X-Pagination-Page': pageNo.toString(),
        'X-Pagination-Page-Limit': pageSize.toString(),
        'X-Pagination-Page-Size': page.length.toString(),
        'X-Pagination-Total-Page': (Math.floor((result.length - 1) / pageSize) + 1).toString(),
        'X-Pagination-Total-Item': result.length.toString(),
      },
    });
  }),

  http.get('/tour-spots/:tourSpotId', async ({ params }) => {
    const { tourSpotId } = params;

    let target = TEST_TOUR_SPOTS.find((tourSpot) => tourSpot.id == tourSpotId);
    if (target) {
      target = {
        ...target,
        tags: target.tags.map(tagView),
      };
      if (AUTHORIZED) {
        return HttpResponse.json(target, { status: 200 });
      } else {
        return HttpResponse.json(target, { status: 200 });
      }
    } else {
      return HttpResponse.json(
        {
          error: 'NO_TOUR_SPOT',
          message: '관광지ID(tourSpotId)에 해당하는 관광지가 존재하지 않습니다.',
        },
        { status: 404 },
      );
    }
  }),

  http.get('/tour-spots/:tourSpotId/reviews', async ({ request, params }) => {
    const url = new URL(request.url);

    const { tourSpotId } = params;

    const pageNoString = url.searchParams.get('pageNo');
    const pageSizeString = url.searchParams.get('pageSize');

    const pageNo = pageNoString ? parseInt(pageNoString) : 1;
    const pageSize = pageSizeString ? parseInt(pageSizeString) : 10;

    const target = TEST_TOUR_SPOTS.find((tourSpot) => tourSpot.id == tourSpotId);
    if (target) {
      const page = TEST_TOUR_SPOT_REVIEWS.slice((pageNo - 1) * pageSize, pageNo * pageSize);

      return HttpResponse.json(page, {
        status: 200,
        headers: {
          'X-Pagination-Page': pageNo.toString(),
          'X-Pagination-Page-Limit': pageSize.toString(),
          'X-Pagination-Page-Size': page.length.toString(),
          'X-Pagination-Total-Page': (
            Math.floor((TEST_TOUR_SPOT_REVIEWS.length - 1) / pageSize) + 1
          ).toString(),
          'X-Pagination-Total-Item': TEST_TOUR_SPOT_REVIEWS.length.toString(),
        },
      });
    } else {
      return HttpResponse.json(
        {
          error: 'NO_TOUR_SPOT',
          message: '관광지ID(tourSpotId)에 해당하는 관광지가 존재하지 않습니다.',
        },
        { status: 404 },
      );
    }
  }),

  http.post('/tour-spots/:tourSpotId/reviews', async ({ request, params }) => {
    const { tourSpotId } = params;

    const reviewRequest = (await request.json()) as Omit<TourSpotReview, 'id'>;

    if (!AUTHORIZED) {
      return HttpResponse.json(
        {
          error: 'UNAUTHORIZED',
          message: '인증이 필요합니다.',
        },
        { status: 401 },
      );
    }
    if (!AUTHORITY.includes('REVIEW_POST')) {
      return HttpResponse.json(
        {
          error: 'FORBIDDEN',
          message: '권한이 없습니다.',
        },
        { status: 403 },
      );
    }

    const target = TEST_TOUR_SPOTS.find((tourSpot) => tourSpot.id == tourSpotId);
    if (target) {
      const newReview = {
        ...reviewRequest,
        id: TEST_TOUR_SPOT_REVIEWS.reduce((prev, curr) => {
          return prev.id > curr.id ? prev : curr;
        }).id,
        likes: 0,
      };
      TEST_TOUR_SPOT_REVIEWS.splice(0, 0, newReview);

      return HttpResponse.json(newReview, { status: 201 });
    } else {
      return HttpResponse.json(
        {
          error: 'NO_TOUR_SPOT',
          message: '관광지ID(tourSpotId)에 해당하는 관광지가 존재하지 않습니다.',
        },
        { status: 404 },
      );
    }
  }),

  http.delete('/tour-spot-reviews/:tourSpotReviewId', async ({ params }) => {
    const { tourSpotReviewId } = params;

    const target = TEST_TOUR_SPOT_REVIEWS.findIndex((review) => review.id == tourSpotReviewId);

    if (target != -1) {
      if (!AUTHORITY.includes('REVIEW_DELETE')) {
        return HttpResponse.json(
          {
            error: 'FORBIDDEN',
            message: '권한이 없습니다.',
          },
          { status: 403 },
        );
      }

      TEST_TOUR_SPOT_REVIEWS.splice(target, 1);

      return new HttpResponse(null, { status: 204 });
    }

    return HttpResponse.json(
      {
        error: 'NO_TOUR_SPOT',
        message: '관광지 후기ID(tourSpotReviewId)에 해당하는 관광지가 존재하지 않습니다.',
      },
      { status: 404 },
    );
  }),
];
