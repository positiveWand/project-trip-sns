import { TourSpotCard } from '@/components/tour-spot-card';
import { MAP_PAGE } from '@/config';
import { useTourSpotRecommendations } from '@/hooks/use-recommendation';
import { cn } from '@/lib/utils';

export interface MainRecommendationProps {
  className?: string;
}

export function MainRecommendation({ className }: MainRecommendationProps) {
  const [recommendation, error, loading] = useTourSpotRecommendations('main');

  if (loading) {
    return null;
  }

  return (
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
            href={MAP_PAGE + `tourSpot/${tourSpot.id}`}
          />
        );
      })}
    </div>
  );
}
