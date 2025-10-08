import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import '@/styles/index.css';
import Page from '@/components/layout/page';
import { Header } from '@/components/layout/header';
import { HeadingContainer, Heading2Title, HeadingDescription } from '@/components/heading';
import Main from '@/components/layout/main';
import { Recommendation } from './main-recommendation';
import { Separator } from '@/components/ui/separator';
import { Welcome } from './welcome';
import { useUserSession } from '@/hooks/use-user-session';
import initApp from '@/init';
import { TrendingUp, FileClock } from 'lucide-react';

await initApp();

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
              <div>
                <HeadingContainer className='mt-5'>
                  <Heading2Title className='flex items-center'>
                    <TrendingUp className='mr-2'/>
                    트렌드 관광지
                  </Heading2Title>
                  <HeadingDescription>현재 TOURIN에서 인기있는 관광지</HeadingDescription>
                  <Separator />
                </HeadingContainer>
                <div className='mt-3'>
                  <Recommendation type='trend' placeholder='현재 집계된 관광지가 없습니다.'/>
                </div>
              </div>
              <div>
                <HeadingContainer className='mt-10'>
                  <Heading2Title className='flex items-center'>
                    <FileClock className='mr-2'/>
                    개인 추천 관광지
                  </Heading2Title>
                  <HeadingDescription>{userInfo?.name}님이 관심있게 본 관광지와 유사한 관광지</HeadingDescription>
                  <Separator />
                </HeadingContainer>
                <div className='mt-3'>
                  <Recommendation type='personalized' placeholder='추천을 받기 위해 관광지를 찾거나 북마크 하세요!'/>
                </div>
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
