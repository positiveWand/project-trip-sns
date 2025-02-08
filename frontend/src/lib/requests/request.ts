import axios from 'axios';

export interface SuccessResponse<T> {
  success: true;
  data: T;
}

export type BaseError = 'NETWORK_ERROR' | 'TIMEOUT_ERROR' | 'UNKNOWN';
export interface ErrorResponse<T> {
  success: false;
  error: T | BaseError;
  message: string;
}

export type Response<T, U> = SuccessResponse<T> | ErrorResponse<U>;

const SERVER_URL = '';
const TIMEOUT = 5000;

export const apiClient = axios.create({
  baseURL: SERVER_URL,
  timeout: TIMEOUT,
  withCredentials: true,
});

export const envelopeApiClient = axios.create({
  baseURL: SERVER_URL,
  timeout: TIMEOUT,
  withCredentials: true,
});

envelopeApiClient.interceptors.response.use(
  (response) => {
    response.data = {
      success: true,
      data: response.data,
    };
    return response;
  },
  (error) => {
    if (axios.isAxiosError(error) && error.response) {
      error.response.data = {
        success: false,
        ...error.response.data,
      };
      return Promise.resolve(error.response);
    }

    return Promise.reject(error);
  },
);

export const defaultErrorHandler = (error: unknown): ErrorResponse<any> => {
  if (axios.isAxiosError(error)) {
    if (error.code == 'ECONNABORTED') {
      return {
        success: false,
        error: 'TIMEOUT_ERROR',
        message: '서버 응답이 너무 오래걸립니다.',
      };
    } else if (!error.response) {
      return {
        success: false,
        error: 'NETWORK_ERROR',
        message: '네트워크 사용이 원활하지 않아 요청에 실패했습니다.',
      };
    }
  }

  return {
    success: false,
    error: 'UNKNOWN',
    message: '알 수 없는 이유로 요청에 실패했습니다.',
  };
};
