const MAIN_PAGE = '/';
const MAP_PAGE = '/map/';
const DASHBOARD_PAGE = '/dashboard/';
const LOGIN_PAGE = '/login/';
const SIGNUP_PAGE = '/signup/';
const MY_PAGE = '/my/';

const ID_PATTERN = '^[a-zA-Z][a-zA-Z0-9]{8,16}$';
const PASSWORD_PATTERN = '^(?=.*[a-zA-Z])(?=.*\d)(?=.*[@$!%*?&])[a-zA-Z\d@$!%*?&]{8,24}$';
const EMAIL_PATTERN = '^(?!\.)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$';

export {
  MAIN_PAGE,
  MAP_PAGE,
  DASHBOARD_PAGE,
  LOGIN_PAGE,
  SIGNUP_PAGE,
  MY_PAGE,
  ID_PATTERN,
  PASSWORD_PATTERN,
  EMAIL_PATTERN,
};
