import { MapInfoBar } from './map-info-bar';
import { cn } from '@/lib/utils';
import { useEffect, useRef, useState, ComponentProps, Reducer } from 'react';
import { useNaverMap, useNaverMapAction } from '@/hooks/use-naver-map';
import {
  useAllUrlSearchParam,
  useChangeUrl,
  useUrlPathParam,
  useUrlSearchParam,
} from '@/hooks/use-url';
import { useTourSpot, useTourSpots } from '@/hooks/use-tour-spot';
import { MAP_PAGE } from '@/config';

// Configuration 상수
const INIT_CENTER = new naver.maps.LatLng(33.37521429272602, 126.53454937152777);
const INIT_ZOOM = 10;
const FOCUS_ZOOM = 12;

export interface MapProps extends ComponentProps<'div'> {}

export function Map({ className }: MapProps) {
  const map = useNaverMap();
  const naverMapAction = useNaverMapAction();
  const mapContainer = useRef<HTMLDivElement>(null);

  const [query] = useUrlSearchParam('query', '');
  const [tags] = useAllUrlSearchParam('tags');
  const [customFilters] = useAllUrlSearchParam('customFilters');
  const [{ focusedTourSpotId }, setPathParam] = useUrlPathParam('/tourSpot/:focusedTourSpotId');

  const [tourSpots] = useTourSpots(query, tags, customFilters, undefined, 1, 100);
  const [focusedTourSpot] = useTourSpot(focusedTourSpotId);

  const [markers, setMarkers] = useState<Record<string, naver.maps.Marker>>({});
  const [focusedMarker, setFocusedMarker] = useState<naver.maps.Marker | null>(null);

  const changeUrl = useChangeUrl(MAP_PAGE, true);

  useEffect(() => {
    const newMap = new naver.maps.Map(mapContainer.current!, {
      center: INIT_CENTER,
      zoom: INIT_ZOOM,
    });

    naverMapAction({
      type: 'INIT_MAP',
      map: newMap,
    });

    return () => {
      newMap.destroy();
      naverMapAction({
        type: 'DESTROY_MAP',
      });
    };
  }, []);

  useEffect(() => {
    if (!map || !focusedTourSpot) {
      focusedMarker?.setMap(null);
      return;
    }

    const focusCoord = new naver.maps.LatLng(focusedTourSpot.lat, focusedTourSpot.lng);

    map.morph(focusCoord, FOCUS_ZOOM);

    const marker = new naver.maps.Marker({
      position: focusCoord,
      map: map,
      animation: naver.maps.Animation.BOUNCE,
      zIndex: 100,
    });

    focusedMarker?.setMap(null);
    setFocusedMarker(marker);

    return () => {
      focusedMarker?.setMap(null);
      setFocusedMarker(marker);
    };
  }, [map, focusedTourSpot]);

  useEffect(() => {
    if (!map || !tourSpots) {
      return;
    }

    Object.entries(markers).forEach(([tourSpotId, marker]) => {
      if (tourSpotId == focusedTourSpotId || !marker) {
        return;
      }

      marker.setMap(null);
    });

    tourSpots.data.forEach((tourSpot) => {
      if (tourSpot.id == focusedTourSpotId) {
        return;
      }

      const marker = new naver.maps.Marker({
        position: new naver.maps.LatLng(tourSpot.lat, tourSpot.lng),
        map: map,
      });
      markers[tourSpot.id] = marker;

      naver.maps.Event.addListener(marker, 'click', () => {
        setPathParam('focusedTourSpotId', tourSpot.id);
      });
    });

    return () => {
      Object.entries(markers).forEach(([tourSpotId, marker]) => {
        if (tourSpotId == focusedTourSpotId || !marker) {
          return;
        }

        marker.setMap(null);
      });
      setMarkers({});
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
