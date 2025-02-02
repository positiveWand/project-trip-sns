import * as React from 'react';
import { Session, SessionContext } from '@/context/session-context';
import { User, UserSessionInfo } from '@/context/user-session-context';

export function useUser(): User | null {
  const session = React.useContext(SessionContext) as Session<UserSessionInfo>;
  const sessionIsActive = session.active;

  if (!sessionIsActive || !session.info || !session.info.user) {
    return null;
  }

  return session.info.user;
}
