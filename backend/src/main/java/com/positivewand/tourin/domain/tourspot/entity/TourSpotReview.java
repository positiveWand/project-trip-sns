package com.positivewand.tourin.domain.tourspot.entity;

import com.positivewand.tourin.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    private static ConcurrentHashMap<Long, Long> likeCountBuffer = new ConcurrentHashMap<>();

    public static TourSpotReview create(TourSpot tourSpot, User user, String content, LocalDateTime createdAt, long likeCount) {
        TourSpotReview tourSpotReview = new TourSpotReview();

        if(content.length() > 500) {
            throw new IllegalStateException("관광지 후기는 500자를 넘지 못합니다.");
        }

        tourSpotReview.setTourSpot(tourSpot);
        tourSpotReview.setUser(user);
        tourSpotReview.setContent(content);
        tourSpotReview.setCreatedAt(createdAt);

        tourSpotReview.setLikeCount(Math.max(0L, likeCount));

        return tourSpotReview;
    }

    public static Map<Long, Long> flushLikeCountBuffer() {
        final Map<Long, Long> snapshot = new HashMap<>();

        // 스냅샷 찍기
        for (long key: TourSpotReview.likeCountBuffer.keySet().stream().toList()) {
            TourSpotReview.likeCountBuffer.computeIfPresent(key, (tourSpotId, difference) -> {
                snapshot.put(tourSpotId, difference);
                return null;
            });
        }

        return snapshot;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = Math.max(0, likeCount);
    }

    public void incrementLikeCount() {
        TourSpotReview.likeCountBuffer.compute(this.id, (tourSpotId, difference) -> {
            if(difference == null) return 1L;

            return difference + 1;
        });
    }
    public void decrementLikeCount() {
        TourSpotReview.likeCountBuffer.compute(this.id, (tourSpotId, difference) -> {
            if(difference == null) return -1L;

            return difference - 1;
        });
    }
}
