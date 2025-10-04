package com.positivewand.tourin.web.aop;

import com.positivewand.tourin.event.trend.TrendEventService;
import com.positivewand.tourin.web.common.ClientIdResolver;
import com.positivewand.tourin.web.tourspot.response.TourSpotResponse;
import com.positivewand.tourin.web.user.response.BookmarkResponse;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class TrendAspect {
    private final ClientIdResolver clientIdResolver;
    private final TrendEventService trendEventService;

    public static final int VISIT_SCORE = 1;
    public static final int BOOKMARK_SCORE = 2;

    @AfterReturning(
            pointcut = "execution(* com.positivewand.tourin.web.tourspot.TourSpotController.getTourSpot(..))",
            returning = "returnVal"
    )
    public void collectViewTrend(JoinPoint join, TourSpotResponse returnVal) throws Throwable {
        trendEventService.publishTourspotViewEvent(clientIdResolver.resolve(), returnVal.id());
    }

    @AfterReturning(
            pointcut = "execution(* com.positivewand.tourin.web.user.UserController.addUserBookmark(..))",
            returning = "returnVal"
    )
    public void collectBookmarkTrend(JoinPoint join, BookmarkResponse returnVal) throws Throwable {
        trendEventService.publishTourspotBookmarkEvent(returnVal.userId(), returnVal.tourSpotId());
    }
}
