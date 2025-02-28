import { requestMe } from '@/lib/requests/request-auth';

export interface User {
  id: string;
  name: string;
  email: string;
}

export interface UserSession {
  active: boolean;
  info: User | null;
}

const USER_SESSION: UserSession = {
  active: false,
  info: null,
};

export async function checkSession() {
  const response = await requestMe();
  if (response.success && response.data) {
    USER_SESSION.active = true;
    USER_SESSION.info = {
      id: response.data.id,
      name: response.data.name,
      email: response.data.email,
    };
  }
}

export function useUserSession(): [boolean, User | null] {
  return [USER_SESSION.active, USER_SESSION.info];
}
