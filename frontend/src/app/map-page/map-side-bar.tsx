import { cn } from '@/lib/utils';
import { Searchbar } from '@/components/searchbar';
import { ToggleGroup, ToggleGroupItem } from '@/components/ui/toggle-group';
import { Label } from '@radix-ui/react-label';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { ArrowUpDown, RefreshCcw, SquareArrowRight } from 'lucide-react';
import { Separator } from '@/components/ui/separator';
import { Button } from '@/components/ui/button';
import { ComponentProps, useEffect, useMemo, useState } from 'react';
import { useAllUrlSearchParam, useUrlPathParam, useUrlSearchParam } from '@/hooks/use-url';
import { Badge } from '@/components/ui/badge';
import { Pagination } from '@/components/ui/pagination';
import { useTourSpots } from '@/hooks/use-tour-spot';
import { MAP_PAGE } from '@/config';

const CATEGORY_FILTER = [
  { name: '자연', value: 'nature' },
  { name: '역사', value: 'history' },
  { name: '휴양', value: 'rest' },
  { name: '체험', value: 'experience' },
  { name: '산업', value: 'industry' },
  { name: '건축/조형', value: 'architecture' },
  { name: '문화', value: 'culture' },
  { name: '축제', value: 'festival' },
  { name: '공연/행사', value: 'event' },
];
const USER_FILTER = [{ name: '북마크', value: 'bookmark' }];
const SORT_OPTION = [
  { name: '이름 오름차순', value: 'name-asc' },
  { name: '이름 내림차순', value: 'name-desc' },
];
const PAGE_LIMIT = 5;

export interface MapSidebarProps extends React.ComponentProps<'div'> {}

export function MapSideBar({ className }: MapSidebarProps) {
  const [query, setQuery] = useUrlSearchParam('query', undefined);
  const [tags, setTags] = useAllUrlSearchParam('tags');
  const [customFilters, setCustomFilters] = useAllUrlSearchParam('customFilters');
  const [sort, setSort] = useUrlSearchParam('sort', undefined);
  const [, setFocusedTourSpotId] = useUrlPathParam(MAP_PAGE + '/tourSpot/:focusedTourSpotId');
  const [page, setPage] = useState<number>(1);

  const [tourSpots, error, loading] = useTourSpots(
    query,
    tags,
    customFilters,
    sort,
    page,
    PAGE_LIMIT,
  );

  const resetFilter = () => {
    setTags([]);
  };

  const spotItemClickHandler = (id: string) => {
    setFocusedTourSpotId('focusedTourSpotId', id);
  };

  return (
    <div className={cn('h-full flex flex-col gap-2 shadow-md overflow-auto p-2', className)}>
      <Searchbar
        onEnter={(event) => {
          setQuery(event.currentTarget.value);
        }}
      />
      <Separator />
      <div className='flex items-center justify-center'>
        <div>필터</div>
        <Button size='sm' className='ml-auto' onClick={resetFilter}>
          <RefreshCcw /> 초기화
        </Button>
      </div>
      <div className='flex flex-col gap-2'>
        <Label className='text-sm'>분류/태그</Label>
        <ToggleGroup
          type='multiple'
          className='grow justify-start flex-wrap'
          value={tags}
          onValueChange={(value) => {
            setTags(value);
            setPage(1);
          }}
        >
          {CATEGORY_FILTER.map(({ name, value }) => {
            return <FilterToggleItem value={value}>{name}</FilterToggleItem>;
          })}
        </ToggleGroup>
      </div>
      <div className='flex flex-col gap-2'>
        <Label className='text-sm'>사용자 맞춤</Label>
        <ToggleGroup
          type='multiple'
          className='grow justify-start flex-wrap'
          value={customFilters}
          onValueChange={(value) => {
            setCustomFilters(value);
            setPage(1);
          }}
        >
          {USER_FILTER.map(({ name, value }) => {
            return <FilterToggleItem value={value}>{name}</FilterToggleItem>;
          })}
        </ToggleGroup>
      </div>
      <Separator />
      <div className='grow flex flex-col gap-2 overflow-auto'>
        <div className='flex items-center justify-center'>
          <div>
            총 <span className='font-bold'>{tourSpots?.totalItem}</span>개
          </div>
          <div className='ml-auto'>
            <Select
              value={undefined}
              onValueChange={(value) => {
                setSort(value);
                setPage(1);
              }}
            >
              <SelectTrigger className='w-44 focus:ring-0 focus:ring-offset-0'>
                <ArrowUpDown size={16} />
                <SelectValue placeholder='정렬 기준' />
              </SelectTrigger>
              <SelectContent>
                {SORT_OPTION.map(({ name, value }) => {
                  return <SelectItem value={value}>{name}</SelectItem>;
                })}
              </SelectContent>
            </Select>
          </div>
        </div>
        <ol className='overflow-auto grow'>
          {tourSpots?.data.map((tourSpot) => {
            return (
              <li
                className='p-2 hover:bg-accent border-b hover:cursor-pointer'
                key={tourSpot.id}
                onClick={() => {
                  spotItemClickHandler(tourSpot.id);
                }}
              >
                <div className='w-full flex'>
                  <span className='text-lg'>{tourSpot.name}</span>
                  <SquareArrowRight className='ml-auto text-foreground/50' strokeWidth='1.5' />
                </div>
                {tourSpot.tags.map((tag) => {
                  return <Badge>{tag}</Badge>;
                })}
              </li>
            );
          })}
        </ol>
        <Pagination
          maxPage={tourSpots ? tourSpots?.totalPage : 1}
          currentPage={page}
          onPageChange={setPage}
        />
      </div>
    </div>
  );
}

function FilterToggleItem({ children, ...props }: ComponentProps<typeof ToggleGroupItem>) {
  return (
    <ToggleGroupItem
      {...props}
      variant='outline'
      size='sm'
      className='text-sm data-[state=on]:bg-slate-400 data-[state=on]:text-primary-foreground data-[state=on]:font-bold'
    >
      {children}
    </ToggleGroupItem>
  );
}
