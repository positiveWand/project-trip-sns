package com.positivewand.tourin.web.tourspot;

import com.positivewand.tourin.domain.tourspot.TourSpotService;
import com.positivewand.tourin.domain.tourspot.dto.TourSpotDto;
import com.positivewand.tourin.web.aop.PaginationAspect.PaginationHeader;
import com.positivewand.tourin.web.tourspot.response.TourSpotOverviewResponse;
import com.positivewand.tourin.web.tourspot.response.TourSpotResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TourSpotController {
    private final TourSpotService tourSpotService;

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
}
