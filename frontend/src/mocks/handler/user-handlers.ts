import { http, HttpResponse } from 'msw';
import { TEST_USERS, User, Bookmark, TEST_BOOKMARKS } from '../database/user';
import { AUTHORITY, AUTHORIZED } from '../config';

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

    const queryString = url.searchParams.get('query');
    const query = queryString ? queryString : '';
    const pageNoString = url.searchParams.get('pageNo');
    const pageSizeString = url.searchParams.get('pageSize');

    const pageNo = pageNoString ? parseInt(pageNoString) : 1;
    const pageSize = pageSizeString ? parseInt(pageSizeString) : 10;

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

    if (target) {
      return HttpResponse.json(toProfile(target), { status: 200 });
    } else {
      return HttpResponse.json(
        {
          error: 'NO_USER',
          message: '회원ID(userId)에 해당하는 사용자가 존재하지 않습니다.',
        },
        { status: 404 },
      );
    }
  }),

  http.get('/users/:userId/bookmarks', async ({ request, params }) => {
    const url = new URL(request.url);

    const { userId } = params;

    const pageNoString = url.searchParams.get('pageNo');
    const pageSizeString = url.searchParams.get('pageSize');

    const pageNo = pageNoString ? parseInt(pageNoString) : 1;
    const pageSize = pageSizeString ? parseInt(pageSizeString) : 10;

    const target = TEST_USERS.find((user) => user.id == userId);

    if (target) {
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
    } else {
      return HttpResponse.json(
        {
          error: 'NO_USER',
          message: '회원ID(userId)에 해당하는 사용자가 존재하지 않습니다.',
        },
        { status: 404 },
      );
    }
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

    const targetUser = TEST_USERS.findIndex((user) => user.id == userId);

    if (targetUser != -1) {
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

    if (targetBookmark != -1) {
      TEST_BOOKMARKS.splice(targetBookmark, 1);

      return new HttpResponse(null, { status: 204 });
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
];
