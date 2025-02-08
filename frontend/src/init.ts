import { worker } from '@/mocks/browser';
import { checkSession } from './hooks/use-user-session';

export default async function initApp() {
  if (process.env.NODE_ENV == 'development') {
    await worker.start();
  }

  await checkSession();
}
