package com.positivewand.tourin.web.tourspot.response;

import com.positivewand.tourin.domain.tourspot.dto.TourSpotDto;

import java.util.List;

public record TourSpotOverviewResponse(
        String id,
        String name,
        String address,
        double lat,
        double lng,
        String imageUrl,
        List<String> tags
) {
    public static TourSpotOverviewResponse createFromTourSpotDto(TourSpotDto tourSpotDto) {
        return new TourSpotOverviewResponse(
                tourSpotDto.id().toString(),
                tourSpotDto.name(),
                tourSpotDto.address(),
                tourSpotDto.lat(),
                tourSpotDto.lng(),
                tourSpotDto.imageUrl(),
                tourSpotDto.tags()
        );
    }
}
