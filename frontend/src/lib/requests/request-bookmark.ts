import { Response, envelopeApiClient, defaultErrorHandler } from './request';

export interface Bookmark {
  userId: string;
  tourSpotId: string;
}

type RequestBookmarkError = never;

export function requestPostUserBookmark(userId: string, tourSpotId: string) {
  return envelopeApiClient
    .post<Response<Bookmark, RequestBookmarkError>>(`/api/users/${userId}/bookmarks`, {
      tourSpotId: tourSpotId,
    })
    .then((response) => {
      return response.data;
    })
    .catch(defaultErrorHandler);
}

export function requestDeleteUserBookmark(userId: string, tourSpotId: string) {
  return envelopeApiClient
    .delete<Response<null, RequestBookmarkError>>(`/api/users/${userId}/bookmarks/${tourSpotId}`)
    .then((response) => {
      return response.data;
    })
    .catch(defaultErrorHandler);
}
