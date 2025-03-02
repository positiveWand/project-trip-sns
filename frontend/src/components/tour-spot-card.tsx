import { Badge } from '@/components/ui/badge';
import { Card } from '@/components/ui/card';
import { cn } from '@/lib/utils';

export interface TourSpotCardProps {
  className?: string;
  name: string;
  address: string;
  imageUrl?: string;
  tags: string[];
  href?: string;
}

export function TourSpotCard({
  className,
  name,
  address,
  imageUrl,
  tags,
  href,
}: TourSpotCardProps) {
  return (
    <div className={cn('w-72 h-80 hover:cursor-pointer hover:opacity-60', className)}>
      <a href={href ? href : ''}>
        <Card className='p-4 h-full flex flex-col justify-center gap-2'>
          {imageUrl ? (
            <img
              src={imageUrl}
              alt='관광지 이미지'
              className='rounded-xl h-40 w-full object-cover'
            />
          ) : (
            <div className='flex items-center justify-center text-xl text-muted-foreground rounded-xl border h-40'>
              이미지 없음
            </div>
          )}
          <div>
            <div className='text-2xl font-semibold'>{name}</div>
            <div className='line-clamp-1'>{address}</div>
          </div>
          <div className='flex flex-wrap gap-1'>
            {tags.map((tag) => {
              return <Badge>{tag}</Badge>;
            })}
          </div>
        </Card>
      </a>
    </div>
  );
}
