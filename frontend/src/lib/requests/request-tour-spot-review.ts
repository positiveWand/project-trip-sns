import { defaultErrorHandler, envelopeApiClient, Response } from './request';

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

export type RequestTourSpotError = never;

export function requestPostTourSpotReview(tourSpotId: string, userId: string, content: string) {
  return envelopeApiClient
    .post<Response<TourSpotReview, RequestTourSpotError>>(`/api/tour-spots/${tourSpotId}/reviews`, {
      userId: userId,
      content: content,
    })
    .then((response) => {
      return response.data;
    })
    .catch(defaultErrorHandler);
}

export function requestDeleteTourSpotReview(tourSpotReviewId: string) {
  return envelopeApiClient
    .delete<Response<null, RequestTourSpotError>>(`/api/tour-spot-reviews/${tourSpotReviewId}`)
    .then((response) => {
      return response.data;
    })
    .catch(defaultErrorHandler);
}

export function requestPutTourSpotReviewLike(
  userId: string,
  tourSpotReviewId: string,
  liked: boolean,
) {
  return envelopeApiClient
    .put<Response<TourSpotReviewLike, RequestTourSpotError>>(
      `/api/tour-spot-reviews/${tourSpotReviewId}/likes`,
      {
        userId: userId,
        liked: liked,
      },
    )
    .then((response) => {
      return response.data;
    })
    .catch(defaultErrorHandler);
}

export function requestGetTourSpotReviewLikes(userId: string, tourSpotReviewIds: string[]) {
  return envelopeApiClient
    .get<Response<TourSpotReviewLike[], RequestTourSpotError>>(
      `/api/users/${userId}/tour-spot-reviews/likes`,
      {
        params: {
          tourSpotReviewIds: tourSpotReviewIds,
        },
      },
    )
    .then((response) => {
      return response.data;
    })
    .catch(defaultErrorHandler);
}
