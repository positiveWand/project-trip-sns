package com.positivewand.tourin.web.tourspot.response;

import com.positivewand.tourin.domain.tourspot.dto.TourSpotDto;

import java.util.List;

public record TourSpotResponse(
        String id,
        String name,
        String address,
        double lat,
        double lng,
        String imageUrl,
        String description,
        String phoneNumber,
        List<String> tags
) {
    public static TourSpotResponse createFromTourSpotDto(TourSpotDto tourSpotDto) {
        return new TourSpotResponse(
                tourSpotDto.id().toString(),
                tourSpotDto.name(),
                tourSpotDto.address(),
                tourSpotDto.lat(),
                tourSpotDto.lng(),
                tourSpotDto.imageUrl(),
                tourSpotDto.description(),
                tourSpotDto.phoneNumber(),
                tourSpotDto.tags()
        );
    }
}
