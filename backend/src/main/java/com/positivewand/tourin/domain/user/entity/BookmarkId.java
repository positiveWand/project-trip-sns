package com.positivewand.tourin.domain.user.entity;

import jakarta.persistence.Embeddable;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Setter
public class BookmarkId implements Serializable {
    private Long userId;
    private Long tourSpotId;

    public static BookmarkId create(Long userId, Long tourSpotId) {
        BookmarkId bookmarkId = new BookmarkId();
        bookmarkId.setUserId(userId);
        bookmarkId.setTourSpotId(tourSpotId);

        return bookmarkId;
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
