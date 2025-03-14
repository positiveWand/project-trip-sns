import { http, HttpResponse } from 'msw';
import { AUTHORIZED } from '../config';

const testuser = {
  name: '김철수',
  id: 'testuser',
  email: 'testuser@example.com',
  password: 'password123!',
};
if (!sessionStorage.getItem('user:' + testuser.id)) {
  sessionStorage.setItem('user:' + testuser.id, JSON.stringify(testuser));
}

interface User {
  id: string;
  name: string;
  email: string;
}
interface UserCredentials {
  id: string;
  password: string;
}
interface UserPatch {
  name: string;
  email: string;
}
interface PasswordPatch {
  oldPassword: string;
  newPassword: string;
}

export const authHandlers = [
  http.post('/api/auth/signup', async ({ request }) => {
    const user = (await request.json()) as User;

    if (sessionStorage.getItem('user:' + user.id)) {
      return HttpResponse.json(
        {
          error: 'DUPLICATE_USER',
          message: '같은 아이디로 가입한 회원이 존재합니다.',
        },
        { status: 422 },
      );
    }

    sessionStorage.setItem('user:' + user.id, JSON.stringify(user));
    return HttpResponse.json(
      {
        id: user.id,
        name: user.name,
        email: user.email,
      },
      { status: 201 },
    );
  }),

  http.post('/api/auth/login', async ({ request }) => {
    const credentials = (await request.json()) as UserCredentials;
    const u = sessionStorage.getItem('user:' + credentials.id);
    if (u && JSON.parse(u).id == credentials.id && JSON.parse(u).password == credentials.password) {
      const uo = JSON.parse(u);
      sessionStorage.setItem('session:' + uo.id, uo.id);

      return HttpResponse.json(
        {
          id: uo.id,
          name: uo.name,
          email: uo.email,
        },
        {
          status: 200,
          headers: {
            'Set-Cookie': 'sessionid=' + uo.id,
          },
        },
      );
    }

    return HttpResponse.json(
      {
        error: 'INVALID_CREDENTIALS',
        message: '아이디 혹은 비밀번호가 일치하는 회원이 존재하지 않습니다.',
      },
      { status: 401 },
    );
  }),

  http.post('/api/auth/logout', async ({ request, cookies }) => {
    const userid = cookies.sessionid;
    if (sessionStorage.getItem('session:' + userid)) {
      sessionStorage.removeItem('session:' + userid);
      return new HttpResponse(null, { status: 204 });
    } else {
      return HttpResponse.json(
        {
          error: 'UNAUTHORIZED',
          message: '인증이 필요합니다.',
        },
        { status: 401 },
      );
    }
  }),

  http.get('/api/auth/me', async ({ request, cookies }) => {
    const userid = cookies.sessionid;
    const u = sessionStorage.getItem('session:' + userid);
    if (u) {
      const user = JSON.parse(sessionStorage.getItem('user:' + userid)!);
      return HttpResponse.json(
        {
          id: user.id,
          name: user.name,
          email: user.email,
        },
        { status: 200 },
      );
    } else if (AUTHORIZED) {
      return HttpResponse.json(
        {
          id: testuser.id,
          name: testuser.name,
          email: testuser.email,
        },
        { status: 200 },
      );
    } else {
      return HttpResponse.json(
        {
          error: 'UNAUTHORIZED',
          message: '인증이 필요합니다.',
        },
        { status: 401 },
      );
    }
  }),

  http.post('/api/auth/delete-account', async ({ request, cookies }) => {
    const userid = cookies.sessionid;
    const u = sessionStorage.getItem('session:' + userid);
    if (u) {
      sessionStorage.removeItem('user:' + userid);
      sessionStorage.removeItem('session:' + userid);
      return new HttpResponse(null, { status: 204 });
    } else {
      return HttpResponse.json(
        {
          error: 'UNAUTHORIZED',
          message: '인증이 필요합니다.',
        },
        { status: 401 },
      );
    }
  }),

  http.post('/api/auth/update-profile', async ({ request, cookies }) => {
    const userid = cookies.sessionid;
    const userpatch = (await request.json()) as UserPatch;
    const u = sessionStorage.getItem('session:' + userid);
    if (u) {
      const user = JSON.parse(sessionStorage.getItem('user:' + userid)!);
      sessionStorage.removeItem('user:' + userid);
      sessionStorage.setItem(
        'user:' + userid,
        JSON.stringify({
          id: user.id,
          password: user.password,
          name: userpatch.name,
          email: userpatch.email,
        }),
      );
      return HttpResponse.json(
        {
          id: user.id,
          password: user.password,
          name: userpatch.name,
          email: userpatch.email,
        },
        { status: 200 },
      );
    } else {
      return HttpResponse.json(
        {
          error: 'UNAUTHORIZED',
          message: '인증이 필요합니다.',
        },
        { status: 401 },
      );
    }
  }),
  http.post('/api/auth/update-password', async ({ request, cookies }) => {
    const userid = cookies.sessionid;
    const passwordpatch = (await request.json()) as PasswordPatch;
    const u = sessionStorage.getItem('session:' + userid);
    if (
      u &&
      JSON.parse(sessionStorage.getItem('user:' + userid)!).password == passwordpatch.oldPassword
    ) {
      const user = JSON.parse(sessionStorage.getItem('user:' + userid)!);
      sessionStorage.setItem(
        'user:' + userid,
        JSON.stringify({
          id: user.id,
          password: passwordpatch.newPassword,
          name: user.name,
          email: user.email,
        }),
      );
      return new HttpResponse(null, { status: 204 });
    } else {
      return HttpResponse.json(
        {
          error: 'UNAUTHORIZED',
          message: '인증이 필요합니다.',
        },
        { status: 401 },
      );
    }
  }),
];
