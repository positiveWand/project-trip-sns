import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import '@/styles/index.css';
import { LoginForm } from '@/components/login-form';
import { Toaster } from '@/components/ui/toaster';
import Page from '@/components/layout/page';
import { Header } from '@/components/layout/header';
import Main from '@/components/layout/main';
import initApp from '@/init';

await initApp();

function LoginPage() {
  return (
    <Page>
      <Header />
      <Main className='w-full h-full flex items-center justify-center'>
        <div className='w-full max-w-sm'>
          <LoginForm />
        </div>
      </Main>
      <Toaster />
    </Page>
  );
}

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <LoginPage />
  </StrictMode>,
);

export default LoginPage;
