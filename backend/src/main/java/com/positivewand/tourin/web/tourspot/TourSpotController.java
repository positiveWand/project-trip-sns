package com.positivewand.tourin.web.tourspot;

import com.positivewand.tourin.domain.tourspot.TourSpotReviewService;
import com.positivewand.tourin.domain.tourspot.TourSpotService;
import com.positivewand.tourin.domain.tourspot.dto.TourSpotDto;
import com.positivewand.tourin.domain.tourspot.dto.TourSpotReviewDto;
import com.positivewand.tourin.domain.tourspot.entity.TourSpotReview;
import com.positivewand.tourin.web.aop.PaginationAspect.PaginationHeader;
import com.positivewand.tourin.web.tourspot.request.AddTourSpotReviewRequest;
import com.positivewand.tourin.web.tourspot.response.TourSpotOverviewResponse;
import com.positivewand.tourin.web.tourspot.response.TourSpotResponse;
import com.positivewand.tourin.web.tourspot.response.TourSpotReviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TourSpotController {
    private final TourSpotService tourSpotService;
    private final TourSpotReviewService tourSpotReviewService;

    @GetMapping("/tour-spots")
    @PaginationHeader
    public Page<TourSpotOverviewResponse> getTourSpots(
            @RequestParam(name = "query", defaultValue = "") String query,
            @RequestParam(name = "tags", defaultValue = "") List<String> tags,
            @RequestParam(name = "customFilters", defaultValue = "") List<String> customFilters,
            @RequestParam(name = "sort", required = false) String sort,
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

        Page<TourSpotDto> tourSpotDtoPage = tourSpotService.findTourSpots(
                query,
                tags,
                sortCriteria,
                pageNo,
                pageSize
        );

        return tourSpotDtoPage.map(TourSpotOverviewResponse::createFromTourSpotDto);
    }

    @GetMapping("/tour-spots/map")
    @PaginationHeader
    public List<TourSpotOverviewResponse> getTourSpotsInMap(
            @RequestParam(name = "query", required = false) String query,
            @RequestParam(name = "tags", defaultValue = "") List<String> tags,
            @RequestParam(name = "customFilters", defaultValue = "") List<String> customFilters,
            @RequestParam(name = "minLat") Double minLat,
            @RequestParam(name = "minLng") Double minLng,
            @RequestParam(name = "maxLat") Double maxLat,
            @RequestParam(name = "maxLng") Double maxLng
    ) {
        List<TourSpotDto> tourSpots = tourSpotService.findTourSpots(
                query,
                tags,
                new TourSpotService.LatLngBounds(minLat, minLng, maxLat, maxLng)
        );

        return tourSpots.stream().map(TourSpotOverviewResponse::createFromTourSpotDto).toList();
    }

    @GetMapping("/tour-spots/{tourSpotId}")
    @PaginationHeader
    public TourSpotResponse getTourSpot(@PathVariable(name = "tourSpotId") Long tourSpotId) {
        TourSpotDto tourSpot = tourSpotService.findTourSpot(tourSpotId);

        return TourSpotResponse.createFromTourSpotDto(tourSpot);
    }

    @GetMapping("/tour-spots/{tourSpotId}/reviews")
    @PaginationHeader
    public Page<TourSpotReviewResponse> getTourSpotReviews(
            @PathVariable(name = "tourSpotId") Long tourSpotId,
            @RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize
    ) {
        Page<TourSpotReviewDto> tourSpotReviews = tourSpotReviewService.findTourSpotReviews(tourSpotId, pageNo-1, pageSize);

        return tourSpotReviews.map(TourSpotReviewResponse::createFromDto);
    }

    @PostMapping("/tour-spots/{tourSpotId}/reviews")
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
}
