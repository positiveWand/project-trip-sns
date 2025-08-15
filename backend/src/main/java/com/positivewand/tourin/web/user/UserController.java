package com.positivewand.tourin.web.user;

import com.positivewand.tourin.domain.auth.CustomUserDetailsService;
import com.positivewand.tourin.domain.tourspot.TourSpotReviewLikeService;
import com.positivewand.tourin.domain.user.BookmarkService;
import com.positivewand.tourin.domain.user.dto.BookmarkDto;
import com.positivewand.tourin.domain.user.dto.UserDto;
import com.positivewand.tourin.domain.user.UserService;
import com.positivewand.tourin.web.aop.PaginationAspect.PaginationHeader;
import com.positivewand.tourin.web.user.request.AddBookmarkRequest;
import com.positivewand.tourin.web.user.response.BookmarkResponse;
import com.positivewand.tourin.web.user.response.TourSpotReviewLikeResponse;
import com.positivewand.tourin.web.user.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final BookmarkService bookmarkService;
    private final TourSpotReviewLikeService tourSpotReviewLikeService;
    private final CustomUserDetailsService userDetailsService;

    @GetMapping("/users")
    @PaginationHeader
    public Page<UserResponse> getUsers(
            @RequestParam(name = "query", required = false) String query,
            @RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize
    ) {
        Page<UserDto> userDtoPage = null;

        if(query == null) {
            userDtoPage = userService.findUsers(pageNo-1, pageSize);
        } else {
            userDtoPage = userService.findUsers(query, pageNo-1, pageSize);
        }

        return userDtoPage.map((UserDto userDto) -> new UserResponse(userDto.username(), userDto.name()));
    }

    @GetMapping("/users/{userId}")
    public UserResponse getUser(@PathVariable(name = "userId") String userId) {
        UserDto user = userService.findUser(userId);

        return new UserResponse(user.username(), user.name());
    }

    @GetMapping("/users/{userId}/bookmarks")
    @PaginationHeader
    public Page<BookmarkResponse> getUserBookmarks(
            @PathVariable(name = "userId") String userId,
            @RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize
    ) {
        Page<BookmarkDto> bookmarks = bookmarkService.findBookmarks(userId, pageNo-1, pageSize);

        return bookmarks.map(BookmarkResponse::createFromDto);
    }

    @GetMapping("/users/{userId}/bookmarks/{tourSpotId}")
    public BookmarkResponse getUserBookmark(
            @PathVariable(name = "userId") String userId,
            @PathVariable(name = "tourSpotId") Long tourSpotId
    ) {
        BookmarkDto bookmark = bookmarkService.findBookmark(userId, tourSpotId);

        return BookmarkResponse.createFromDto(bookmark);
    }

    @PostMapping("/users/{userId}/bookmarks")
    @ResponseStatus(HttpStatus.CREATED)
    public BookmarkResponse addUserBookmark(
            @PathVariable(name = "userId") String userId,
            @RequestBody AddBookmarkRequest addBookmarkRequest,
            Principal principal
    ) {
        if(!principal.getName().equals(userId)) {
            throw new AccessDeniedException("회원은 자신의 북마크만 변경할 수 있습니다.");
        }

        BookmarkDto bookmark = bookmarkService.addBookmark(userId, addBookmarkRequest.tourSpotId());

        return BookmarkResponse.createFromDto(bookmark);
    }

    @DeleteMapping("/users/{userId}/bookmarks/{tourSpotId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserBookmark(
            @PathVariable(name = "userId") String userId,
            @PathVariable(name = "tourSpotId") Long tourSpotId,
            Principal principal
    ) {
        if(!principal.getName().equals(userId)) {
            throw new AccessDeniedException("회원은 자신의 북마크만 변경할 수 있습니다.");
        }
        
        bookmarkService.deleteBookmark(userId, tourSpotId);
    }

    @GetMapping("/users/{userId}/tour-spot-reviews/likes")
    @ResponseStatus(HttpStatus.OK)
    public List<TourSpotReviewLikeResponse> checkTourSpotReviewLike(
            @PathVariable(name = "userId") String userId,
            @RequestParam(name = "tourSpotReviewIds") List<Long> tourSpotReviewIds
    ) {
        Map<Long, Boolean> checkResult = tourSpotReviewLikeService.checkLikes(userId, tourSpotReviewIds);

        List<TourSpotReviewLikeResponse> response = new ArrayList<>();
        for (Long tourSpotReviewId: tourSpotReviewIds) {
            response.add(new TourSpotReviewLikeResponse(
                    userId,
                    tourSpotReviewId.toString(),
                    checkResult.get(tourSpotReviewId)
            ));
        }

        return response;
    }
}
