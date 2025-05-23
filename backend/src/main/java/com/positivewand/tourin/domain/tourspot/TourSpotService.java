package com.positivewand.tourin.domain.tourspot;

import com.positivewand.tourin.domain.tourspot.dto.TourSpotDto;
import com.positivewand.tourin.domain.tourspot.entity.TourSpot;
import com.positivewand.tourin.domain.tourspot.entity.TourSpotCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TourSpotService {
    public static int MIN_QUERY_LENGTH = 2;

    private final TourSpotRepository tourSpotRepository;

    public TourSpotDto findTourSpot(Long tourSpotId) {
        TourSpot tourSpot = tourSpotRepository.findById(tourSpotId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 관광지입니다."));

        return TourSpotDto.create(tourSpot);
    }

    public Page<TourSpotDto> findTourSpots(
            String query,
            List<String> tags,
            Sort sort,
            int page,
            int size
    ) {
        if (query.length() < MIN_QUERY_LENGTH) {
            throw new IllegalArgumentException("검색 문자열은 길이가 " + MIN_QUERY_LENGTH + "이상 이어야합니다");
        }

        Page<TourSpot> tourSpots = null;
        if (tags.isEmpty()) {
            tourSpots = tourSpotRepository.findByNameContaining(query, PageRequest.of(page, size, sort));
        } else {
            tourSpots = tourSpotRepository.findByNameAndTags(query, tags, PageRequest.of(page, size, sort));
        }

        return tourSpots.map(TourSpotDto::create);
    }

    public Page<TourSpotDto> findTourSpots(
            List<String> tags,
            Sort sort,
            int page,
            int size
    ) {
        Page<TourSpot> tourSpots = null;
        if (tags.isEmpty()) {
            tourSpots = tourSpotRepository.findAll(PageRequest.of(page, size, sort));
        } else {
            tourSpots = tourSpotRepository.findByTags(tags, PageRequest.of(page, size, sort));
        }

        return tourSpots.map(TourSpotDto::create);
    }

    public record LatLngBounds(double minLat, double minLng, double maxLat, double maxLng) {}

    public List<TourSpotDto> findTourSpots(
            String query,
            List<String> tags,
            LatLngBounds latLngBounds
    ) {
        if (query.length() < MIN_QUERY_LENGTH) {
            throw new IllegalArgumentException("검색 문자열은 길이가 " + MIN_QUERY_LENGTH + "이상 이어야합니다");
        }
        if (Haversine.calculateDistance(latLngBounds.minLat, latLngBounds.minLng, latLngBounds.maxLat, latLngBounds.maxLng) > 30) {
            throw new IllegalArgumentException("대각선 길이가 30km 이하인 경우에만 지도에서 검색이 가능합니다.");
        }

        List<TourSpot> tourSpots = null;
        if (tags.isEmpty()) {
            tourSpots = tourSpotRepository.findByNameAndLatLngBounds(
                    query,
                    latLngBounds.minLat(),
                    latLngBounds.minLng(),
                    latLngBounds.maxLat(),
                    latLngBounds.maxLng()
            );
        } else {
            tourSpots = tourSpotRepository.findByNameAndTagsAndLatLngBounds(
                    query,
                    tags,
                    latLngBounds.minLat(),
                    latLngBounds.minLng(),
                    latLngBounds.maxLat(),
                    latLngBounds.maxLng()
            );
        }

        return tourSpots.stream().map(TourSpotDto::create).toList();
    }

    public List<TourSpotDto> findTourSpots(
            List<String> tags,
            LatLngBounds latLngBounds
    ) {
        if (Haversine.calculateDistance(latLngBounds.minLat, latLngBounds.minLng, latLngBounds.maxLat, latLngBounds.maxLng) > 30) {
            throw new IllegalArgumentException("대각선 길이가 30km 이하인 경우에만 지도에서 검색이 가능합니다.");
        }
        
        List<TourSpot> tourSpots = null;
        if (tags.isEmpty()) {
            tourSpots = tourSpotRepository.findByTagsAndLatLngBounds(
                    Arrays.stream(TourSpotCategory.values()).map(Objects::toString).toList(),
                    latLngBounds.minLat(),
                    latLngBounds.minLng(),
                    latLngBounds.maxLat(),
                    latLngBounds.maxLng()
            );
        } else {
            tourSpots = tourSpotRepository.findByTagsAndLatLngBounds(
                    tags,
                    latLngBounds.minLat(),
                    latLngBounds.minLng(),
                    latLngBounds.maxLat(),
                    latLngBounds.maxLng()
            );
        }

        return tourSpots.stream().map(TourSpotDto::create).toList();
    }
}
