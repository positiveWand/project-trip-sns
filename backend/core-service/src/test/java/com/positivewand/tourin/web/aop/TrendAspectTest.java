package com.positivewand.tourin.web.aop;

import com.positivewand.tourin.domain.auth.CustomUserDetails;
import com.positivewand.tourin.domain.auth.CustomUserDetailsService;
import com.positivewand.tourin.domain.tourspot.TourSpotService;
import com.positivewand.tourin.domain.tourspot.dto.TourSpotDto;
import com.positivewand.tourin.domain.user.BookmarkService;
import com.positivewand.tourin.domain.user.dto.BookmarkDto;
import com.positivewand.tourin.event.trend.TrendEventService;
import com.positivewand.tourin.web.common.ClientIdResolver;
import com.positivewand.tourin.web.tourspot.TourSpotController;
import com.positivewand.tourin.web.user.UserController;
import com.positivewand.tourin.web.user.request.AddBookmarkRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.*;

import static com.positivewand.tourin.web.aop.TrendAspect.BOOKMARK_SCORE;
import static com.positivewand.tourin.web.aop.TrendAspect.VISIT_SCORE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@SpringBootTest
class TrendAspectTest {
    @Autowired
    TourSpotController tourSpotController;
    @Autowired
    UserController userController;
    @MockitoBean
    TrendEventService trendEventService;
    @MockitoBean
    CustomUserDetailsService userDetailsService;
    @MockitoBean
    BookmarkService bookmarkService;
    @MockitoBean
    TourSpotService tourSpotService;
    @MockitoBean
    ClientIdResolver clientIdResolver;

    @Test
    void AOP를_활용해_트렌드_정보를_정확히_수집한다() throws Exception {
        // given - 10명의 사용자, 100개의 관광지
        Set<String> usernames = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            usernames.add("user"+i);
        }
        List<Long> tourSpotIds = new ArrayList<>();
        for (long i = 0; i < 100; i++) {
            tourSpotIds.add(i);
        }

        Map<String, Long> collected = new HashMap<>();
        
        // Controller가 호출하는 Service 모킹
        when(tourSpotService.findTourSpot(anyLong())).thenAnswer(invocation -> {
            long tourSpotId = invocation.getArgument(0, Long.class);

            if (!tourSpotIds.contains(tourSpotId))
                throw new NoSuchElementException();

            return new TourSpotDto(tourSpotId, null, null, 1.0, 1.0, null, null, null, null);
        });

        when(bookmarkService.addBookmark(anyString(), anyLong())).thenAnswer(invocation -> {
            String username = invocation.getArgument(0, String.class);
            long tourSpotId = invocation.getArgument(1, Long.class);

            if (!usernames.contains(username) || !tourSpotIds.contains(tourSpotId))
                return new NoSuchElementException();

            return new BookmarkDto("", tourSpotId, new TourSpotDto(tourSpotId, null, null, 1.0, 1.0, null, null, null, null));
        });
        
        // 테스트 관심사는 TrendAspect이기 때문에 그것이 의존하는 TrendEventService도 모킹
        doAnswer(invocation -> {
            String userId = invocation.getArgument(0, String.class);
            String tourSpotId = invocation.getArgument(1, String.class);

            if (!collected.containsKey(tourSpotId))
                collected.put(tourSpotId, 0L);
            collected.put(tourSpotId, collected.get(tourSpotId) + VISIT_SCORE);
            return null;
        }).when(trendEventService).publishTourspotViewEvent(anyString(), anyString());

        doAnswer(invocation -> {
            String userId = invocation.getArgument(0, String.class);
            String tourSpotId = invocation.getArgument(1, String.class);

            if (!collected.containsKey(tourSpotId))
                collected.put(tourSpotId, 0L);
            collected.put(tourSpotId, collected.get(tourSpotId) + BOOKMARK_SCORE);
            return null;
        }).when(trendEventService).publishTourspotBookmarkEvent(anyString(), anyString());

        // ClientIdResolver 모킹
        when(clientIdResolver.resolve()).thenAnswer(invocation -> "testuser");

        // 20번의 무작위 테스트 세션을 통과해야한다
        // 세션마다 트렌드 점수에 반영이 되는 경우(요청 정상 처리)와 안되는 경우(처리 중 예외 발생)가 섞여 있다
        // AOP를 이용해 처리가 정상적으로 완료된 요청만 트렌드 점수에 반영되고, 비정상 처리된 요청은 트렌드 점수에 반영되지 않아야 한다
        // 확률적 프로세스를 거쳐 기대하는 순위와 AOP로 집계된 점수 순위가 일치하면 수집이 정상적으로 됐다고 생각할 수 있다
        for (int iter = 0; iter < 20; iter++) {
            collected.clear();

            Map<String, Long> expected = new HashMap<>();
            
            for (int i = 0; i < 1000; i++) {
                // 전체 중 20% - 예외 발생하여 집계되지 않음
                // 전체 중 80% - 정상 처리되어 집계됨
                if (Math.random() < 0.2) {
                    try {
                        tourSpotController.getTourSpot(2000L);
                    } catch (Exception e) {}
                } else {
                    // 무작위 관광지 선정
                    long id = (long) (Math.random() * 99) + 1;
                    tourSpotController.getTourSpot(id);
                    if (!expected.containsKey(String.valueOf(id)))
                        expected.put(String.valueOf(id), 0L);
                    expected.put(String.valueOf(id), expected.get(String.valueOf(id)) + VISIT_SCORE);
                }
            }
            for (int i = 0; i < 1000; i++) {
                // 전체 중 20% - 예외 발생하여 집계되지 않음
                // 전체 중 80% - 정상 처리되어 집계됨
                if (Math.random() < 0.2) {
                    try {
                        userController.addUserBookmark("user", new AddBookmarkRequest(2000L));
                    } catch (Exception e) {}
                } else {
                    // 무작위 관광지, 사용자 선정
                    long id = (long) (Math.random() * 99) + 1;
                    long useri = (long) (Math.random() * 9) + 1;
                    when(userDetailsService.getCurrentContextUser()).thenAnswer(invocation ->
                            new CustomUserDetails(null, "user"+useri, null, null, null, null)
                    );
                    userController.addUserBookmark("user"+useri, new AddBookmarkRequest(id));
                    if (!expected.containsKey(String.valueOf(id)))
                        expected.put(String.valueOf(id), 0L);
                    expected.put(String.valueOf(id), expected.get(String.valueOf(id)) + BOOKMARK_SCORE);
                }
            }
            
            // 기대하는 순위 및 점수가 실제 순위 및 점수가 일치해야 한다
            assertEquals(expected.entrySet(), collected.entrySet());
            System.out.println("무작위 세션" + iter + " 완료");
        }
    }
}