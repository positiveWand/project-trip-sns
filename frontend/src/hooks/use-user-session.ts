import * as React from 'react';
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

const userSession: UserSession = {
  active: false,
  info: null,
};

export async function checkSession() {
  const response = await requestMe();
  console.log(response);
  if (response.success && response.data) {
    userSession.active = true;
    userSession.info = {
      id: response.data.id,
      name: response.data.name,
      email: response.data.email,
    };
  }
}

export function useUserSession(): [boolean, User | null] {
  console.log(userSession);
  return [userSession.active, userSession.info];
}
