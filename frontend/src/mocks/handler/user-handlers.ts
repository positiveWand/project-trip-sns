import { http, HttpResponse } from 'msw';
import { TEST_USERS, User, Bookmark, TEST_BOOKMARKS } from '../database/user';
import { AUTHORITY, AUTHORIZED } from '../config';
import { TEST_TOUR_SPOT_REVIEW_LIKES, TourSpotReviewLike } from '../database/tour-spot-review';

type Profile = Pick<User, 'id' | 'name'>;

function toProfile(user: User): Profile {
  return {
    id: user.id,
    name: user.name,
  };
}

export const userHandlers = [
  http.get('/users', async ({ request }) => {
    const url = new URL(request.url);

    const query = url.searchParams.get('query') ? (url.searchParams.get('query') as string) : '';
    const pageNo = url.searchParams.get('pageNo')
      ? parseInt(url.searchParams.get('pageNo') as string)
      : 1;
    const pageSize = url.searchParams.get('pageSize')
      ? parseInt(url.searchParams.get('pageSize') as string)
      : 10;

    const result = TEST_USERS.filter((user) => user.id.includes(query)).sort();
    const page = result.slice((pageNo - 1) * pageSize, pageNo * pageSize).map(toProfile);

    return HttpResponse.json(result, {
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

  http.get('/users/:userId', async ({ params }) => {
    const { userId } = params;

    const target = TEST_USERS.find((user) => user.id == userId);

    if (!target) {
      return HttpResponse.json(
        {
          error: 'NO_USER',
          message: '회원ID(userId)에 해당하는 사용자가 존재하지 않습니다.',
        },
        { status: 404 },
      );
    }

    return HttpResponse.json(toProfile(target), { status: 200 });
  }),

  http.get('/users/:userId/bookmarks', async ({ request, params }) => {
    const url = new URL(request.url);

    const { userId } = params;

    const pageNo = url.searchParams.get('pageNo')
      ? parseInt(url.searchParams.get('pageNo') as string)
      : 1;
    const pageSize = url.searchParams.get('pageSize')
      ? parseInt(url.searchParams.get('pageSize') as string)
      : 10;

    const target = TEST_USERS.find((user) => user.id == userId);

    if (!target) {
      return HttpResponse.json(
        {
          error: 'NO_USER',
          message: '회원ID(userId)에 해당하는 사용자가 존재하지 않습니다.',
        },
        { status: 404 },
      );
    }

    const page = TEST_BOOKMARKS.slice((pageNo - 1) * pageSize, pageNo * pageSize);

    return HttpResponse.json(page, {
      status: 200,
      headers: {
        'X-Pagination-Page': pageNo.toString(),
        'X-Pagination-Page-Limit': pageSize.toString(),
        'X-Pagination-Page-Size': page.length.toString(),
        'X-Pagination-Total-Page': (
          Math.floor((TEST_BOOKMARKS.length - 1) / pageSize) + 1
        ).toString(),
        'X-Pagination-Total-Item': TEST_BOOKMARKS.length.toString(),
      },
    });
  }),

  http.post('/users/:userId/bookmarks', async ({ request }) => {
    if (!AUTHORIZED) {
      return HttpResponse.json(
        {
          error: 'UNAUTHORIZED',
          message: '인증이 필요합니다.',
        },
        { status: 401 },
      );
    }

    const bookmarkRequest = (await request.json()) as Bookmark;

    if (!AUTHORITY.includes('BOOKMARK_POST')) {
      return HttpResponse.json(
        {
          error: 'FORBIDDEN',
          message: '권한이 없습니다.',
        },
        { status: 403 },
      );
    }

    TEST_BOOKMARKS.push({
      tourSpotId: bookmarkRequest.tourSpotId,
    });

    return HttpResponse.json(
      {
        tourSpotId: bookmarkRequest.tourSpotId,
      },
      { status: 201 },
    );
  }),

  http.get('/users/:userId/bookmarks/:tourSpotId', async ({ params }) => {
    const { userId, tourSpotId } = params;

    const targetBookmark = TEST_BOOKMARKS.findIndex(
      (bookmark) => bookmark.tourSpotId == tourSpotId,
    );

    if (targetBookmark == -1) {
      return HttpResponse.json(
        {
          error: 'NO_TOUR_SPOT',
          message: '관광지ID(tourSpotId)에 해당하는 관광지가 존재하지 않습니다.',
        },
        { status: 404 },
      );
    }

    TEST_BOOKMARKS.splice(targetBookmark, 1);

    return HttpResponse.json(
      {
        userId: userId,
        tourSpotId: TEST_BOOKMARKS[targetBookmark].tourSpotId,
      },
      { status: 200 },
    );
  }),

  http.delete('/users/:userId/bookmarks/:tourSpotId', async ({ params }) => {
    if (!AUTHORIZED) {
      return HttpResponse.json(
        {
          error: 'UNAUTHORIZED',
          message: '인증이 필요합니다.',
        },
        { status: 401 },
      );
    }

    const { userId, tourSpotId } = params;

    if (!AUTHORITY.includes('BOOKMARK_DELETE')) {
      return HttpResponse.json(
        {
          error: 'FORBIDDEN',
          message: '권한이 없습니다.',
        },
        { status: 403 },
      );
    }

    const targetUser = TEST_USERS.find((user) => user.id == userId);
    console.log(userId, targetUser);

    if (!targetUser) {
      return HttpResponse.json(
        {
          error: 'NO_USER',
          message: '회원ID(userId)에 해당하는 사용자가 존재하지 않습니다.',
        },
        { status: 404 },
      );
    }

    const targetBookmark = TEST_BOOKMARKS.findIndex(
      (bookmark) => bookmark.tourSpotId == tourSpotId,
    );

    if (targetBookmark == -1) {
      return HttpResponse.json(
        {
          error: 'NO_TOUR_SPOT',
          message: '관광지ID(tourSpotId)에 해당하는 관광지가 존재하지 않습니다.',
        },
        { status: 404 },
      );
    }

    TEST_BOOKMARKS.splice(targetBookmark, 1);

    return new HttpResponse(null, { status: 204 });
  }),

  http.get('/users/:userId/tour-spot-reviews/likes', async ({ request, params }) => {
    const { userId } = params;

    const url = new URL(request.url);

    const tourSpotReviewIds = url.searchParams.getAll('tourSpotReviewIds');

    const targetUser = TEST_USERS.find((user) => user.id == userId);

    if (!targetUser) {
      return HttpResponse.json(
        {
          error: 'NO_USER',
          message: '회원ID(userId)에 해당하는 사용자가 존재하지 않습니다.',
        },
        { status: 404 },
      );
    }

    const result: Pick<TourSpotReviewLike, 'tourSpotReviewId' | 'liked'>[] = [];

    for (const tourSpotReviewId of tourSpotReviewIds) {
      if (TEST_TOUR_SPOT_REVIEW_LIKES.some((like) => like.tourSpotReviewId == tourSpotReviewId)) {
        result.push({
          tourSpotReviewId: tourSpotReviewId,
          liked: true,
        });
      } else {
        result.push({
          tourSpotReviewId: tourSpotReviewId,
          liked: false,
        });
      }
    }

    return HttpResponse.json(result, { status: 200 });
  }),
];
