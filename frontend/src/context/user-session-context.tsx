import * as React from 'react';
import {
  SessionProvider,
  SessionActions,
  SessionCredentials,
  SessionInfo,
} from './session-context';

export interface User {
  name: string;
  id: string;
  email: string;
}

export interface UserSessionInfo extends SessionInfo {
  user: User;
}

export interface UserSessionCredentials extends SessionCredentials {
  id: string;
  password: string;
}

export interface SessionProviderProps {
  children?: React.ReactNode;
}

export const UserSessionProvider = ({ children }: SessionProviderProps) => {
  // 테스트 세션
  const testuser = {
    name: '신경방',
    id: 'singbhang',
    password: 'password123!',
    email: 'singbhang@gmail.com',
  };

  const actions = React.useMemo<SessionActions<UserSessionInfo, UserSessionCredentials>>(
    () => ({
      createSession(credentials) {
        if (!credentials) {
          throw Error('[UserSession.createSession] 세션 생성을 위해서는 인증정보가 필요합니다.');
        }

        // 세션 로그인
        if (credentials.id != testuser.id || credentials.password != testuser.password) {
          return {
            success: false,
            info: null,
            error: {
              type: 'INVALID_CREDENTIALS',
              message: '아이디 또는 비밀번호가 맞지 않습니다.',
            },
          };
        }

        sessionStorage.setItem('session', JSON.stringify(testuser));

        return {
          success: true,
          info: {
            user: {
              name: testuser.name,
              id: testuser.id,
              email: testuser.email,
            },
          },
        };
      },
      destroySession() {
        // 세션 로그아웃
        sessionStorage.removeItem('session');

        return {
          success: true,
          info: null,
        };
      },
      checkSession() {
        // 세션 확인
        if (sessionStorage.getItem('session')) {
          return {
            success: true,
            active: true,
            info: {
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
            active: false,
            info: null,
          };
        }
      },
    }),
    [],
  );

  return <SessionProvider actions={actions}>{children}</SessionProvider>;
};
