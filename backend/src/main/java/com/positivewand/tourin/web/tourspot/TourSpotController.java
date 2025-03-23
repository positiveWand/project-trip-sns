package com.positivewand.tourin.web.tourspot;

import com.positivewand.tourin.domain.auth.CustomUserDetails;
import com.positivewand.tourin.domain.auth.CustomUserDetailsService;
import com.positivewand.tourin.domain.tourspot.TourSpotReviewLikeService;
import com.positivewand.tourin.domain.tourspot.TourSpotReviewService;
import com.positivewand.tourin.domain.tourspot.TourSpotService;
import com.positivewand.tourin.domain.tourspot.dto.TourSpotDto;
import com.positivewand.tourin.domain.tourspot.dto.TourSpotReviewDto;
import com.positivewand.tourin.domain.tourspot.entity.TourSpotReview;
import com.positivewand.tourin.web.aop.PaginationAspect.PaginationHeader;
import com.positivewand.tourin.web.tourspot.request.AddTourSpotReviewRequest;
import com.positivewand.tourin.web.tourspot.request.PutTourSpotReviewLikeRequest;
import com.positivewand.tourin.web.tourspot.response.TourSpotOverviewResponse;
import com.positivewand.tourin.web.tourspot.response.TourSpotResponse;
import com.positivewand.tourin.web.tourspot.response.TourSpotReviewLikeResponse;
import com.positivewand.tourin.web.tourspot.response.TourSpotReviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TourSpotController {
    private final TourSpotService tourSpotService;
    private final TourSpotReviewService tourSpotReviewService;
    private final TourSpotReviewLikeService tourSpotReviewLikeService;
    private final CustomUserDetailsService userDetailsService;

    @GetMapping("/tour-spots")
    @PaginationHeader
    public Page<TourSpotOverviewResponse> getTourSpots(
            @RequestParam(name = "query", defaultValue = "") String query,
            @RequestParam(name = "tags", defaultValue = "") List<String> tags,
            @RequestParam(name = "customFilters", defaultValue = "") List<String> customFilters,
            @RequestParam(name = "sort", defaultValue = "name-asc") String sort,
            @RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize
    ) {
        Sort sortCriteria = null;
        if(sort.equals("name-asc")) {
            sortCriteria = Sort.by("name").ascending();
        } else if(sort.equals("name-desc")) {
            sortCriteria = Sort.by("name").descending();
        } else {
            throw new IllegalArgumentException("존재하지 않는 정렬 기준.");
        }

        Page<TourSpotDto> tourSpotDtoPage = null;
        if(query.isEmpty()) {
            tourSpotDtoPage = tourSpotService.findTourSpots(
                    tags,
                    sortCriteria,
                    pageNo-1,
                    pageSize
            );
        } else {
            tourSpotDtoPage = tourSpotService.findTourSpots(
                    query,
                    tags,
                    sortCriteria,
                    pageNo-1,
                    pageSize
            );
        }

        return tourSpotDtoPage.map(TourSpotOverviewResponse::createFromTourSpotDto);
    }

    @GetMapping("/tour-spots/map")
    public List<TourSpotOverviewResponse> getTourSpotsInMap(
            @RequestParam(name = "query", defaultValue = "") String query,
            @RequestParam(name = "tags", defaultValue = "") List<String> tags,
            @RequestParam(name = "customFilters", defaultValue = "") List<String> customFilters,
            @RequestParam(name = "minLat") Double minLat,
            @RequestParam(name = "minLng") Double minLng,
            @RequestParam(name = "maxLat") Double maxLat,
            @RequestParam(name = "maxLng") Double maxLng
    ) {
        if (Haversine.calculateDistance(minLat, minLng, maxLat, maxLng) > 30) {
            throw new IllegalArgumentException("대각선 길이가 30km 이하인 경우에만 지도에서 검색이 가능합니다.");
        }

        List<TourSpotDto> tourSpots = null;
        if(query.isEmpty()) {
            tourSpots = tourSpotService.findTourSpots(
                    tags,
                    new TourSpotService.LatLngBounds(minLat, minLng, maxLat, maxLng)
            );
        } else {
            tourSpots = tourSpotService.findTourSpots(
                    query,
                    tags,
                    new TourSpotService.LatLngBounds(minLat, minLng, maxLat, maxLng)
            );
        }

        return tourSpots.stream().map(TourSpotOverviewResponse::createFromTourSpotDto).toList();
    }

    @GetMapping("/tour-spots/{tourSpotId}")
    public TourSpotResponse getTourSpot(@PathVariable(name = "tourSpotId") Long tourSpotId) {
        TourSpotDto tourSpot = tourSpotService.findTourSpot(tourSpotId);

        return TourSpotResponse.createFromTourSpotDto(tourSpot);
    }

    @GetMapping("/tour-spots/{tourSpotId}/reviews")
    public Page<TourSpotReviewResponse> getTourSpotReviews(
            @PathVariable(name = "tourSpotId") Long tourSpotId,
            @RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize
    ) {
        Page<TourSpotReviewDto> tourSpotReviews = tourSpotReviewService.findTourSpotReviews(tourSpotId, pageNo-1, pageSize);

        return tourSpotReviews.map(TourSpotReviewResponse::createFromDto);
    }

    @PostMapping("/tour-spots/{tourSpotId}/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public TourSpotReviewResponse addTourSpotReview(
            @PathVariable(name = "tourSpotId") Long tourSpotId,
            @RequestBody AddTourSpotReviewRequest request
    ) {
        TourSpotReviewDto tourSpotReview = tourSpotReviewService.addTourSpotReview(tourSpotId, request.userId(), request.content());
        return TourSpotReviewResponse.createFromDto(tourSpotReview);
    }

    @DeleteMapping("/tour-spot-reviews/{tourSpotReviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTourSpotReview(@PathVariable(name = "tourSpotReviewId") Long tourSpotReviewId) {
        tourSpotReviewService.deleteTourSpotReview(tourSpotReviewId);
    }

    @PutMapping("/tour-spot-reviews/{tourSpotReviewId}/likes")
    @ResponseStatus(HttpStatus.OK)
    public TourSpotReviewLikeResponse putTourSpotReviewLike(
            @PathVariable(name = "tourSpotReviewId") Long tourSpotReviewId,
            @RequestBody PutTourSpotReviewLikeRequest putTourSpotReviewLikeRequest
    ) {
        CustomUserDetails userDetails = userDetailsService.getCurrentContextUser();

        if(!userDetails.getUsername().equals(putTourSpotReviewLikeRequest.userId())) {
            throw new AccessDeniedException("회원은 자신의 북마크만 삭제할 수 있습니다.");
        }

        if(putTourSpotReviewLikeRequest.liked()) {
            tourSpotReviewLikeService.addReviewLike(putTourSpotReviewLikeRequest.userId(), tourSpotReviewId);
            return new TourSpotReviewLikeResponse(
                    userDetails.getUsername(),
                    tourSpotReviewId.toString(),
                    true
            );
        } else {
            tourSpotReviewLikeService.deleteReviewLike(putTourSpotReviewLikeRequest.userId(), tourSpotReviewId);
            return new TourSpotReviewLikeResponse(
                    userDetails.getUsername(),
                    tourSpotReviewId.toString(),
                    false
            );
        }
    }
}
