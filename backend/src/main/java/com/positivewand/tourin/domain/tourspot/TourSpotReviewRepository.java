package com.positivewand.tourin.domain.tourspot;

import com.positivewand.tourin.domain.tourspot.entity.TourSpotReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TourSpotReviewRepository extends JpaRepository<TourSpotReview, Long> {
    Page<TourSpotReview> findByTourSpotId(Long tourSpotId, Pageable pageable);
}
