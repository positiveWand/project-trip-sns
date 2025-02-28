export interface User {
  id: string;
  password: string;
  name: string;
  email: string;
}

export interface Bookmark {
  tourSpotId: string;
}

export const TEST_USERS: User[] = [
  {
    id: 'testuser',
    password: 'password123!',
    name: '김철수',
    email: 'testuser@example.com',
  },
  {
    id: 'testuser1',
    password: 'password123!',
    name: '박서준',
    email: 'testuser1@example.com',
  },
  {
    id: 'testuser2',
    password: 'password123!',
    name: '이지우',
    email: 'testuser2@example.com',
  },
  {
    id: 'testuser3',
    password: 'password123!',
    name: '신하준',
    email: 'testuser3@example.com',
  },
  {
    id: 'testuser4',
    password: 'password123!',
    name: '김준서',
    email: 'testuser4@example.com',
  },
  {
    id: 'testuser5',
    password: 'password123!',
    name: '김채원',
    email: 'testuser5@example.com',
  },
];

export const TEST_BOOKMARKS: Bookmark[] = [
  { tourSpotId: '1918643' },
  { tourSpotId: '1918421' },
  { tourSpotId: '2819599' },
  { tourSpotId: '1926379' },
  { tourSpotId: '1621165' },
  { tourSpotId: '3008007' },
  { tourSpotId: '988441' },
  { tourSpotId: '1621118' },
  { tourSpotId: '2778809' },
  { tourSpotId: '1556005' },
];
