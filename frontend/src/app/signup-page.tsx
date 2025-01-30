import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import '@/styles/index.css'

function SignupPage() {
    return (
        <div>
        회원가입 페이지
        </div>
    )
}

createRoot(document.getElementById('root')!).render(
    <StrictMode>
        <SignupPage />
    </StrictMode>,
)
  
  
export default SignupPage
  