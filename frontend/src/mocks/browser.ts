import { setupWorker } from 'msw/browser';
import { authHandlers } from './auth-handlers';

export const worker = setupWorker(...authHandlers);
