package com.positivewand.tourin.domain.tourspot.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class TourSpotTag {

    @EmbeddedId
    private TourSpotTagId id;

    @ManyToOne
    @MapsId("tourSpotId")
    @JoinColumn(name = "tour_spot_id")
    private TourSpot tourSpot;
    @Column(name = "tag", insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private TourSpotCategory tag;

    public static TourSpotTag create(TourSpot tourSpot, TourSpotCategory tag) {
        TourSpotTag newTag = new TourSpotTag();

        newTag.tourSpot = tourSpot;
        newTag.id = TourSpotTagId.create(tourSpot.getId(), tag);
        newTag.tag = tag;

        return newTag;
    }
}
