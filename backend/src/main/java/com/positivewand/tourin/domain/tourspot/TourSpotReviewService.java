package com.positivewand.tourin.domain.tourspot;

import com.positivewand.tourin.domain.tourspot.dto.TourSpotReviewDto;
import com.positivewand.tourin.domain.tourspot.entity.TourSpot;
import com.positivewand.tourin.domain.tourspot.entity.TourSpotReview;
import com.positivewand.tourin.domain.user.UserRepository;
import com.positivewand.tourin.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TourSpotReviewService {
    private final TourSpotRepository tourSpotRepository;
    private final UserRepository userRepository;
    private final TourSpotReviewRepository tourSpotReviewRepository;

    public Page<TourSpotReviewDto> findTourSpotReviews(Long tourSpotId, int page, int size) {
        Optional<TourSpot> tourSpot = tourSpotRepository.findById(tourSpotId);

        if(tourSpot.isEmpty()) {
            throw new NoSuchElementException("존재하지 않는 관광지입니다.");
        }

        Page<TourSpotReview> tourSpotReviews = tourSpotReviewRepository.findByTourSpotId(
                tourSpotId,
                PageRequest.of(page, size, Sort.by("createdAt").descending())
        );

        return tourSpotReviews.map(entity -> new TourSpotReviewDto(
                entity.getId(),
                entity.getTourSpot().getId(),
                entity.getUser().getUsername(),
                entity.getContent(),
                tourSpotReviewRepository.countById(entity.getId())
        ));
    }

    @Transactional
    public TourSpotReviewDto addTourSpotReview(Long tourSpotId, String username, String content) {
        Optional<TourSpot> tourSpot = tourSpotRepository.findById(tourSpotId);

        if(tourSpot.isEmpty()) {
            throw new NoSuchElementException("존재하지 않는 관광지입니다.");
        }

        Optional<User> user = userRepository.findByUsername(username);

        if(user.isEmpty()) {
            throw new NoSuchElementException("등록된 회원이 없습니다.");
        }

        TourSpotReview tourSpotReview = TourSpotReview.createTourSpotReview(tourSpot.get(), user.get(), content, LocalDateTime.now());

        tourSpotReviewRepository.save(tourSpotReview);

        return TourSpotReviewDto.createFromEntity(tourSpotReview);
    }

    @Transactional
    public void deleteTourSpotReview(Long tourSpotReviewId) {
        tourSpotReviewRepository.deleteById(tourSpotReviewId);
    }
}
