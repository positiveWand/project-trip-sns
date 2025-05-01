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
    private final TourSpotReviewLikeRepository tourSpotReviewLikeRepository;

    public TourSpotReviewDto findTourSpotReview(Long tourSpotReviewId) {
        Optional<TourSpotReview> tourSpotReview = tourSpotReviewRepository.findById(tourSpotReviewId);

        if(tourSpotReview.isEmpty()) {
            throw new NoSuchElementException("존재하지 않는 관광지 후기입니다.");
        }

        return TourSpotReviewDto.create(tourSpotReview.get());
    }

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
                entity.getCreatedAt(),
                entity.getContent(),
                entity.getLikeCount()
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

        TourSpotReview tourSpotReview = TourSpotReview.create(tourSpot.get(), user.get(), content, LocalDateTime.now(), 0L);

        tourSpotReviewRepository.save(tourSpotReview);

        return TourSpotReviewDto.create(tourSpotReview);
    }

    @Transactional
    public void deleteTourSpotReview(Long tourSpotReviewId) {
        Optional<TourSpotReview> tourSpotReview = tourSpotReviewRepository.findById(tourSpotReviewId);

        if(tourSpotReview.isEmpty()) {
            throw new NoSuchElementException("관광지 후기가 존재하지 않습니다.");
        }

        tourSpotReviewLikeRepository.deleteByTourSpotReviewId(tourSpotReviewId);
        tourSpotReviewRepository.deleteById(tourSpotReviewId);
    }
}
