import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import '@/styles/index.css';
import { Toaster } from '@/components/ui/toaster';
import Page from '@/components/layout/page';
import { Header } from '@/components/layout/header';
import Main from '@/components/layout/main';
import UserSettings from './user-settings';
import initApp from '@/init';

initApp();

function MyPage() {
  return (
    <Page>
      <Header />
      <Main className='w-full flex items-center justify-center'>
        <div className='w-full max-w-3xl py-7'>
          <UserSettings />
        </div>
      </Main>
      <Toaster />
    </Page>
  );
}

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <MyPage />,
  </StrictMode>,
);

export default MyPage;
