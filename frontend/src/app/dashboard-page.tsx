import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import '@/styles/index.css'

function DashboardPage() {
    return (
        <div>
        대시보드 페이지
        </div>
    )
}

createRoot(document.getElementById('root')!).render(
    <StrictMode>
        <DashboardPage />
    </StrictMode>,
)
  
  
export default DashboardPage
  