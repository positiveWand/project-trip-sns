import * as React from "react"
import logoUrl from '@/assets/map.png'
import { cn } from "@/lib/utils"

export interface MainLogoProps
  extends React.AnchorHTMLAttributes<HTMLAnchorElement> {

}

const MainLogo = React.forwardRef<HTMLAnchorElement, MainLogoProps>(
({ href, className, ...props }, ref) => {

    return (
        <a href={href} className={cn('flex items-center', className)} ref={ref} {...props}>
            <img src={logoUrl} alt="메인 로고 이미지" className='w-8 mr-1' />
            <h1 className='font-extrabold text-3xl align-middle'>TOURIN</h1>
        </a>
    )
}
)

export default MainLogo