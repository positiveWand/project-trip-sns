import * as React from 'react';

type Action =
  | {
      type: 'INIT_MAP';
      map: naver.maps.Map;
    }
  | {
      type: 'DESTROY_MAP';
    };

export const NaverMapContext = React.createContext<naver.maps.Map | null>(null);
export const NaverMapActionContext = React.createContext<React.Dispatch<Action>>(() => {
  throw Error('MapActionContext이 정의되지 않았습니다.');
});

const reducer: React.Reducer<naver.maps.Map | null, Action> = (state, action) => {
  switch (action.type) {
    case 'INIT_MAP':
      return action.map;
    case 'DESTROY_MAP':
      if (!state) {
        throw Error('지도 객체가 없습니다.');
      }
      return null;
  }
};

export interface NaverMapProviderProps {
  children?: React.ReactNode;
}

export function MapProvider({ children }: NaverMapProviderProps) {
  const [map, dispatch] = React.useReducer(reducer, null);

  return (
    <NaverMapContext.Provider value={map}>
      <NaverMapActionContext.Provider value={dispatch}>{children}</NaverMapActionContext.Provider>
    </NaverMapContext.Provider>
  );
}
