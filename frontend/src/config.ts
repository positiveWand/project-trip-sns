const MAIN_PAGE = '/';
const MAP_PAGE = '/map/';
const SOCIAL_PAGE = '/social/';
const LOGIN_PAGE = '/login/';
const SIGNUP_PAGE = '/signup/';
const MY_PAGE = '/my/';

const NAME_PATTERN = '^[가-힣]{2,6}|[a-zA-Z\s]{2,24}$';
const ID_PATTERN = '^[a-zA-Z][a-zA-Z0-9]{7,16}$';
const PASSWORD_PATTERN = '^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[@$!%*?&])[a-zA-Z0-9@$!%*?&]{8,24}$';
const EMAIL_PATTERN = '^(?!\\.)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$';

export {
  MAIN_PAGE,
  MAP_PAGE,
  SOCIAL_PAGE,
  LOGIN_PAGE,
  SIGNUP_PAGE,
  MY_PAGE,
  ID_PATTERN,
  PASSWORD_PATTERN,
  EMAIL_PATTERN,
  NAME_PATTERN,
};
