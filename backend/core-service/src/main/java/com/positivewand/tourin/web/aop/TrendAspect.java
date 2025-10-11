package com.positivewand.tourin.web.aop;

import com.positivewand.tourin.event.user.UserEventService;
import com.positivewand.tourin.web.common.ClientIdResolver;
import com.positivewand.tourin.web.tourspot.response.TourSpotResponse;
import com.positivewand.tourin.web.user.response.BookmarkResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class TrendAspect {
    private final ClientIdResolver clientIdResolver;
    private final UserEventService userEventService;

    public static final int VISIT_SCORE = 1;
    public static final int BOOKMARK_SCORE = 2;

    @AfterReturning(
            pointcut = "execution(* com.positivewand.tourin.web.tourspot.TourSpotController.getTourSpot(..))",
            returning = "returnVal"
    )
    public void collectViewTrend(JoinPoint join, TourSpotResponse returnVal) {
        try {
            userEventService.publishTourspotViewEvent(clientIdResolver.resolve(), returnVal.id());
        } catch (Exception e) {
            log.error("[이벤트] 관광지 조회 이벤트 출판 실패", e);
        }
    }

    @AfterReturning(
            pointcut = "execution(* com.positivewand.tourin.web.user.UserController.addUserBookmark(..))",
            returning = "returnVal"
    )
    public void collectBookmarkTrend(JoinPoint join, BookmarkResponse returnVal) {
        try {
            userEventService.publishTourspotBookmarkEvent(returnVal.userId(), returnVal.tourSpotId());
        } catch (Exception e) {
            log.error("[이벤트] 관광지 조회 이벤트 출판 실패", e);
        }
    }
}
