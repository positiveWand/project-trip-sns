package com.positivewand.tourin.domain.tourspot.entity;

import com.positivewand.tourin.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tour_spot_review")
@Getter
@Setter
public class TourSpotReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content")
    private String content;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "like_count")
    private Long likeCount;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "tour_spot_id")
    private TourSpot tourSpot;
    @OneToMany(mappedBy = "tourSpotReview")
    private List<TourSpotReviewLike> likes;

    public static TourSpotReview create(TourSpot tourSpot, User user, String content, LocalDateTime createdAt) {
        TourSpotReview tourSpotReview = new TourSpotReview();

        if(content.length() > 500) {
            throw new IllegalStateException("관광지 후기는 500자를 넘지 못합니다.");
        }

        tourSpotReview.setTourSpot(tourSpot);
        tourSpotReview.setUser(user);
        tourSpotReview.setContent(content);
        tourSpotReview.setCreatedAt(createdAt);

        tourSpotReview.setLikeCount(0L);

        return tourSpotReview;
    }
}
