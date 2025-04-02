package com.positivewand.tourin.domain.tourspot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TourSpotTagId implements Serializable {
    private Long tourSpotId;
    @Enumerated(EnumType.STRING)
    private TourSpotCategory tag;

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        TourSpotTagId that = (TourSpotTagId) other;
        return Objects.equals(this.tourSpotId, that.tourSpotId) &&
                Objects.equals(this.tag, that.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tourSpotId, tag);
    }
}
