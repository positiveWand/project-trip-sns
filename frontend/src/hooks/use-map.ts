import { NaverMapContext, NaverMapActionContext } from '@/context/naver-map-context';
import * as React from 'react';

export function useNaverMap() {
  const map = React.useContext(NaverMapContext);

  return map;
}

export function useNaverMapAction() {
  const mapAction = React.useContext(NaverMapActionContext);

  return mapAction;
}
