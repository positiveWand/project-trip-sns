import { http, HttpResponse } from 'msw';
import { TourSpot, TEST_TOUR_SPOTS } from '../database/tour-spot';
import {
  TourSpotReview,
  TEST_TOUR_SPOT_REVIEWS,
  TourSpotReviewLike,
  TEST_TOUR_SPOT_REVIEW_LIKES,
} from '../database/tour-spot-review';
import { AUTHORITY, AUTHORIZED } from '../config';
import { TEST_BOOKMARKS } from '../database/user';

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
    NATURE: '자연',
    HISTORY: '역사',
    REST: '휴양',
    EXPERIENCE: '체험',
    INDUSTRY: '산업',
    ARCHITECTURE: '건축/조형',
    CULTURE: '문화',
    FESTIVAL: '축제',
    CONCERT: '공연/행사',
  };
  return map[tag];
}

function dateFormat(date: Date) {
  const month = date.getMonth() + 1;
  const day = date.getDate();
  const hour = date.getHours();
  const minute = date.getMinutes();

  const monthStr = month >= 10 ? month : '0' + month;
  const dayStr = day >= 10 ? day : '0' + day;
  const hourStr = hour >= 10 ? hour : '0' + hour;
  const minuteStr = minute >= 10 ? minute : '0' + minute;

  return date.getFullYear() + '-' + monthStr + '-' + dayStr + 'T' + hourStr + ':' + minuteStr;
}

export const tourSpotHandlers = [
  http.get('/api/tour-spots', async ({ request }) => {
    const url = new URL(request.url);

    const query = url.searchParams.get('query');
    const tags = url.searchParams.getAll('tags');
    const customFilters = url.searchParams.getAll('customFilters');
    const sort = url.searchParams.get('sort');
    const pageNo = url.searchParams.get('pageNo')
      ? parseInt(url.searchParams.get('pageNo') as string)
      : 1;
    const pageSize = url.searchParams.get('pageSize')
      ? parseInt(url.searchParams.get('pageSize') as string)
      : 10;

    let result = TEST_TOUR_SPOTS.filter((tourSpot) => {
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

    if (customFilters.length > 0 && AUTHORIZED && customFilters.includes('bookmark')) {
      result = result.filter((tourSpot) =>
        TEST_BOOKMARKS.some((bookmark) => bookmark.tourSpotId == tourSpot.id),
      );
    } else if (customFilters.length > 0 && !AUTHORIZED) {
      return HttpResponse.json(
        {
          error: 'UNAUTHORIZED',
          message: '인증이 필요합니다.',
        },
        { status: 401 },
      );
    }

    if (query) {
      result = result.filter((tourSpot) => tourSpot.name.includes(query));
    }

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

  http.get('/api/tour-spots/map', async ({ request }) => {
    const url = new URL(request.url);

    const query = url.searchParams.get('query');
    const tags = url.searchParams.getAll('tags');
    const customFilters = url.searchParams.getAll('customFilters');
    const minLat = parseFloat(url.searchParams.get('minLat')!);
    const minLng = parseFloat(url.searchParams.get('minLng')!);
    const maxLat = parseFloat(url.searchParams.get('maxLat')!);
    const maxLng = parseFloat(url.searchParams.get('maxLng')!);

    let result = TEST_TOUR_SPOTS.filter((tourSpot) => {
      if (tags.length == 0) {
        return true;
      }

      for (const tag of tags) {
        if (tourSpot.tags.includes(tag)) {
          return true;
        }
      }
      return false;
    });

    if (customFilters.length > 0 && AUTHORIZED && customFilters.includes('bookmark')) {
      result = result.filter((tourSpot) =>
        TEST_BOOKMARKS.some((bookmark) => bookmark.tourSpotId == tourSpot.id),
      );
    } else if (customFilters.length > 0 && !AUTHORIZED) {
      return HttpResponse.json(
        {
          error: 'UNAUTHORIZED',
          message: '인증이 필요합니다.',
        },
        { status: 401 },
      );
    }

    if (query) {
      result = result.filter((tourSpot) => tourSpot.name.includes(query));
    }

    result = result.filter(
      (tourSpot) =>
        minLat <= tourSpot.lat &&
        tourSpot.lat <= maxLat &&
        minLng <= tourSpot.lng &&
        tourSpot.lng <= maxLng,
    );

    return HttpResponse.json(result, {
      status: 200,
    });
  }),

  http.get('/api/tour-spots/:tourSpotId', async ({ params }) => {
    const { tourSpotId } = params;

    let target = TEST_TOUR_SPOTS.find((tourSpot) => tourSpot.id == tourSpotId);

    if (!target) {
      return HttpResponse.json(
        {
          error: 'NO_TOUR_SPOT',
          message: '관광지ID(tourSpotId)에 해당하는 관광지가 존재하지 않습니다.',
        },
        { status: 404 },
      );
    }

    target = {
      ...target,
      tags: target.tags.map(tagView),
    };
    return HttpResponse.json(target, { status: 200 });
  }),

  http.get('/api/tour-spots/:tourSpotId/reviews', async ({ request, params }) => {
    const url = new URL(request.url);

    const { tourSpotId } = params;

    const pageNo = url.searchParams.get('pageNo')
      ? parseInt(url.searchParams.get('pageNo') as string)
      : 1;
    const pageSize = url.searchParams.get('pageSize')
      ? parseInt(url.searchParams.get('pageSize') as string)
      : 10;

    const target = TEST_TOUR_SPOTS.find((tourSpot) => tourSpot.id == tourSpotId);
    if (!target) {
      return HttpResponse.json(
        {
          error: 'NO_TOUR_SPOT',
          message: '관광지ID(tourSpotId)에 해당하는 관광지가 존재하지 않습니다.',
        },
        { status: 404 },
      );
    }

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
  }),

  http.post('/api/tour-spots/:tourSpotId/reviews', async ({ request, params }) => {
    const { tourSpotId } = params;

    const reviewRequest = (await request.json()) as Pick<TourSpotReview, 'userId' | 'content'>;

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
    if (!target) {
      return HttpResponse.json(
        {
          error: 'NO_TOUR_SPOT',
          message: '관광지ID(tourSpotId)에 해당하는 관광지가 존재하지 않습니다.',
        },
        { status: 404 },
      );
    }

    const newReview = {
      ...reviewRequest,
      id: TEST_TOUR_SPOT_REVIEWS.reduce((prev, curr) => {
        return prev.id > curr.id ? prev : curr;
      }).id,
      likes: 0,
      tourSpotId: tourSpotId as string,
      time: dateFormat(new Date()),
    };
    TEST_TOUR_SPOT_REVIEWS.splice(0, 0, newReview);

    return HttpResponse.json(newReview, { status: 201 });
  }),

  http.delete('/api/tour-spot-reviews/:tourSpotReviewId', async ({ params }) => {
    const { tourSpotReviewId } = params;

    const target = TEST_TOUR_SPOT_REVIEWS.findIndex((review) => review.id == tourSpotReviewId);

    if (!AUTHORIZED) {
      return HttpResponse.json(
        {
          error: 'UNAUTHORIZED',
          message: '인증이 필요합니다.',
        },
        { status: 401 },
      );
    }

    if (target == -1) {
      return HttpResponse.json(
        {
          error: 'NO_TOUR_SPOT',
          message: '관광지 후기ID(tourSpotReviewId)에 해당하는 관광지가 존재하지 않습니다.',
        },
        { status: 404 },
      );
    }

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
  }),

  http.put('/api/tour-spot-reviews/:tourSpotReviewId/likes', async ({ request, params }) => {
    const likeRequest = (await request.json()) as Omit<TourSpotReviewLike, 'tourSpotId'>;
    const { tourSpotReviewId } = params;

    if (!AUTHORIZED) {
      return HttpResponse.json(
        {
          error: 'UNAUTHORIZED',
          message: '인증이 필요합니다.',
        },
        { status: 401 },
      );
    }
    if (!AUTHORITY.includes('REVIEW_LIKE_PUT')) {
      return HttpResponse.json(
        {
          error: 'FORBIDDEN',
          message: '권한이 없습니다.',
        },
        { status: 403 },
      );
    }

    const targetReview = TEST_TOUR_SPOT_REVIEWS.find((review) => review.id == tourSpotReviewId);

    if (!targetReview) {
      return HttpResponse.json(
        {
          error: 'NO_TOUR_SPOT',
          message: '관광지 후기ID(tourSpotReviewId)에 해당하는 관광지가 존재하지 않습니다.',
        },
        { status: 404 },
      );
    }

    const targetLike = TEST_TOUR_SPOT_REVIEW_LIKES.findIndex(
      (like) => like.tourSpotReviewId == tourSpotReviewId,
    );
    if (likeRequest.liked) {
      if (targetLike == -1) {
        TEST_TOUR_SPOT_REVIEW_LIKES.push({
          userId: '',
          tourSpotReviewId: (tourSpotReviewId as string).toString(),
        });

        targetReview.likes += 1;
      }
    } else {
      if (targetLike != -1) {
        TEST_TOUR_SPOT_REVIEW_LIKES.splice(targetLike, 1);

        targetReview.likes -= 1;
      }
    }

    return HttpResponse.json(
      {
        userId: likeRequest.userId,
        tourSpotReviewId: tourSpotReviewId,
        liked: likeRequest.liked,
      },
      { status: 200 },
    );
  }),
];
