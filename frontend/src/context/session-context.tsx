import * as React from 'react';

export interface Session {
  active: boolean;
  user: {
    name: string;
    id: string;
    email: string;
  } | null;
}

export interface ActionResult {
  success: boolean;
  description: string;
}

export interface LoginActionResult extends ActionResult {}
export interface LogoutActionResult extends ActionResult {}
export interface CheckActionResult extends ActionResult {
  active: boolean;
}

export interface SessionActions {
  sessionLogin: () => LoginActionResult;
  sessionLogout: () => LogoutActionResult;
  sessionCheck: () => CheckActionResult;
}

const SessionContext = React.createContext<Session>({ active: false, user: null });
const SessionActionsContext = React.createContext<SessionActions>({
  sessionLogin: () => ({ success: false, description: '세션 생성 방법이 정의되지 않았습니다.' }),
  sessionLogout: () => ({ success: false, description: '세션 생성 방법이 정의되지 않았습니다.' }),
  sessionCheck: () => ({
    success: false,
    description: '세션 생성 방법이 정의되지 않았습니다.',
    active: false,
  }),
});

export interface SessionProviderProps {
  children?: React.ReactNode;
}

const SessionProvider = ({ children }: SessionProviderProps) => {
  const [session, setSession] = React.useState<Session>({ active: false, user: null });

  React.useEffect(() => {}, []);

  const actions = React.useMemo<SessionActions>(
    () => ({
      sessionLogin() {
        // 세션 로그인
        // 테스트 세션
        setSession({
          active: true,
          user: {
            name: '신경방',
            id: 'singbhang',
            email: 'singbhang@gmail.com',
          },
        });

        return {
          success: true,
          description: '로그인 성공.',
        };
      },
      sessionLogout() {
        // 세션 로그아웃
        setSession({
          active: false,
          user: null,
        });

        return {
          success: true,
          description: '로그아웃 성공.',
        };
      },
      sessionCheck() {
        // 세션 확인
        if (session.active) {
          return {
            success: true,
            description: '인증된 상태입니다.',
            active: true,
          };
        } else {
          return {
            success: true,
            description: '인증된 상태되지 않은 상태입니다.',
            active: false,
          };
        }
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
  const { sessionLogin, sessionLogout } = React.useContext(SessionActionsContext);

  return { session, sessionLogin, sessionLogout };
}

function useUser() {
  const session = React.useContext(SessionContext);

  return session.user;
}

export { SessionProvider, useSession, useUser };
