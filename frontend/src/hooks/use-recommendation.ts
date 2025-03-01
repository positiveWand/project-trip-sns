import { apiClient } from '@/lib/requests/request';
import * as React from 'react';

export interface UseDataError {
  error: string;
  message: string;
}

export interface TourSpotRecommendation {
  id: string;
  name: string;
  address: string;
  lat: number;
  lng: number;
  imageUrl: string;
  tags: string[];
}

export function useTourSpotRecommendations(
  type: string,
): [TourSpotRecommendation[], UseDataError | null, boolean] {
  const [recommendations, setRecommendations] = React.useState<TourSpotRecommendation[]>([]);
  const [error, setError] = React.useState<UseDataError | null>(null);
  const [loading, setLoading] = React.useState<boolean>(true);

  React.useEffect(() => {
    setLoading(true);

    apiClient
      .get<TourSpotRecommendation[] | UseDataError>(`/recommendations/${type}`)
      .then((response) => {
        setRecommendations(response.data as TourSpotRecommendation[]);
        setError(null);
      })
      .catch((error) => {
        setRecommendations([]);
        setError(error.response.data);
      })
      .finally(() => {
        setLoading(false);
      });
  }, [type]);

  return [recommendations, error, loading];
}
