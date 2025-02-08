import { Response, defaultErrorHandler, envelopeApiClient } from './request';

export interface ErrorResponse {
  error: string;
  message: string;
}

export interface User {
  id: string;
  password: string;
  name: string;
  email: string;
}

export type UserProfile = Pick<User, 'id' | 'name' | 'email'>;
export type UserProfilePatch = Pick<User, 'name' | 'email'>;
export interface PasswordPatch {
  oldPassword: string;
  newPassword: string;
}
export interface UserCredentials {
  id: string;
  password: string;
}

export type AuthorizationError = 'INVALID_CREDENTIALS' | 'UNAUTHORIZED' | 'DUPLICATE_USER';

export async function requestLogin(
  user: UserCredentials,
): Promise<Response<UserProfile, AuthorizationError>> {
  return envelopeApiClient
    .post<Response<UserProfile, AuthorizationError>>('/auth/login', user)
    .then((response) => {
      return response.data;
    })
    .catch(defaultErrorHandler);
}

export async function requestLogout(): Promise<Response<null, AuthorizationError>> {
  return envelopeApiClient
    .post<Response<null, AuthorizationError>>('/auth/logout')
    .then((response) => {
      return response.data;
    })
    .catch(defaultErrorHandler);
}

export async function requestMe(): Promise<Response<UserProfile, AuthorizationError>> {
  return envelopeApiClient
    .get<Response<UserProfile, AuthorizationError>>('/auth/me')
    .then((response) => {
      return response.data;
    })
    .catch(defaultErrorHandler);
}

export async function requestSignup(user: User): Promise<Response<User, AuthorizationError>> {
  return envelopeApiClient
    .post<Response<User, AuthorizationError>>('/auth/signup', user)
    .then((response) => {
      return response.data;
    })
    .catch(defaultErrorHandler);
}

export async function requestDeleteAccount(): Promise<Response<null, AuthorizationError>> {
  return envelopeApiClient
    .post<Response<null, AuthorizationError>>('/auth/delete-account')
    .then((response) => {
      return response.data;
    })
    .catch(defaultErrorHandler);
}

export async function requestUpdateProfile(
  patch: Partial<UserProfilePatch>,
): Promise<Response<UserProfile, AuthorizationError>> {
  return envelopeApiClient
    .post<Response<UserProfile, AuthorizationError>>('/auth/update-profile', patch)
    .then((response) => {
      return response.data;
    })
    .catch(defaultErrorHandler);
}

export async function requestUpdatePassword(
  patch: PasswordPatch,
): Promise<Response<null, AuthorizationError>> {
  return envelopeApiClient
    .post<Response<null, AuthorizationError>>('/auth/update-password', patch)
    .then((response) => {
      return response.data;
    })
    .catch(defaultErrorHandler);
}
