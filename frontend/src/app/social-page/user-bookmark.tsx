import { TourSpotCard } from '@/components/tour-spot-card';
import { MAP_PAGE } from '@/config';
import { useUserBookmarks } from '@/hooks/use-bookmark';
import { cn } from '@/lib/utils';
import { useState } from 'react';
import { Pagination } from '@/components/ui/pagination';

export interface UserBookmarkProps {
  className?: string;
  userId?: string;
}

export function UserBookmark({ className, userId }: UserBookmarkProps) {
  const [page, setPage] = useState<number>(1);
  const [bookmarks, error, loading] = useUserBookmarks(userId, page, 8);

  return (
    <div className={cn('grid grid-cols-4 gap-2', className)}>
      {bookmarks && bookmarks.data.length > 0 ? (
        bookmarks.data.map((bookmark) => {
          return (
            <TourSpotCard
              name={bookmark.tourSpotOverview.name}
              address={bookmark.tourSpotOverview.address}
              imageUrl={bookmark.tourSpotOverview.imageUrl}
              tags={bookmark.tourSpotOverview.tags}
              href={MAP_PAGE + `tourSpot/${bookmark.tourSpotId}`}
            />
          );
        })
      ) : (
        <div className=''>북마크가 없습니다</div>
      )}
      <div className='col-span-full mt-3'>
        <Pagination currentPage={page} maxPage={bookmarks?.totalPage} onPageChange={setPage} />
      </div>
    </div>
  );
}
