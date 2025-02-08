import { ComponentProps } from 'react';
import { cn } from '@/lib/utils';

export default function Main({ className, children }: ComponentProps<'div'>) {
  return <main className={className}>{children}</main>;
}
