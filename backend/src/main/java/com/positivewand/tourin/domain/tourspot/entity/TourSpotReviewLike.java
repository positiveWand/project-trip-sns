package com.positivewand.tourin.domain.tourspot.entity;

import com.positivewand.tourin.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
public class TourSpotReviewLike {
    @EmbeddedId
    private TourSpotReviewLikeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tourSpotReviewId")
    @JoinColumn(name = "tour_spot_review_id")
    private TourSpotReview tourSpotReview;

    public static TourSpotReviewLike create(User user, TourSpotReview tourSpotReview) {
        TourSpotReviewLike tourSpotReviewLike = new TourSpotReviewLike();
        
        tourSpotReviewLike.setId(TourSpotReviewLikeId.create(user.getId(), tourSpotReview.getId()));

        tourSpotReviewLike.setUser(user);
        tourSpotReviewLike.setTourSpotReview(tourSpotReview);

        return tourSpotReviewLike;
    }
}
