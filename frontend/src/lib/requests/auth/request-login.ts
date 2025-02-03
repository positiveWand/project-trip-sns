import { Response } from '../request';
import { User, UserCredentials, testuser } from './auth-type';

export interface LoginRequest {
  credentials: UserCredentials;
}

export type LoginError = 'INVALID_CREDENTIALS';

export interface LoginResponseBody {
  user: User;
}

type LoginResponse = Response<LoginResponseBody, LoginError>;

export function requestLogin({ credentials }: LoginRequest): LoginResponse {
  // 세션 로그인
  if (credentials.id != testuser.id || credentials.password != testuser.password) {
    return {
      success: false,
      error: {
        type: 'INVALID_CREDENTIALS',
        message: '아이디 또는 비밀번호가 맞지 않습니다.',
      },
    };
  }

  sessionStorage.setItem('session', JSON.stringify(testuser));

  return {
    success: true,
    body: {
      user: {
        name: testuser.name,
        id: testuser.id,
        email: testuser.email,
      },
    },
  };
}
