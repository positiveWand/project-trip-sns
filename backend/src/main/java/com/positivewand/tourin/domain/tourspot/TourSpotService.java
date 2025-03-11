package com.positivewand.tourin.domain.tourspot;

import com.positivewand.tourin.domain.tourspot.dto.TourSpotDto;
import com.positivewand.tourin.domain.tourspot.entity.TourSpot;
import com.positivewand.tourin.domain.tourspot.entity.TourSpotCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TourSpotService {
    private final TourSpotRepository tourSpotRepository;

    public TourSpotDto findTourSpot(Long id) {
        Optional<TourSpot> tourSpot = tourSpotRepository.findById(id);

        if(tourSpot.isEmpty()) {
            throw new NoSuchElementException("관광지가 없습니다.");
        }

        return TourSpotDto.createFromTourSpot(tourSpot.get());
    }

    public Page<TourSpotDto> findTourSpots(
            String query,
            List<String> tags,
            Sort sort,
            int page,
            int size
    ) {
        Page<TourSpot> tourSpots = null;
        if (tags.isEmpty()) {
            tourSpots = tourSpotRepository.findByNameContaining(query, PageRequest.of(page, size, sort));
        } else {
            tourSpots = tourSpotRepository.findByNameAndTags(query, tags, PageRequest.of(page, size, sort));
        }

        return tourSpots.map(TourSpotDto::createFromTourSpot);
    }

    public record LatLngBounds(double minLat, double minLng, double maxLat, double maxLng) {}

    public List<TourSpotDto> findTourSpots(
            String query,
            List<String> tags,
            LatLngBounds latLngBounds
    ) {
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

        return tourSpots.stream().map(TourSpotDto::createFromTourSpot).toList();
    }
}
