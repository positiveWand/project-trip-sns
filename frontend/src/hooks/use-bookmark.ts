import { paginationApiClient, envelopeApiClient, Response, Page } from '@/lib/requests/request';
import * as React from 'react';

export interface UseDataError {
  error: string;
  message: string;
}

export interface Bookmark {
  userId: string;
  tourSpotId: string;
  tourSpotOverview: {
    name: string;
    address: string;
    lat: number;
    lng: number;
    imageUrl: string;
    description: string;
    phoneNumber: string;
    tags: string[];
  };
}

export function useUserBookmarks(
  userId: string | undefined,
  page: number | undefined,
  limit: number | undefined,
): [Page<Bookmark[]> | null, UseDataError | null, boolean] {
  const [bookmarks, setBookmarks] = React.useState<Page<Bookmark[]> | null>(null);
  const [error, setError] = React.useState<UseDataError | null>(null);
  const [loading, setLoading] = React.useState<boolean>(true);

  React.useEffect(() => {
    setLoading(true);

    if (!userId) {
      setBookmarks(null);
      setError(null);
      setLoading(false);
      return;
    }

    paginationApiClient
      .get<Page<Bookmark[]> | UseDataError>(`/api/users/${userId}/bookmarks`, {
        params: {
          pageNo: page,
          pageSize: limit,
        },
      })
      .then((response) => {
        setBookmarks(response.data as Page<Bookmark[]>);
        setError(null);
      })
      .catch((error) => {
        setBookmarks(null);
        setError(error.response.data);
      })
      .finally(() => {
        setLoading(false);
      });
  }, [userId, page, limit]);

  return [bookmarks, error, loading];
}

export function useIsBookmark(
  userId: string | undefined,
  tourSpotId: string | undefined,
): [boolean, UseDataError | null, boolean] {
  const [isBookmark, setIsBookmark] = React.useState<boolean>(false);
  const [error, setError] = React.useState<UseDataError | null>(null);
  const [loading, setLoading] = React.useState<boolean>(true);

  React.useEffect(() => {
    setLoading(true);

    if (!userId || !tourSpotId) {
      setIsBookmark(false);
      setError(null);
      setLoading(false);
      return;
    }

    envelopeApiClient
      .get<Response<Bookmark, unknown>>(`/api/users/${userId}/bookmarks/${tourSpotId}`)
      .then((response) => {
        if (response.data.success) {
          setIsBookmark(true);
        } else {
          setIsBookmark(false);
        }
        setError(null);
      })
      .catch((error) => {
        setIsBookmark(false);
        setError(error.response.data);
      })
      .finally(() => {
        setLoading(false);
      });
  }, [userId, tourSpotId]);

  return [isBookmark, error, loading];
}
