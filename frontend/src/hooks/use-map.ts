import * as React from 'react';

type MapState = {
  map: naver.maps.Map | null;
  mapDiv: HTMLElement | null;
  focusedMarker: {
    id: string;
    marker: naver.maps.Marker;
  } | null;
  markers: Record<string, naver.maps.Marker>;
};

const MAP_STATE: MapState = {
  map: null,
  mapDiv: null,
  focusedMarker: null,
  markers: {},
};

type MapAction =
  | {
      type: 'INIT_MAP';
      map?: naver.maps.Map;
      mapDiv: HTMLElement;
      mapOptions?: naver.maps.MapOptions;
    }
  | {
      type: 'DESTROY_MAP';
    }
  | {
      type: 'MORPH_MAP';
      coord: {
        lat: number;
        lng: number;
      };
      zoom?: number;
    };
function dispatchMap(action: MapAction) {
  switch (action.type) {
    case 'INIT_MAP':
      MAP_STATE.map?.destroy();
      if (action.map) {
        MAP_STATE.map = action.map;
      } else {
        MAP_STATE.map = new naver.maps.Map(action.mapDiv, action.mapOptions);
      }
      MAP_STATE.mapDiv = action.mapDiv;
      window.dispatchEvent(new Event('x-mapaction'));
      break;
    case 'DESTROY_MAP':
      MAP_STATE.map?.destroy();
      MAP_STATE.map = null;
      MAP_STATE.mapDiv = null;
      window.dispatchEvent(new Event('x-mapaction'));
      break;
    case 'MORPH_MAP':
      MAP_STATE.map?.morph(action.coord, action.zoom);
      break;
  }
}

type FocusedMarkerAction =
  | {
      type: 'SET_FOCUSED_MARKER';
      markerId: string;
      position: {
        lat: number;
        lng: number;
      };
    }
  | {
      type: 'REMOVE_FOCUSED_MARKER';
    };
function dispatchFocusedMarker(action: FocusedMarkerAction) {
  switch (action.type) {
    case 'SET_FOCUSED_MARKER':
      if (!MAP_STATE.map) {
        throw Error('네이버 지도 객체가 없습니다.');
      }

      if (MAP_STATE.markers[action.markerId]) {
        MAP_STATE.markers[action.markerId]?.setMap(null);
      }

      MAP_STATE.focusedMarker?.marker.setMap(null);

      MAP_STATE.focusedMarker = {
        id: action.markerId,
        marker: new naver.maps.Marker({
          position: new naver.maps.LatLng(action.position.lat, action.position.lng),
          map: MAP_STATE.map,
          animation: naver.maps.Animation.BOUNCE,
          zIndex: 100,
        }),
      };
      break;
    case 'REMOVE_FOCUSED_MARKER':
      if (!MAP_STATE.map) {
        throw Error('지도 객체가 없습니다.');
      }

      const focusedMarkerId = MAP_STATE.focusedMarker?.id;
      if (focusedMarkerId && MAP_STATE.markers[focusedMarkerId]) {
        MAP_STATE.markers[focusedMarkerId].setMap(MAP_STATE.map);
      }

      MAP_STATE.focusedMarker?.marker.setMap(null);
      MAP_STATE.focusedMarker = null;

      break;
  }
  MAP_STATE.mapDiv?.dispatchEvent(new Event('x-focusedmarkeraction'));
}

type MarkersAction =
  | {
      type: 'SET_MARKERS';
      markers: {
        id: string;
        lat: number;
        lng: number;
        eventListener?: {
          eventName: string;
          listener: (event: any) => any;
        };
      }[];
    }
  | {
      type: 'REMOVE_MARKERS';
    };
function dispatchMarkers(action: MarkersAction) {
  switch (action.type) {
    case 'SET_MARKERS':
      if (!MAP_STATE.map) {
        throw Error('네이버 지도 객체가 없습니다.');
      }

      const newMarkers: typeof MAP_STATE.markers = {};

      action.markers.forEach(({ id, lat, lng, eventListener }) => {
        if (id in MAP_STATE.markers) {
          newMarkers[id] = MAP_STATE.markers[id];
          return;
        }

        const marker = new naver.maps.Marker({
          position: new naver.maps.LatLng(lat, lng),
          map: MAP_STATE.map!,
        });

        if (MAP_STATE.focusedMarker && MAP_STATE.focusedMarker.id == id) {
          marker.setMap(null);
        }

        newMarkers[id] = marker;

        if (eventListener) {
          naver.maps.Event.addListener(marker, eventListener.eventName, eventListener.listener);
        }
      });

      Object.entries(MAP_STATE.markers).map((entry) => {
        if (!(entry[0] in newMarkers)) {
          entry[1].setMap(null);
        }
      });

      MAP_STATE.markers = newMarkers;

      break;
    case 'REMOVE_MARKERS':
      if (!MAP_STATE.map) {
        throw Error('지도 객체가 없습니다.');
      }

      Object.values(MAP_STATE.markers).map((marker) => {
        marker.setMap(null);
      });

      MAP_STATE.markers = {};

      break;
  }
  MAP_STATE.mapDiv?.dispatchEvent(new Event('x-markersaction'));
}

export function useMap(): [typeof MAP_STATE.map, typeof dispatchMap] {
  const subscribeNaverMap = React.useCallback((onStoreChange: () => void) => {
    window.addEventListener('x-setmap', onStoreChange);
    return () => {
      window.removeEventListener('x-setmap', onStoreChange);
    };
  }, []);

  const naverMap = React.useSyncExternalStore(subscribeNaverMap, () => {
    return MAP_STATE.map;
  });

  return [naverMap, dispatchMap];
}

export function useMapDiv(): [typeof MAP_STATE.mapDiv, typeof dispatchMap] {
  const subscribeNaverMap = React.useCallback((onStoreChange: () => void) => {
    window.addEventListener('x-setmap', onStoreChange);
    return () => {
      window.removeEventListener('x-setmap', onStoreChange);
    };
  }, []);

  const naverMapDiv = React.useSyncExternalStore(subscribeNaverMap, () => {
    return MAP_STATE.mapDiv;
  });

  return [naverMapDiv, dispatchMap];
}

export function useFocusedMarker(): [typeof MAP_STATE.focusedMarker, typeof dispatchFocusedMarker] {
  const [naverMapDiv] = useMapDiv();
  const subscribeNaverMap = React.useCallback(
    (onStoreChange: () => void) => {
      naverMapDiv?.addEventListener('x-focusedmarkeraction', onStoreChange);
      return () => {
        naverMapDiv?.removeEventListener('x-focusedmarkeraction', onStoreChange);
      };
    },
    [naverMapDiv],
  );
  const focusedMarker = React.useSyncExternalStore(subscribeNaverMap, () => {
    return MAP_STATE.focusedMarker;
  });

  return [focusedMarker, dispatchFocusedMarker];
}

export function useMarkers(): [typeof MAP_STATE.markers, typeof dispatchMarkers] {
  const [naverMapDiv] = useMapDiv();
  const subscribeNaverMap = React.useCallback(
    (onStoreChange: () => void) => {
      naverMapDiv?.addEventListener('x-setmap', onStoreChange);
      return () => {
        naverMapDiv?.removeEventListener('x-setmap', onStoreChange);
      };
    },
    [naverMapDiv],
  );
  const markers = React.useSyncExternalStore(subscribeNaverMap, () => {
    return MAP_STATE.markers;
  });

  return [markers, dispatchMarkers];
}
