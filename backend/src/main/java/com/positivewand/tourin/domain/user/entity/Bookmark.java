package com.positivewand.tourin.domain.user.entity;

import com.positivewand.tourin.domain.tourspot.entity.TourSpot;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "bookmark")
@Getter
public class Bookmark {
    @EmbeddedId
    private BookmarkId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("tourSpotId")
    @JoinColumn(name = "tour_spot_id")
    private TourSpot tourSpot;

    public Bookmark() {}

    public Bookmark(User user, TourSpot tourSpot) {
        this.id = new BookmarkId(user.getId(), tourSpot.getId());
        this.user = user;
        this.tourSpot = tourSpot;
    }
}
