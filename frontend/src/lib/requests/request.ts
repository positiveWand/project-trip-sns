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

export interface PageInfo {
  page: number; // 페이지 번호
  pageLimit: number; // 페이지 최대크기
  pageSize: number; // 페이지 크기
  totalPage: number; // 총 페이지 개수
  totalItem: number; // 총 아이템 개수
}

export type Page<T> = PageInfo & {
  data: T;
};

const SERVER_URL = '';
const TIMEOUT = 5000;

export const apiClient = axios.create({
  baseURL: SERVER_URL,
  timeout: TIMEOUT,
  withCredentials: true,
  paramsSerializer: {
    indexes: null,
  },
});

export const envelopeApiClient = axios.create({
  baseURL: SERVER_URL,
  timeout: TIMEOUT,
  withCredentials: true,
  paramsSerializer: {
    indexes: null,
  },
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

export const paginationApiClient = axios.create({
  baseURL: SERVER_URL,
  timeout: TIMEOUT,
  withCredentials: true,
  paramsSerializer: {
    indexes: null,
  },
});

paginationApiClient.interceptors.response.use((response) => {
  response.data = {
    page: parseInt(response.headers['x-pagination-page']),
    pageLimit: parseInt(response.headers['x-pagination-page-limit']),
    pageSize: parseInt(response.headers['x-pagination-page-size']),
    totalPage: parseInt(response.headers['x-pagination-total-page']),
    totalItem: parseInt(response.headers['x-pagination-total-item']),
    data: response.data,
  };
  return response;
});

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
