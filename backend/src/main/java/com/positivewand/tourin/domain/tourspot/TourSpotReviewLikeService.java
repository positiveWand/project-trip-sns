package com.positivewand.tourin.domain.tourspot;

import com.positivewand.tourin.domain.tourspot.entity.TourSpotReview;
import com.positivewand.tourin.domain.tourspot.entity.TourSpotReviewLike;
import com.positivewand.tourin.domain.tourspot.entity.TourSpotReviewLikeId;
import com.positivewand.tourin.domain.user.UserRepository;
import com.positivewand.tourin.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TourSpotReviewLikeService {
    private final UserRepository userRepository;
    private final TourSpotReviewRepository tourSpotReviewRepository;
    private final TourSpotReviewLikeRepository tourSpotReviewLikeRepository;

    public boolean checkLike(String username, Long tourSpotReviewId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("등록된 회원이 없습니다."));

        tourSpotReviewRepository.findById(tourSpotReviewId)
                .orElseThrow(() -> new NoSuchElementException("관광지 후기가 존재하지 않습니다."));

        Optional<TourSpotReviewLike> tourSpotReviewLike = tourSpotReviewLikeRepository.findById(TourSpotReviewLikeId.create(user.getId(), tourSpotReviewId));
        return tourSpotReviewLike.isPresent();
    }

    public Map<Long, Boolean> checkLikes(String username, List<Long> tourSpotReviewIds) {
        Map<Long, Boolean> checkResult = new HashMap<>();

        for (Long tourSpotReviewId: tourSpotReviewIds) {
            checkResult.put(tourSpotReviewId, checkLike(username, tourSpotReviewId));
        }

        return checkResult;
    }

    @Transactional
    public void addReviewLike(String username, Long tourSpotReviewId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("등록된 회원이 없습니다."));

        TourSpotReview tourSpotReview = tourSpotReviewRepository.findById(tourSpotReviewId)
                .orElseThrow(() -> new NoSuchElementException("관광지 후기가 존재하지 않습니다."));

        tourSpotReview.incrementLikeCount();

        tourSpotReviewLikeRepository.save(TourSpotReviewLike.create(user, tourSpotReview));
    }

    @Transactional
    public void deleteReviewLike(String username, Long tourSpotReviewId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("등록된 회원이 없습니다."));

        TourSpotReview tourSpotReview = tourSpotReviewRepository.findById(tourSpotReviewId)
                .orElseThrow(() -> new NoSuchElementException("관광지 후기가 존재하지 않습니다."));

        tourSpotReview.decrementLikeCount();

        tourSpotReviewLikeRepository.deleteById(TourSpotReviewLikeId.create(user.getId(), tourSpotReviewId));
    }

    @Transactional
    public void syncTourSpotReviewLike(List<Long> tourSpotReviewKeys, Map<Long, Long> difference) {
        List<TourSpotReview> tourSpotReviews = tourSpotReviewRepository.findForUpdateByIdIn(tourSpotReviewKeys);

        for(TourSpotReview tourSpotReview: tourSpotReviews) {
            tourSpotReview.setLikeCount(tourSpotReview.getLikeCount()+difference.get(tourSpotReview.getId()));
        }
    }
}
