package com.positivewand.tourin.domain.user.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class BookmarkId implements Serializable {
    private Long userId;
    private Long tourSpotId;

    public BookmarkId() {}

    public BookmarkId(Long userId, Long tourSpotId) {
        this.userId = userId;
        this.tourSpotId = tourSpotId;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        BookmarkId that = (BookmarkId) other;
        return Objects.equals(this.userId, that.userId) &&
                Objects.equals(this.tourSpotId, that.tourSpotId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, tourSpotId);
    }
}
