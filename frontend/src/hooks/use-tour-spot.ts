import { apiClient, paginationApiClient, Page } from '@/lib/requests/request';
import * as React from 'react';

export interface UseDataError {
  error: string;
  message: string;
}

export interface TourSpot {
  id: string;
  name: string;
  address: string;
  lat: number;
  lng: number;
  imageUrl: string;
  description: string;
  phoneNumber: string;
  tags: string[];
}

export type TourSpotOverview = Omit<TourSpot, 'description' | 'phoneNumber'>;

export interface LatLngBound {
  minLat: number;
  minLng: number;
  maxLat: number;
  maxLng: number;
}

export function useTourSpots(
  query: string | undefined,
  tags: string[] | undefined,
  customFilters: string[] | undefined,
  sort: string | undefined,
  page: number | undefined,
  limit: number | undefined,
): [Page<TourSpotOverview[]> | null, UseDataError | null, boolean] {
  const [tourSpots, setTourSpots] = React.useState<Page<TourSpotOverview[]> | null>(null);
  const [error, setError] = React.useState<UseDataError | null>(null);
  const [loading, setLoading] = React.useState<boolean>(true);

  React.useEffect(() => {
    setLoading(true);

    paginationApiClient
      .get<Page<TourSpotOverview[]> | UseDataError>('/api/tour-spots', {
        params: {
          query: query,
          tags: tags,
          customFilters: customFilters,
          sort: sort,
          pageNo: page,
          pageSize: limit,
        },
      })
      .then((response) => {
        setTourSpots(response.data as Page<TourSpotOverview[]>);
        setError(null);
      })
      .catch((error) => {
        setTourSpots(null);
        setError(error.response.data);
      })
      .finally(() => {
        setLoading(false);
      });
  }, [query, tags, customFilters, sort, page, limit]);

  return [tourSpots, error, loading];
}

export function useMapTourSpots(
  query: string | undefined,
  tags: string[] | undefined,
  customFilters: string[] | undefined,
  latLngBound: LatLngBound | undefined,
): [TourSpotOverview[] | null, UseDataError | null, boolean] {
  const [tourSpots, setTourSpots] = React.useState<TourSpotOverview[] | null>(null);
  const [error, setError] = React.useState<UseDataError | null>(null);
  const [loading, setLoading] = React.useState<boolean>(true);

  React.useEffect(() => {
    setLoading(true);

    apiClient
      .get<TourSpotOverview[] | UseDataError>('/api/tour-spots/map', {
        params: {
          query: query,
          tags: tags,
          customFilters: customFilters,
          minLat: latLngBound?.minLat,
          minLng: latLngBound?.minLng,
          maxLat: latLngBound?.maxLat,
          maxLng: latLngBound?.maxLng,
        },
      })
      .then((response) => {
        setTourSpots(response.data as TourSpotOverview[]);
        setError(null);
      })
      .catch((error) => {
        setTourSpots(null);
        setError(error.response.data);
      })
      .finally(() => {
        setLoading(false);
      });
  }, [query, tags, customFilters, latLngBound]);

  return [tourSpots, error, loading];
}

export function useTourSpot(
  id: string | undefined,
): [TourSpot | null, UseDataError | null, boolean] {
  const [tourSpot, setTourSpot] = React.useState<TourSpot | null>(null);
  const [error, setError] = React.useState<UseDataError | null>(null);
  const [loading, setLoading] = React.useState<boolean>(true);

  React.useEffect(() => {
    setLoading(true);

    if (!id) {
      setTourSpot(null);
      setError(null);
      setLoading(false);
      return;
    }

    apiClient
      .get<TourSpot | Error>(`/api/tour-spots/${id}`)
      .then((response) => {
        setTourSpot(response.data as TourSpot);
        setError(null);
      })
      .catch((error) => {
        setTourSpot(null);
        setError(error.response.data);
      })
      .finally(() => {
        setLoading(false);
      });
  }, [id]);

  return [tourSpot, error, loading];
}
