import { Response } from '../request';

export interface LogoutRequest {}

export type LogoutError = never;

export interface LogoutResponseBody {}

type LogoutResponse = Response<LogoutResponseBody | null, LogoutError>;

export function requestLogout({}: LogoutRequest): LogoutResponse {
  sessionStorage.removeItem('session');

  return {
    success: true,
    body: null,
  };
  return {
    success: false,
    error: {
      type: 'NETWORK_ERROR',
      message: '서버가 응답하지 않습니다.',
    },
  };
}
