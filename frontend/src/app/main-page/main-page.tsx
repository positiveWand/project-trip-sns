import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import '@/styles/index.css';
import Page from '@/components/layout/page';
import { Header } from '@/components/layout/header';
import { HeadingContainer, Heading2Title, HeadingDescription } from '@/components/heading';
import Main from '@/components/layout/main';
import { MainRecommendation } from './main-recommendation';
import { Separator } from '@/components/ui/separator';
import { Welcome } from './welcome';
import { useUserSession } from '@/hooks/use-user-session';
import initApp from '@/init';

initApp();

function MainPage() {
  const [sessionActive, userInfo] = useUserSession();

  return (
    <Page>
      <Header />
      <Main className='w-full flex items-center justify-center'>
        <div className='w-full max-w-7xl py-20'>
          <Welcome username={userInfo?.name} />
          {sessionActive && (
            <>
              <HeadingContainer className='mt-5'>
                <Heading2Title>이런 관광지는 어때요?</Heading2Title>
                <HeadingDescription>북마크 관광지를 기반으로 구성해봤어요</HeadingDescription>
                <Separator />
              </HeadingContainer>
              <div className='mt-3'>
                <MainRecommendation />
              </div>
            </>
          )}
        </div>
      </Main>
    </Page>
  );
}

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <MainPage />
  </StrictMode>,
);

export default MainPage;
