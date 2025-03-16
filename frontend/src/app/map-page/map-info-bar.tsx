import { cn } from '@/lib/utils';
import { useState, ComponentProps, useRef, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import {
  X,
  MapPin,
  Text,
  Phone,
  Bookmark,
  Plus,
  Heart,
  Share2,
  EllipsisVertical,
} from 'lucide-react';
import { Toggle } from '@/components/ui/toggle';
import { Separator } from '@/components/ui/separator';
import { Badge } from '@/components/ui/badge';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
  DialogFooter,
} from '@/components/ui/dialog';
import { Pagination } from '@/components/ui/pagination';
import { Textarea } from '@/components/ui/textarea';
import { TourSpot } from '@/hooks/use-tour-spot';
import { useTourSpotReviews } from '@/hooks/use-tour-spot-review';
import { Skeleton } from '@/components/ui/skeleton';
import { useToast } from '@/hooks/use-toast';
import { useUserSession } from '@/hooks/use-user-session';
import {
  requestDeleteUserBookmark,
  requestPostUserBookmark,
} from '@/lib/requests/request-bookmark';
import {
  requestDeleteTourSpotReview,
  requestGetTourSpotReviewLikes,
  requestPostTourSpotReview,
  requestPutTourSpotReviewLike,
} from '@/lib/requests/request-tour-spot-review';
import { useLoading } from '@/hooks/use-loading';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { MAP_PAGE, SOCIAL_PAGE } from '@/config';
import { useIsBookmark } from '@/hooks/use-bookmark';
import { IDENTITY_TRANSFORM, useProxyState } from '@/hooks/use-proxy-state';

export interface MapInfoBarProps extends ComponentProps<'div'> {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  info: TourSpot | null;
}

interface ReviewLikeInfo {
  liked: boolean;
  count: number;
}

export function MapInfoBar({ open, onOpenChange, info, className }: MapInfoBarProps) {
  const [sessionActive, sessionInfo] = useUserSession();
  const [isBookmark, setIsBookmark] = useProxyState(
    useIsBookmark(sessionInfo?.id, info?.id)[0],
    IDENTITY_TRANSFORM,
    [sessionInfo?.id, info?.id],
  );

  const [reviewPage, setReviewPage] = useState<number>(1);
  const [reviews, reviewsError, reviewsLoading] = useTourSpotReviews(info?.id, reviewPage, 5);
  const [reviewLikes, setReviewLikes] = useProxyState(
    reviews,
    async (state) => {
      if (!state || !sessionInfo) return null;
      const response = await requestGetTourSpotReviewLikes(
        sessionInfo.id,
        state.data.map((review) => review.id),
      );

      if (!response.success) return null;

      const result: Record<string, ReviewLikeInfo> = {};
      for (let i = 0; i < response.data.length; i++) {
        result[response.data[i].tourSpotReviewId] = {
          liked: response.data[i].liked,
          count: state.data[i].likes,
        };
      }

      return result;
    },
    [info?.id, reviewPage, 5],
  );

  const reviewTextRef = useRef<HTMLTextAreaElement>(null);
  const [reviewPotalOpen, setReviewPortalOpen] = useState<boolean>(false);

  const loading = useLoading(info == null, reviewsLoading, isBookmark == null);
  const { toast } = useToast();

  useEffect(() => {
    setReviewPage(1);
  }, [info]);

  if (!open || !info) return null;

  if (reviewsError) {
    toast({
      title: reviewsError.error,
      description: reviewsError.message,
      variant: 'destructive',
    });
  }

  const closeButtonHandler = () => {
    onOpenChange(!open);
  };
  const bookmarkToggleHandler = async (bookmark: boolean) => {
    if (!sessionInfo || !info) {
      return;
    }

    if (bookmark) {
      const response = await requestPostUserBookmark(sessionInfo.id, info.id);
      if (response.success) {
        setIsBookmark(true);
        toast({
          title: '북마크 추가',
          description: '관광지를 북마크에 추가했습니다.',
        });
      } else {
        toast({
          title: response.error,
          description: response.message,
          variant: 'destructive',
        });
      }
    } else {
      const response = await requestDeleteUserBookmark(sessionInfo.id, info.id);
      if (response.success) {
        setIsBookmark(false);
        toast({
          title: '북마크 제거',
          description: '관광지를 북마크에서 제거했습니다.',
        });
      } else {
        toast({
          title: response.error,
          description: response.message,
          variant: 'destructive',
        });
      }
    }
  };
  const shareButtonHandler = async () => {
    navigator.clipboard
      .writeText(window.location.host + MAP_PAGE + '/tourSpot/' + info.id)
      .then(() => {
        toast({
          title: '관광지 공유',
          description: '관광지 링크를 클립보드에 복사했습니다.',
        });
      })
      .catch(() => {
        toast({
          title: '관광지 공유',
          description: '관광지 링크를 클립보드에 복사하는데 실패했습니다.',
          variant: 'destructive',
        });
      });
  };
  const postReviewButtonHandler = async () => {
    if (!sessionInfo || !info || !reviewTextRef.current) {
      return;
    }

    const response = await requestPostTourSpotReview(
      info.id,
      sessionInfo.id,
      reviewTextRef.current.value,
    );

    if (response.success) {
      setReviewPortalOpen(false);
      toast({
        title: '후기 작성',
        description: '후기를 작성했습니다.',
      });
    } else {
      toast({
        title: response.error,
        description: response.message,
        variant: 'destructive',
      });
    }
  };

  return (
    <div
      className={cn(
        'h-[98%] p-1 bg-popover text-popover-foreground shadow-md rounded-xl z-10 top-[50%] left-2 absolute translate-y-[-50%] animate-in slide-in-from-left slide-in-from-top-1/2 duration-300 flex flex-col overflow-hidden',
        className,
      )}
      data-open={open ? 'true' : 'false'}
    >
      <div className='flex flex-row-reverse'>
        <Button variant='link' className='text-foreground' size='sm' onClick={closeButtonHandler}>
          <X />
        </Button>
      </div>
      {loading || !info ? (
        <InfoBarSkeleton />
      ) : (
        <div className='flex flex-col gap-2 grow overflow-auto py-2'>
          {info.imageUrl ? (
            <img src={info.imageUrl} alt='관광지 이미지' className='rounded-xl' />
          ) : null}
          <div className='px-2 flex flex-col gap-4'>
            <div className='flex flex-col'>
              <div className='flex items-center justify-center'>
                <h1 className='text-2xl font-bold mr-auto'>{info.name}</h1>
              </div>
              <div className='mt-2 flex flex-wrap gap-1'>
                {info.tags.map((tag) => {
                  return <Badge>{tag}</Badge>;
                })}
              </div>
              <div className='mt-2 flex items-center justify-center gap-4'>
                <BookmarkToggle
                  disabled={!sessionActive}
                  pressed={Boolean(isBookmark)}
                  onPressedChange={bookmarkToggleHandler}
                />
                <ShareButton onClick={shareButtonHandler} />
              </div>
            </div>
            <div className='flex flex-col gap-2'>
              <h2 className='text-xl font-bold'>정보</h2>
              <div className='flex items-center'>
                <span className='mr-2'>
                  <MapPin size={24} />
                </span>
                <span>{info.address}</span>
              </div>
              <div className='flex items-center'>
                <span className='mr-2'>
                  <Phone size={24} />
                </span>
                <span>{info.phoneNumber}</span>
              </div>
              <div className='flex items-start'>
                <span className='mr-2'>
                  <Text size={24} />
                </span>
                <span>{info.description}</span>
              </div>
            </div>
            <div>
              <div className='flex items-center justify-center'>
                <h2 className='text-xl font-bold'>후기</h2>
                <Dialog open={reviewPotalOpen} onOpenChange={setReviewPortalOpen}>
                  <DialogTrigger asChild>
                    <Button variant='outline' className='ml-auto' disabled={!sessionActive}>
                      <Plus />
                      후기 작성
                    </Button>
                  </DialogTrigger>
                  <DialogContent className='sm:max-w-[425px]'>
                    <DialogHeader>
                      <DialogTitle>후기</DialogTitle>
                      <DialogDescription>관광지는 어떠셨나요? 후기를 남겨주세요!</DialogDescription>
                    </DialogHeader>
                    <Textarea maxLength={100} ref={reviewTextRef} />
                    <DialogFooter>
                      <Button type='submit' onClick={postReviewButtonHandler}>
                        작성
                      </Button>
                    </DialogFooter>
                  </DialogContent>
                </Dialog>
              </div>
              <div>
                {reviews && reviewLikes
                  ? reviews.data.map((review) => {
                      if (!reviewLikes[review.id]) return null;

                      const likeToggleHandler = async (value: boolean) => {
                        if (!sessionInfo) {
                          return;
                        }

                        const response = await requestPutTourSpotReviewLike(
                          sessionInfo?.id,
                          review.id,
                          value,
                        );

                        if (!response.success) {
                          toast({
                            title: response.error,
                            description: response.message,
                            variant: 'destructive',
                          });
                        }

                        if (value) {
                          const newReviewLikes = {
                            ...reviewLikes,
                          };
                          newReviewLikes[review.id] = {
                            liked: value,
                            count: reviewLikes[review.id].count + 1,
                          };
                          setReviewLikes(newReviewLikes);
                        } else {
                          const newReviewLikes = {
                            ...reviewLikes,
                          };
                          newReviewLikes[review.id] = {
                            liked: value,
                            count: reviewLikes[review.id].count - 1,
                          };
                          setReviewLikes(newReviewLikes);
                        }
                      };

                      return (
                        <>
                          <ReviewItem
                            reviewId={review.id}
                            userId={review.userId}
                            content={review.content}
                            liked={reviewLikes[review.id].liked}
                            likeCount={reviewLikes[review.id].count}
                            onPressedChange={likeToggleHandler}
                          />
                          <Separator />
                        </>
                      );
                    })
                  : null}
              </div>
              <Pagination
                className='mt-2'
                maxPage={reviews?.totalPage}
                currentPage={reviewPage}
                onPageChange={setReviewPage}
              />
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

function InfoBarSkeleton() {
  return (
    <div className='flex grow'>
      <Skeleton className='grow bg-primary' />
    </div>
  );
}

function BookmarkToggle({ className, ...props }: ComponentProps<typeof Toggle>) {
  return (
    <Toggle
      className={cn('flex flex-col h-auto py-2 px-3', className)}
      variant='outline'
      {...props}
    >
      {props.pressed ? <Bookmark fill='fill' /> : <Bookmark />}
      <span>북마크</span>
    </Toggle>
  );
}

function ShareButton({ className, ...props }: ComponentProps<typeof Button>) {
  return (
    <Button variant='outline' className={cn('flex flex-col h-auto py-2 px-3')} {...props}>
      <Share2 />
      <span>공유</span>
    </Button>
  );
}

interface ReviewProps extends ComponentProps<'div'> {
  reviewId: string;
  userId: string;
  content: string;
  liked: boolean;
  likeCount: number;
  onPressedChange: (value: boolean) => void;
}

function ReviewItem({ reviewId, userId, content, liked, likeCount, onPressedChange }: ReviewProps) {
  const { toast } = useToast();

  return (
    <div className='py-3 flex flex-col gap-2'>
      <div className='flex items-center gap-1'>
        <a
          href={`${SOCIAL_PAGE}/user/${userId}`}
          className='font-semibold hover:underline underline-offset-4'
        >
          {userId}
        </a>
        <Toggle
          className='ml-auto'
          variant='outline'
          pressed={liked}
          onPressedChange={onPressedChange}
        >
          {liked ? <Heart fill='fill' /> : <Heart />}
          {likeCount}
        </Toggle>
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant='ghost' size='sm'>
              <EllipsisVertical />
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align='start'>
            <DropdownMenuItem
              onClick={async () => {
                const response = await requestDeleteTourSpotReview(reviewId);
                if (response.success) {
                  toast({
                    title: '후기 삭제',
                    description: '후기를 성공적으로 삭제했습니다.',
                  });
                } else {
                  toast({
                    title: response.error,
                    description: response.message,
                    variant: 'destructive',
                  });
                }
              }}
            >
              <span>삭제</span>
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </div>
      <div>{content}</div>
    </div>
  );
}
