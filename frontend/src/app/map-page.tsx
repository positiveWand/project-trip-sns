import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import '@/styles/index.css';
import Page from '@/components/layout/page';
import { MapProvider } from '@/context/naver-map-context';
import { Header } from '@/components/layout/header';
import Main from '@/components/layout/main';
import { MapSideBar } from '@/components/map-side-bar';
import { Map } from '@/components/map';
import initApp from '@/init';
import { Toaster } from '@/components/ui/toaster';

await initApp();

function MapPage() {
  return (
    <Page className='max-h-screen'>
      <Header />
      <MapProvider>
        <Main className='w-full flex items-center justify-center grow overflow-auto relative'>
          <div className='h-full w-[400px] overflow-auto'>
            <MapSideBar />
          </div>
          <div className='h-full grow flex relative overflow-hidden'>
            <div className='grow'>
              <Map></Map>
            </div>
          </div>
        </Main>
      </MapProvider>
      <Toaster />
    </Page>
  );
}

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <MapPage />
  </StrictMode>,
);

export default MapPage;
