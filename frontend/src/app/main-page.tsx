import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import '@/styles/index.css';

function MainPage() {
  return <div>메인 페이지</div>;
}

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <MainPage />
  </StrictMode>,
);

export default MainPage;
