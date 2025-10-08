import { TourSpotCard } from '@/components/tour-spot-card';
import { MAP_PAGE } from '@/config';
import { useTourSpotRecommendations } from '@/hooks/use-recommendation';
import { cn } from '@/lib/utils';

export interface MainRecommendationProps {
  className?: string;
  type: string
  placeholder: string
}

export function Recommendation({ className, type, placeholder }: MainRecommendationProps) {
  const [recommendation, error, loading] = useTourSpotRecommendations(type);

  if (loading) {
    return null;
  }

  return (
      recommendation.length != 0 ?
        <div
          className={cn(
            'grid grid-cols-[repeat(auto-fill,minmax(300px,1fr))] gap-2 justify-items-center',
            className,
          )}
        >
        {recommendation.map((tourSpot) => {
          return (
            <TourSpotCard
              name={tourSpot.name}
              address={tourSpot.address}
              imageUrl={tourSpot.imageUrl}
              tags={tourSpot.tags}
              href={MAP_PAGE + `/tourSpot/${tourSpot.id}`}
            />
          );
        })}
        </div>
      :
      <div className='text-center text-xl text-muted-foreground my-10'>{placeholder}</div>
  );
}
