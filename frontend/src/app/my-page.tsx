import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import '@/styles/index.css'

function MyPage() {
    return (
        <div>
        계정 설정 페이지
        </div>
    )
}

createRoot(document.getElementById('root')!).render(
    <StrictMode>
        <MyPage />
    </StrictMode>,
)
  
  
export default MyPage
  