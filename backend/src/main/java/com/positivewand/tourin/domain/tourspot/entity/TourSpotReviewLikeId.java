package com.positivewand.tourin.domain.tourspot.entity;

import com.positivewand.tourin.domain.user.entity.BookmarkId;
import jakarta.persistence.Embeddable;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Setter
public class TourSpotReviewLikeId implements Serializable {
    private Long userId;
    private Long tourSpotReviewId;

    public static TourSpotReviewLikeId create(Long userId, Long tourSpotReviewId) {
        TourSpotReviewLikeId tourSpotReviewLikeId = new TourSpotReviewLikeId();
        tourSpotReviewLikeId.setUserId(userId);
        tourSpotReviewLikeId.setTourSpotReviewId(tourSpotReviewId);

        return tourSpotReviewLikeId;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        TourSpotReviewLikeId that = (TourSpotReviewLikeId) other;
        return Objects.equals(this.userId, that.userId) &&
                Objects.equals(this.tourSpotReviewId, that.tourSpotReviewId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, tourSpotReviewId);
    }
}
