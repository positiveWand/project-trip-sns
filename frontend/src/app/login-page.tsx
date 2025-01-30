import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import '@/styles/index.css'

function LoginPage() {
    return (
        <div>
        로그인 페이지
        </div>
    )
}

createRoot(document.getElementById('root')!).render(
    <StrictMode>
        <LoginPage />
    </StrictMode>,
)
  
  
export default LoginPage
  