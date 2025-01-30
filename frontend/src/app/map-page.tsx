import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import '@/styles/index.css'

function MapPage() {
    return (
        <div>
        지도 페이지
        </div>
    )
}

createRoot(document.getElementById('root')!).render(
    <StrictMode>
        <MapPage />
    </StrictMode>,
)
  
  
export default MapPage
  