package com.positivewand.tourin.domain.user.entity;

import com.positivewand.tourin.domain.tourspot.entity.TourSpot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "bookmark")
@Getter
@Setter
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

    public static Bookmark create(User user, TourSpot tourSpot) {
        Bookmark bookmark = new Bookmark();
        bookmark.setId(BookmarkId.create(user.getId(), tourSpot.getId()));
        bookmark.setUser(user);
        bookmark.setTourSpot(tourSpot);

        return bookmark;
    }
}
