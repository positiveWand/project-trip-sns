package com.positivewand.tourin.domain.tourspot.dto;

import com.positivewand.tourin.domain.tourspot.entity.TourSpot;

import java.util.List;

public record TourSpotDto(
        Long id,
        String name,
        String address,
        Double lat,
        Double lng,
        String imageUrl,
        String description,
        String phoneNumber,
        List<String> tags
) {
    public static TourSpotDto create(TourSpot entity) {
        return new TourSpotDto(
                entity.getId(),
                entity.getName(),
                entity.getFullAddress(),
                entity.getLat(),
                entity.getLng(),
                entity.getImageUrl(),
                entity.getDescription(),
                entity.getFullAddress(),
                entity.getTags().stream().map((tourSpotTag -> tourSpotTag.getTag().getViewString())).toList()
        );
    }
}
