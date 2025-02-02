import * as React from 'react';

export interface SessionInfo {}

export interface Session<T extends SessionInfo> {
  active: boolean;
  info: T | null;
}

export interface SessionCredentials {}

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

export interface SessionActionResult<T extends SessionInfo> {
  success: boolean;
  info: T | null;
}

export interface CreateSessionResult<T extends SessionInfo> extends SessionActionResult<T> {
  error?: {
    type: 'NO_DEFINITION' | 'INVALID_CREDENTIALS' | 'NETWORK_FAILURE' | 'ERROR';
    message: string;
  };
}
export interface DestroySessionResult<T extends SessionInfo> extends SessionActionResult<T> {
  error?: {
    type: 'NO_DEFINITION' | 'NO_SESSION' | 'NETWORK_FAILURE' | 'ERROR';
    message: string;
  };
}
export interface CheckSessionResult<T extends SessionInfo> extends SessionActionResult<T> {
  active: boolean;
  error?: {
    type: 'NO_DEFINITION' | 'NETWORK_FAILURE' | 'ERROR';
    message: string;
  };
}

export interface SessionActions<T extends SessionInfo, U extends SessionCredentials> {
  createSession: (credential?: U) => CreateSessionResult<T>;
  destroySession: (credential?: U) => DestroySessionResult<T>;
  checkSession: () => CheckSessionResult<T>;
}

export const SessionContext = React.createContext<Session<any>>({ active: false, info: null });
export const SessionActionsContext = React.createContext<SessionActions<any, any>>({
  createSession: () => ({
    success: false,
    info: null,
    error: {
      type: 'NO_DEFINITION',
      message: '세션 생성 방법이 정의되지 않았습니다.',
    },
  }),
  destroySession: () => ({
    success: false,
    info: null,
    error: {
      type: 'NO_DEFINITION',
      message: '세션 제거 방법이 정의되지 않았습니다.',
    },
  }),
  checkSession: () => ({
    success: false,
    active: false,
    info: null,
    error: {
      type: 'NO_DEFINITION',
      message: '세션 확인 방법이 정의되지 않았습니다.',
    },
  }),
});

export interface SessionProviderProps {
  actions: SessionActions<any, any>;
  children?: React.ReactNode;
}

export const SessionProvider = ({ actions, children }: SessionProviderProps) => {
  const [session, dispatch] = React.useReducer(reducer, {
    active: false,
    info: null,
  });

  // mount 시 세션 확인
  React.useEffect(() => {
    const result = actions.checkSession();
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
  }, [actions]);

  const actions_proxy = React.useMemo<SessionActions<any, any>>(
    () => ({
      createSession(credential) {
        // 세션 로그인
        const result = actions.createSession(credential);

        if (result.success) {
          dispatch({
            type: 'ACTIVATE_SESSION',
            info: result.info,
          });
        }

        return result;
      },
      destroySession(credential) {
        // 세션 로그아웃
        const result = actions.destroySession(credential);

        dispatch({
          type: 'DEACTIVATE_SESSION',
        });

        return result;
      },
      checkSession() {
        // 세션 확인
        const result = actions.checkSession();

        if (result.success) {
          dispatch({
            type: 'ACTIVATE_SESSION',
            info: { ...result.info },
          });
        } else {
          dispatch({
            type: 'DEACTIVATE_SESSION',
          });
        }

        return result;
      },
    }),
    [],
  );

  return (
    <SessionContext.Provider value={session}>
      <SessionActionsContext.Provider value={actions_proxy}>
        {children}
      </SessionActionsContext.Provider>
    </SessionContext.Provider>
  );
};
