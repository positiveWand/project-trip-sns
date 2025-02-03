import { cn } from '@/lib/utils';
import * as React from 'react';
import { Button } from '@/components/ui/button';
import { DASHBOARD_PAGE, LOGIN_PAGE, MAIN_PAGE, MAP_PAGE, SIGNUP_PAGE, MY_PAGE } from '@/config';
import MainLogo from '@/components/ui/main-logo';
import { LogOut, Settings, UserRound } from 'lucide-react';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { useSession } from '@/hooks/use-session';
import { useUser } from '@/hooks/use-user';
import { useToast } from '@/hooks/use-toast';
import { requestLogout } from '@/lib/requests/auth/request-logout';

export interface HeaderProps extends React.ComponentProps<'header'> {}

const NAVIGATION_LINKS = [
  {
    name: '지도',
    url: MAP_PAGE,
  },
  {
    name: '대시보드',
    url: DASHBOARD_PAGE,
  },
];

function Header({ className }: HeaderProps) {
  const user = useUser();

  return (
    <header className={cn('w-full border-b border-border/50 px-5 py-3 flex bg-primary', className)}>
      <div className='flex text-primary-foreground'>
        <MainLogo href={MAIN_PAGE} className='mr-4' />

        {NAVIGATION_LINKS.map(({ name, url }) =>
          location.pathname == url ? (
            <Button
              variant='link'
              size='sm'
              className='text-primary-foreground underline font-extrabold'
              onClick={() => {
                location.href = url;
              }}
            >
              {name}
            </Button>
          ) : (
            <Button
              variant='link'
              size='sm'
              className='text-primary-foreground'
              onClick={() => {
                location.href = url;
              }}
            >
              {name}
            </Button>
          ),
        )}
      </div>

      <div className='flex flex-1 justify-end items-center text-primary-foreground'>
        {user ? (
          <>
            <UserMenu username={user.name} />
          </>
        ) : (
          <>
            <Button
              size='sm'
              className='mr-1'
              onClick={() => {
                location.href = LOGIN_PAGE;
              }}
            >
              로그인
            </Button>
            <Button
              size='sm'
              onClick={() => {
                location.href = SIGNUP_PAGE;
              }}
            >
              회원가입
            </Button>
          </>
        )}
      </div>
    </header>
  );
}

export interface UserMenuProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  username?: string;
}

const UserMenu = React.forwardRef<HTMLButtonElement, UserMenuProps>(
  ({ username, className, ...props }, ref) => {
    const { dispatchSession } = useSession();
    const { toast } = useToast();

    return (
      <DropdownMenu>
        <DropdownMenuTrigger asChild>
          <Button variant='ghost' size='sm' className='text-base' ref={ref} {...props}>
            <UserRound />
            <span>{username}</span>
          </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent>
          <DropdownMenuItem
            onClick={() => {
              location.href = MY_PAGE;
            }}
          >
            <Settings />
            <span>계정 설정</span>
          </DropdownMenuItem>
          <DropdownMenuItem
            onClick={() => {
              const response = requestLogout({});
              if (!response.success) {
                toast({
                  title: response.error.type,
                  description: response.error.message,
                  variant: 'destructive',
                });
                return;
              }

              dispatchSession({
                type: 'DEACTIVATE_SESSION',
              });
              location.href = MAIN_PAGE;
            }}
          >
            <LogOut />
            <span>로그아웃</span>
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>
    );
  },
);

export { Header };
