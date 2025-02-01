import * as React from 'react';

export interface User {
  name: string;
  id: string;
  email: string;
}
export interface Session {
  active: boolean;
  user: User | null;
}

type Action =
  | {
      type: 'ACTIVATE_SESSION';
      user: User;
    }
  | {
      type: 'DEACTIVATE_SESSION';
    };

const reducer: React.Reducer<Session, Action> = (state, action) => {
  switch (action.type) {
    case 'ACTIVATE_SESSION':
      return {
        active: true,
        user: { ...action.user },
      };

    case 'DEACTIVATE_SESSION':
      return {
        active: false,
        user: null,
      };
  }
};

export interface SessionActionResult {
  success: boolean;
}

export interface CreateSessionResult extends SessionActionResult {
  error?: {
    type: 'NO_DEFINITION' | 'NO_ACCOUNT' | 'NETWORK_FAILURE';
    message: string;
  };
}
export interface DestroySessionResult extends SessionActionResult {
  error?: {
    type: 'NO_DEFINITION' | 'NO_SESSION' | 'NETWORK_FAILURE';
    message: string;
  };
}
export interface CheckSessionResult extends SessionActionResult {
  error?: {
    type: 'NO_DEFINITION' | 'NETWORK_FAILURE';
    message: string;
  };
}

export interface SessionActions {
  createSession: (id: string, password: string) => CreateSessionResult;
  destroySession: () => DestroySessionResult;
  checkSession: () => CheckSessionResult;
}

const SessionContext = React.createContext<Session>({ active: false, user: null });
const SessionActionsContext = React.createContext<SessionActions>({
  createSession: () => ({
    success: false,
    error: {
      type: 'NO_DEFINITION',
      message: '세션 생성 방법이 정의되지 않았습니다.',
    },
  }),
  destroySession: () => ({
    success: false,
    error: {
      type: 'NO_DEFINITION',
      message: '세션 제거 방법이 정의되지 않았습니다.',
    },
  }),
  checkSession: () => ({
    success: false,
    error: {
      type: 'NO_DEFINITION',
      message: '세션 확인 방법이 정의되지 않았습니다.',
    },
  }),
});

export interface SessionProviderProps {
  children?: React.ReactNode;
}

const SessionProvider = ({ children }: SessionProviderProps) => {
  const [session, dispatch] = React.useReducer(reducer, {
    active: false,
    user: null,
  });

  React.useEffect(() => {
    if (sessionStorage.getItem('session')) {
      dispatch({
        type: 'ACTIVATE_SESSION',
        user: JSON.parse(sessionStorage.getItem('session') as string) as User,
      });
    } else {
      dispatch({
        type: 'DEACTIVATE_SESSION',
      });
    }
  }, []);

  const actions = React.useMemo<SessionActions>(
    () => ({
      createSession(id, password) {
        // 세션 로그인
        // 테스트 세션

        const testuser = {
          name: '신경방',
          id: 'singbhang',
          password: 'password123!',
          email: 'singbhang@gmail.com',
        };

        if (id != testuser.id || password != testuser.password) {
          return {
            success: false,
            error: {
              type: 'NO_ACCOUNT',
              message: '아이디 또는 비밀번호가 맞지 않습니다.',
            },
          };
        }

        sessionStorage.setItem('session', JSON.stringify(testuser));

        dispatch({
          type: 'ACTIVATE_SESSION',
          user: testuser,
        });

        return {
          success: true,
        };
      },
      destroySession() {
        // 세션 로그아웃
        sessionStorage.removeItem('session');

        dispatch({
          type: 'DEACTIVATE_SESSION',
        });

        return {
          success: true,
        };
      },
      checkSession() {
        // 세션 확인
        if (sessionStorage.getItem('session')) {
          dispatch({
            type: 'ACTIVATE_SESSION',
            user: JSON.parse(sessionStorage.getItem('session') as string) as User,
          });
        } else {
          dispatch({
            type: 'DEACTIVATE_SESSION',
          });
        }

        return {
          success: true,
        };
      },
    }),
    [],
  );

  return (
    <SessionContext.Provider value={session}>
      <SessionActionsContext.Provider value={actions}>{children}</SessionActionsContext.Provider>
    </SessionContext.Provider>
  );
};

function useSession() {
  const session = React.useContext(SessionContext);
  const sessionIsActive = session.active;
  const { createSession, destroySession, checkSession } = React.useContext(SessionActionsContext);

  return { sessionIsActive, session, createSession, destroySession, checkSession };
}

function useUser() {
  const session = React.useContext(SessionContext);
  const sessionIsActive = session.active;

  if (!sessionIsActive) {
    return null;
  }

  return session.user;
}

export { SessionProvider, useSession, useUser };
