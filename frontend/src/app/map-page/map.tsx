import { MapInfoBar } from './map-info-bar';
import { cn } from '@/lib/utils';
import { useEffect, useRef, useState, ComponentProps, Reducer } from 'react';
import { useFocusedMarker, useMap, useMarkers } from '@/hooks/use-map';
import {
  useAllUrlSearchParam,
  useChangeUrl,
  useUrlPathParam,
  useUrlSearchParam,
} from '@/hooks/use-url';
import { LatLngBound, useMapTourSpots, useTourSpot, useTourSpots } from '@/hooks/use-tour-spot';
import { MAP_PAGE } from '@/config';

// 지도 config
const INIT_CENTER = new naver.maps.LatLng(33.37521429272602, 126.53454937152777);
const INIT_ZOOM = 10;
const FOCUS_ZOOM = 12;

export interface MapProps extends ComponentProps<'div'> {}

export function Map({ className }: MapProps) {
  const [map, dispatchMap] = useMap();
  const [focusedMarker, dispatchFocusedMarker] = useFocusedMarker();
  const [markers, dispatchMarkers] = useMarkers();
  const mapContainer = useRef<HTMLDivElement>(null);

  const [query] = useUrlSearchParam('query', '');
  const [tags] = useAllUrlSearchParam('tags');
  const [customFilters] = useAllUrlSearchParam('customFilters');
  const [{ focusedTourSpotId }, setPathParam] = useUrlPathParam(
    MAP_PAGE + '/tourSpot/:focusedTourSpotId',
  );

  const [latLngBound, setLatLngBound] = useState<LatLngBound>();

  const [tourSpots] = useMapTourSpots(query, tags, customFilters, latLngBound);
  const [focusedTourSpot] = useTourSpot(focusedTourSpotId);

  const changeUrl = useChangeUrl(MAP_PAGE, true);

  useEffect(() => {
    if (!mapContainer.current) return;

    const newMap = new naver.maps.Map(mapContainer.current, {
      center: INIT_CENTER,
      zoom: INIT_ZOOM,
    });

    dispatchMap({
      type: 'INIT_MAP',
      map: newMap,
      mapDiv: mapContainer.current,
    });

    const bound = newMap.getBounds();
    setLatLngBound({
      minLat: bound.minY(),
      minLng: bound.minX(),
      maxLat: bound.maxY(),
      maxLng: bound.maxX(),
    });

    let debounceTimer: NodeJS.Timeout;
    let dragging = false;
    newMap.addListener('dragstart', () => {
      dragging = true;
    });
    newMap.addListener('dragend', () => {
      clearTimeout(debounceTimer);
      dragging = false;

      const bound = newMap.getBounds();
      setLatLngBound({
        minLat: bound.minY(),
        minLng: bound.minX(),
        maxLat: bound.maxY(),
        maxLng: bound.maxX(),
      });
    });
    newMap.addListener('bounds_changed', () => {
      if (debounceTimer) {
        clearTimeout(debounceTimer);
      }

      debounceTimer = setTimeout(() => {
        if (dragging) return;

        const bound = newMap.getBounds();
        setLatLngBound({
          minLat: bound.minY(),
          minLng: bound.minX(),
          maxLat: bound.maxY(),
          maxLng: bound.maxX(),
        });
      }, 300);
    });

    return () => {
      dispatchMap({
        type: 'DESTROY_MAP',
      });
    };
  }, []);

  useEffect(() => {
    if (!map || !focusedTourSpot) {
      dispatchFocusedMarker({
        type: 'REMOVE_FOCUSED_MARKER',
      });
      return;
    }

    dispatchMap({
      type: 'MORPH_MAP',
      coord: {
        lat: focusedTourSpot.lat,
        lng: focusedTourSpot.lng,
      },
      zoom: FOCUS_ZOOM,
    });

    dispatchFocusedMarker({
      type: 'SET_FOCUSED_MARKER',
      markerId: focusedTourSpot.id,
      position: {
        lat: focusedTourSpot.lat,
        lng: focusedTourSpot.lng,
      },
    });

    return () => {
      dispatchFocusedMarker({
        type: 'REMOVE_FOCUSED_MARKER',
      });
    };
  }, [map, focusedTourSpot]);

  useEffect(() => {
    if (!map || !tourSpots) {
      return;
    }

    dispatchMarkers({
      type: 'SET_MARKERS',
      markers: tourSpots.map((tourSpot) => {
        return {
          id: tourSpot.id,
          lat: tourSpot.lat,
          lng: tourSpot.lng,
          eventListener: {
            eventName: 'click',
            listener: () => {
              setPathParam('focusedTourSpotId', tourSpot.id);
            },
          },
        };
      }),
    });

    return () => {
      dispatchMarkers({
        type: 'REMOVE_MARKERS',
      });
    };
  }, [map, tourSpots, focusedTourSpotId]);

  return (
    <div className={cn('h-full w-full flex', className)}>
      <MapInfoBar
        open={focusedTourSpotId ? true : false}
        onOpenChange={changeUrl}
        className='w-1/4'
        info={focusedTourSpot}
      />
      <div id='map' ref={mapContainer} className='grow h-full'></div>
    </div>
  );
}
