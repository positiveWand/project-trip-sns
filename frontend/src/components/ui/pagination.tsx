import * as React from "react"
import { ChevronLeft, ChevronRight, MoreHorizontal } from "lucide-react"

import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"

interface PaginationProps extends React.ComponentProps<'nav'> {
  maxPage?: number;
  currentPage: number;
  onPageChange: (page: number) => void;
}

export function Pagination({ maxPage, currentPage, onPageChange, className, ...props }: PaginationProps) {
  const navItems = React.useMemo(() => {
    let navItems = [];

    if(!maxPage) {
      maxPage = currentPage;
    }

    navItems.push(<PaginationPrevious onClick={() => {onPageChange(currentPage-1)}} disabled={currentPage == 1}/>);

    if(maxPage <= 7) {
      for (let i = 1; i <= maxPage; i++) {
        navItems.push((
          <PaginationItem onClick={() => {onPageChange(i)}} isActive={i == currentPage}>{i}</PaginationItem>
        ));
      }
    } else if(currentPage < 5) {
      for (let i = 1; i <= 5; i++) {
        navItems.push((
          <PaginationItem onClick={() => {onPageChange(i)}} isActive={i == currentPage}>{i}</PaginationItem>
        ));
      }
      navItems.push(<PaginationEllipsis/>);
      navItems.push((
        <PaginationItem onClick={() => {onPageChange(maxPage!)}}>{maxPage}</PaginationItem>
      ));
    } else if(maxPage - 4 < currentPage) {
      navItems.push((
        <PaginationItem onClick={() => {onPageChange(1)}}>1</PaginationItem>
      ));
      navItems.push(<PaginationEllipsis/>);
      for (let i = maxPage - 4; i <= maxPage; i++) {
        navItems.push((
          <PaginationItem onClick={() => {onPageChange(i)}} isActive={i == currentPage}>{i}</PaginationItem>
        ));
      }
    } else {
      navItems.push((
        <PaginationItem onClick={() => {onPageChange(1)}}>1</PaginationItem>
      ));
      navItems.push(<PaginationEllipsis/>);
      for (let i = currentPage - 1; i <= currentPage + 1; i++) {
        navItems.push((
          <PaginationItem onClick={() => {onPageChange(i)}} isActive={i == currentPage}>{i}</PaginationItem>
        ));
      }
      navItems.push(<PaginationEllipsis/>);
      navItems.push((
        <PaginationItem onClick={() => {onPageChange(maxPage!)}}>{maxPage}</PaginationItem>
      ));
    }
    navItems.push(<PaginationNext onClick={() => {onPageChange(currentPage+1)}} disabled={currentPage == maxPage}/>)

    return navItems;
  }, [maxPage, currentPage])

  return (
    <nav
      role='navigation'
      className={cn('flex justify-center', className)} {...props}
    >
      <ul className='flex'>
        {navItems}
      </ul>
    </nav>
  )
}

interface PaginationItemProps extends React.ComponentProps<typeof Button> {
  isActive?: boolean;
}

function PaginationItem({children, isActive, ...props}: PaginationItemProps) {
  return (
    <li>
      <Button
        variant='ghost'
        aria-current={isActive ? 'page' : undefined}
        className='aria-[current=page]:bg-slate-200'
        size='sm'
        {...props}
      >
        {children}
      </Button>
    </li>
  )
}

function PaginationPrevious({...props}: PaginationItemProps) {
  return (
    <li>
      <Button
        variant='ghost'
        size='sm'
        {...props}
      >
        <ChevronLeft/>
      </Button>
    </li>
  )
}

function PaginationNext({...props}: PaginationItemProps) {
  return (
    <li>
      <Button
        variant='ghost'
        size='sm'
        {...props}
      >
        <ChevronRight/>
      </Button>
    </li>
  )
}

function PaginationEllipsis({...props}: PaginationItemProps) {
  return (
    <li>
      <Button
        variant='ghost'
        size='sm'
        className='hover:bg-background'
        {...props}
      >
        <MoreHorizontal/>
      </Button>
    </li>
  )
}