import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './styles/index.css'
import ColorPalette from '@/app/ColorPalette'
import App from '@/app/App'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <ColorPalette />
    {/* <App /> */}
  </StrictMode>,
)
