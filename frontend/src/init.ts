import { checkSession } from './hooks/use-user-session';

export default async function initApp() {
  if (process.env.NODE_ENV == 'development') {
    const { worker } = await import('@/mocks/browser');
    await worker.start();
  }

  await checkSession();
}
