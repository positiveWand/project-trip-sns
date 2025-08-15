package com.positivewand.tourin.web.tourspot;

import com.positivewand.tourin.domain.recommendation.RecommendationService;
import com.positivewand.tourin.domain.tourspot.dto.TourSpotDto;
import com.positivewand.tourin.web.tourspot.response.TourSpotOverviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    @GetMapping("/recommendations/test")
    public List<TourSpotOverviewResponse> getRecommendation() {
        List<TourSpotDto> recommendation = recommendationService.getTestRecommendation("");

        return recommendation.stream().map(TourSpotOverviewResponse::createFromTourSpotDto).toList();
    }
}
