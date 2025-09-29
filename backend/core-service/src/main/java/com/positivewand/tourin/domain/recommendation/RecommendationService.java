package com.positivewand.tourin.domain.recommendation;

import com.positivewand.tourin.domain.tourspot.TourSpotRepository;
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
    private final TrendService trendService;

    private static final int TOPK_LIMIT = 5;

    public List<TourSpotDto> getTestRecommendation(String username) {
        List<TourSpot> tourSpots = tourSpotRepository.findAllById(Arrays.asList(
                126436L,
                126438L,
                127744L,
                128796L,
                780778L
        ));

        return tourSpots.stream().map(TourSpotDto::create).toList();
    }

    public List<TourSpotDto> getTrendRecommendation() {
        List<Long> tourSpotIds = trendService.getTrendTopkIds(TOPK_LIMIT);

        List<TourSpot> tourSpots = tourSpotRepository.findAllById(tourSpotIds);

        return tourSpots.stream().map(TourSpotDto::create).toList();
    }
}
