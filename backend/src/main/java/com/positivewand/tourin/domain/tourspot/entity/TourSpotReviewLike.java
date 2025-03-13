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

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("tourSpotReviewId")
    @JoinColumn(name = "tour_spot_review_id")
    private TourSpotReview tourSpotReview;

    public static TourSpotReviewLike create(User user, TourSpotReview tourSpotReview) {
        TourSpotReviewLike tourSpotReviewLike = new TourSpotReviewLike();

        tourSpotReviewLike.setId(new TourSpotReviewLikeId(user.getId(), tourSpotReview.getId()));

        tourSpotReviewLike.setUser(user);
        tourSpotReviewLike.setTourSpotReview(tourSpotReview);

        return tourSpotReviewLike;
    }
}
