package com.positivewand.tourin.domain.recommendation;

import com.positivewand.tourin.domain.recommendation.dto.TrendItem;
import com.positivewand.tourin.domain.tourspot.TourSpotRepository;
import com.positivewand.tourin.domain.tourspot.dto.TourSpotDto;
import com.positivewand.tourin.domain.tourspot.entity.TourSpot;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final TourSpotRepository tourSpotRepository;
    private final RestTemplate restTemplate;

    private static final int TOPK_LIMIT = 5;
    @Value("${msa.recommendation-service.base-url}")
    private String RECOMMENDATION_SERVICE_BASE_URL;

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
        String url = UriComponentsBuilder
                .fromUri(URI.create(RECOMMENDATION_SERVICE_BASE_URL + "/api/recommendations/trend"))
                .queryParam("k", TOPK_LIMIT)
                .toUriString();

        TrendItem[] topkItems = restTemplate.getForObject(url, TrendItem[].class);
        if (topkItems == null || topkItems.length == 0)
            return List.of();

        List<Long> topkIds = Arrays.stream(topkItems)
                .map(TrendItem::itemId)
                .map(Long::parseLong)
                .toList();

        List<TourSpot> tourSpots = tourSpotRepository.findAllById(topkIds);
        Map<Long, TourSpot> mapById = tourSpots.stream()
                .collect(Collectors.toMap(TourSpot::getId, Function.identity()));

        return topkIds.stream()
                .map(mapById::get)
                .filter(Objects::nonNull)
                .map(TourSpotDto::create)
                .toList();
    }

    public List<TourSpotDto> getPersonalizedRecommendation(String username) {
        String url = UriComponentsBuilder
                .fromUri(URI.create(RECOMMENDATION_SERVICE_BASE_URL + "/api/recommendations/personalized"))
                .queryParam("user_id", username)
                .queryParam("k", TOPK_LIMIT)
                .toUriString();

        String[] topkItems = restTemplate.getForObject(url, String[].class);
        if (topkItems == null || topkItems.length == 0)
            return List.of();

        List<Long> topkIds = Arrays.stream(topkItems)
                .map(Long::parseLong)
                .toList();

        List<TourSpot> tourSpots = tourSpotRepository.findAllById(topkIds);
        Map<Long, TourSpot> mapById = tourSpots.stream()
                .collect(Collectors.toMap(TourSpot::getId, Function.identity()));

        return topkIds.stream()
                .map(mapById::get)
                .filter(Objects::nonNull)
                .map(TourSpotDto::create)
                .toList();
    }
}
