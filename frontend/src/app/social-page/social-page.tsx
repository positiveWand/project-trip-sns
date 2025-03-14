import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import '@/styles/index.css';
import Page from '@/components/layout/page';
import { Header } from '@/components/layout/header';
import Main from '@/components/layout/main';
import { Separator } from '@/components/ui/separator';
import initApp from '@/init';
import { useUrlPathParam, useUrlSearchParam } from '@/hooks/use-url';
import { UserProfile } from './user-profile';
import { HeadingContainer, Heading3Title, HeadingDescription } from '@/components/heading';
import { UserBookmark } from './user-bookmark';
import { useUser } from '@/hooks/use-user';

await initApp();

function SocialPage() {
  const [{ userId }, setPathParam] = useUrlPathParam('/social/user/:userId');
  const [user, error, loading] = useUser(userId);

  return (
    <Page>
      <Header />
      <Main className='w-full flex items-center justify-center'>
        <div className='w-full max-w-7xl py-20 flex flex-col'>
          <UserProfile className='mb-2' userId={user?.id} userName={user?.name} />
          <Separator />
          <HeadingContainer className='mt-5'>
            <Heading3Title>북마크</Heading3Title>
          </HeadingContainer>
          <UserBookmark className='mt-3' userId={userId} />
        </div>
      </Main>
    </Page>
  );
}

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <SocialPage />
  </StrictMode>,
);

export default SocialPage;
