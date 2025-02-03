export const testuser = {
  name: '김철수',
  id: 'testuserid',
  password: 'password123!',
  email: 'example@gmail.com',
};

export interface User {
  name: string;
  id: string;
  email: string;
}

export interface UserCredentials {
  id: string;
  password: string;
}
