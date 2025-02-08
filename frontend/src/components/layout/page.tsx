import { ComponentProps } from 'react';
import { cn } from '@/lib/utils';
import { Header } from './header';
import Main from './main';

export default function Page({ className, children }: ComponentProps<'div'>) {
  return <div className={cn('flex flex-col h-full', className)}>{children}</div>;
}
