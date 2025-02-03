import * as React from 'react';
import { SessionContext, SessionActionsContext } from '@/context/session-context';

export function useSession() {
  const session = React.useContext(SessionContext);
  const sessionIsActive = session.active;
  const dispatchSession = React.useContext(SessionActionsContext);

  return { sessionIsActive, session, dispatchSession };
}
