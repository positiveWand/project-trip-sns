import * as React from 'react';
import { SessionProvider, SessionInfo, CheckSession } from './session-context';
import { UserCredentials } from '@/lib/requests/auth/auth-type';
import { requestCheck } from '@/lib/requests/auth/request-check';

export interface User {
  name: string;
  id: string;
  email: string;
}

export interface UserSessionInfo extends SessionInfo {
  user: User | null;
}

export interface SessionProviderProps {
  children?: React.ReactNode;
}

export const UserSessionProvider = ({ children }: SessionProviderProps) => {
  const checkSession: CheckSession<UserSessionInfo, UserCredentials> = () => {
    const response = requestCheck({});
    if (response.success) {
      return {
        success: true,
        active: response.body.active,
        info: {
          user: response.body.user,
        },
      };
    } else {
      return {
        success: false,
        active: false,
        info: null,
      };
    }
  };

  return <SessionProvider checkSession={checkSession}>{children}</SessionProvider>;
};
