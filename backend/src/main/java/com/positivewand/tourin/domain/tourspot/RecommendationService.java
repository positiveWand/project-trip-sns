package com.positivewand.tourin.domain.tourspot;

import com.positivewand.tourin.domain.tourspot.dto.TourSpotDto;
import com.positivewand.tourin.domain.tourspot.entity.TourSpot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final TourSpotRepository tourSpotRepository;

    public List<TourSpotDto> getUserRecommendation(String username) {
        List<TourSpot> tourSpots = tourSpotRepository.findAllById(Arrays.asList(
                126436L,
                126438L,
                127744L,
                128796L,
                780778L
        ));

        return tourSpots.stream().map(TourSpotDto::create).toList();
    }
}
