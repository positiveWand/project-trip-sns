import { setupWorker } from 'msw/browser';
import { authHandlers } from './handler/auth-handlers';
import { userHandlers } from './handler/user-handlers';
import { tourSpotHandlers } from './handler/tour-spot-handlers';
import { recommendationHandlers } from './handler/recommendation-handlers';

export const worker = setupWorker(
  ...authHandlers,
  ...userHandlers,
  ...tourSpotHandlers,
  ...recommendationHandlers,
);
