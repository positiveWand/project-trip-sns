package com.positivewand.tourin.domain.tourspot;

import com.positivewand.tourin.domain.tourspot.entity.TourSpot;
import com.positivewand.tourin.domain.tourspot.entity.TourSpotReview;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface TourSpotReviewRepository extends JpaRepository<TourSpotReview, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<TourSpotReview> findForUpdateById(Long tourSpotId);

    Page<TourSpotReview> findByTourSpotId(Long tourSpotId, Pageable pageable);

    void deleteByUser_Id(Long userId);

    Long countById(Long id);
}
