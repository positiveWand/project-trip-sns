import { Response } from '../request';
import { User, testuser } from './auth-type';

export interface CheckRequest {}

export type CheckError = never;

export interface CheckResponseBody {
  active: boolean;
  user: User | null;
}

type CheckResponse = Response<CheckResponseBody, CheckError>;

export function requestCheck({}: CheckRequest): CheckResponse {
  if (sessionStorage.getItem('session')) {
    return {
      success: true,
      body: {
        active: true,
        user: {
          name: testuser.name,
          id: testuser.id,
          email: testuser.email,
        },
      },
    };
  } else {
    return {
      success: true,
      body: {
        active: false,
        user: null,
      },
    };
  }
}
