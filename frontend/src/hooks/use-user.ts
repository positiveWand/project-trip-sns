import { apiClient, paginationApiClient, Page } from '@/lib/requests/request';
import * as React from 'react';

export interface UseDataError {
  error: string;
  message: string;
}

export interface UserProfile {
  id: string;
  name: string;
}

export function useUsers(
  query: string | undefined,
  page: number | undefined,
  limit: number | undefined,
): [Page<UserProfile[]> | null, UseDataError | null, boolean] {
  const [users, setUsers] = React.useState<Page<UserProfile[]> | null>(null);
  const [error, setError] = React.useState<UseDataError | null>(null);
  const [loading, setLoading] = React.useState<boolean>(true);

  React.useEffect(() => {
    setLoading(true);

    paginationApiClient
      .get<Page<UserProfile[]> | UseDataError>('/api/users', {
        params: {
          query: query,
          pageNo: page,
          pageSize: limit,
        },
      })
      .then((response) => {
        setUsers(response.data as Page<UserProfile[]>);
        setError(null);
      })
      .catch((error) => {
        setUsers(null);
        setError(error.response.data);
      })
      .finally(() => {
        setLoading(false);
      });
  }, [query, page, limit]);

  return [users, error, loading];
}

export function useUser(
  id: string | undefined,
): [UserProfile | null, UseDataError | null, boolean] {
  const [user, setUser] = React.useState<UserProfile | null>(null);
  const [error, setError] = React.useState<UseDataError | null>(null);
  const [loading, setLoading] = React.useState<boolean>(true);

  React.useEffect(() => {
    setLoading(true);

    if (!id) {
      setUser(null);
      setError(null);
      setLoading(false);
      return;
    }

    apiClient
      .get<UserProfile | UseDataError>(`/api/users/${id}`)
      .then((response) => {
        setUser(response.data as UserProfile);
      })
      .catch((error) => {
        setError(error.response.data);
        setUser(null);
      })
      .finally(() => {
        setLoading(false);
      });
  }, [id]);

  return [user, error, loading];
}
