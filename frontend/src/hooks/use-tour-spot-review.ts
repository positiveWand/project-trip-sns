import { paginationApiClient, Page } from '@/lib/requests/request';
import * as React from 'react';

export interface UseDataError {
  error: string;
  message: string;
}

export interface TourSpotReview {
  id: string;
  tourSpotId: string;
  userId: string;
  content: string;
  likes: number;
  time: string;
}

export function useTourSpotReviews(
  id: string | undefined,
  page: number | undefined,
  limit: number | undefined,
): [Page<TourSpotReview[]> | null, UseDataError | null, boolean] {
  const [tourSpotReviews, setTourSpotReviews] = React.useState<Page<TourSpotReview[]> | null>(null);
  const [error, setError] = React.useState<UseDataError | null>(null);
  const [loading, setLoading] = React.useState<boolean>(true);

  React.useEffect(() => {
    setLoading(true);

    if (!id) {
      setTourSpotReviews(null);
      setError(null);
      setLoading(false);
      return;
    }

    paginationApiClient
      .get<Page<TourSpotReview[]> | UseDataError>(`/api/tour-spots/${id}/reviews`, {
        params: {
          pageNo: page,
          pageSize: limit,
        },
      })
      .then((response) => {
        setTourSpotReviews(response.data as Page<TourSpotReview[]>);
        setError(null);
      })
      .catch((error) => {
        setTourSpotReviews(null);
        setError(error.response.data);
      })
      .finally(() => {
        setLoading(false);
      });
  }, [id, page, limit]);

  return [tourSpotReviews, error, loading];
}
