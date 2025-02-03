import * as React from 'react';

export interface SessionInfo {}

export interface Session<T extends SessionInfo> {
  active: boolean;
  info: T | null;
}

type Action =
  | {
      type: 'ACTIVATE_SESSION';
      info: SessionInfo | null;
    }
  | {
      type: 'DEACTIVATE_SESSION';
    };

const reducer: React.Reducer<Session<any>, Action> = (state, action) => {
  switch (action.type) {
    case 'ACTIVATE_SESSION':
      return {
        active: true,
        info: { ...action.info },
      };

    case 'DEACTIVATE_SESSION':
      return {
        active: false,
        info: null,
      };
  }
};

export interface CheckSessionResult<T extends SessionInfo> {
  success: boolean;
  active: boolean;
  info: T | null;
}

export type CheckSession<T extends SessionInfo, U> = (credential?: U) => CheckSessionResult<T>;

export const SessionContext = React.createContext<Session<any>>({ active: false, info: null });
export const SessionActionsContext = React.createContext<React.Dispatch<Action>>(() => {
  throw new Error('세션 사용을 위해 Context가 제공되어야합니다.');
});

export interface SessionProviderProps {
  checkSession: CheckSession<any, any>;
  children?: React.ReactNode;
}

export const SessionProvider = ({ checkSession, children }: SessionProviderProps) => {
  const [session, dispatch] = React.useReducer(reducer, {
    active: false,
    info: null,
  });

  // mount 시 세션 확인
  React.useEffect(() => {
    const result = checkSession();
    console.log(result);

    if (result.active) {
      dispatch({
        type: 'ACTIVATE_SESSION',
        info: { ...result.info },
      });
    } else {
      dispatch({
        type: 'DEACTIVATE_SESSION',
      });
    }
  }, [checkSession]);

  return (
    <SessionContext.Provider value={session}>
      <SessionActionsContext.Provider value={dispatch}>{children}</SessionActionsContext.Provider>
    </SessionContext.Provider>
  );
};
