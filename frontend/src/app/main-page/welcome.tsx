import { cn } from '@/lib/utils';

export interface WelcomeProps {
  className?: string;
  username?: string;
}

export function Welcome({ className, username }: WelcomeProps) {
  return (
    <section className={cn('flex flex-col items-center justify-center gap-2 py-2', className)}>
      {username ? (
        <span className='text-5xl font-bold'>안녕하세요, {username}님!</span>
      ) : (
        <>
          <span className='text-5xl font-bold'>안녕하세요!</span>
          <div className='flex flex-col items-center text-xl'>
            <div>
              <span className='text-primary font-bold'>TOURIN</span>은 관광지를 탐색하고 공유할 수
              있는 서비스입니다.
            </div>
            <div>
              회원가입 하신다면 커뮤니티, 관광지 추천과 같이 더 많은 서비스를 이용하실 수 있습니다!
            </div>
          </div>
        </>
      )}
    </section>
  );
}
