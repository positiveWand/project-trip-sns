package com.positivewand.tourin.domain.tourspot;

import com.positivewand.tourin.domain.tourspot.entity.TourSpotReview;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Collection;
import java.util.List;

public interface TourSpotReviewRepository extends JpaRepository<TourSpotReview, Long> {
    Page<TourSpotReview> findByTourSpotId(Long tourSpotId, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<TourSpotReview> findForUpdateByIdIn(Collection<Long> ids);

    void deleteByUser_Id(Long userId);

    Long countById(Long id);
}
