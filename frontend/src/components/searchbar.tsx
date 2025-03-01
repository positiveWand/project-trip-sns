import * as React from 'react';
import { Search } from 'lucide-react';
import { Input } from './ui/input';
import { cn } from '@/lib/utils';

export interface SearchbarProps extends React.ComponentProps<'div'> {
  onEnter?: (event: React.KeyboardEvent<HTMLInputElement>) => void;
}

export function Searchbar({ className, onEnter }: SearchbarProps) {
  // const [isOpen, setIsOpen] = React.useState<boolean>(false);
  const dropdownRef = React.useRef<HTMLDivElement>(null);

  // React.useEffect(() => {
  //   const handleClickOutside = (event: MouseEvent) => {
  //     if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
  //       setIsOpen(false);
  //     }
  //   };

  //   document.addEventListener('click', handleClickOutside);
  //   return () => document.removeEventListener('click', handleClickOutside);
  // }, []);

  return (
    <div className={cn('flex items-stretch justify-center', className)}>
      <div className='bg-primary rounded-s-md rounded-e-none flex items-center justify-center h-10 w-10'>
        <Search className='h-6 w-6 text-primary-foreground' />
      </div>
      <div className='grow relative' ref={dropdownRef}>
        <Input
          className='rounded-s-none focus-visible:ring-0 focus-visible:ring-offset-0'
          placeholder='관광지 이름 검색'
          // onFocus={() => {
          //   setIsOpen(true);
          // }}
          onKeyDown={(event) => {
            if (event.key == 'Enter' && onEnter) {
              onEnter(event);
            }
          }}
        />
        {/* {isOpen && (
          <div className='bg-popover text-popover-foreground shadow-md rounded-b-md absolute w-full'>
            <div className='max-h-80 overflow-auto' onClick={(event) => {}}>
              <div>1111</div>
              <div>2222</div>
              <div>3333</div>
              <div>4444</div>
              <div>5555</div>
              <div>6666</div>
              <div>7777</div>
              <div>8888</div>
            </div>
          </div>
        )} */}
      </div>
    </div>
  );
}
