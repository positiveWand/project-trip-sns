import { cn } from '@/lib/utils';

export interface UserProfileProps {
  className?: string;
  userName?: string;
  userId?: string;
}

export function UserProfile({ className, userName, userId }: UserProfileProps) {
  return (
    <div className={cn('flex flex-col', className)}>
      <div className='font-bold text-3xl'>{userName ? userName : '-'}</div>
      <div className='text-muted-foreground'>{userId ? userId : '-'}</div>
    </div>
  );
}
