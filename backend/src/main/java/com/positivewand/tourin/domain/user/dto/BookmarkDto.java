package com.positivewand.tourin.domain.user.dto;

import com.positivewand.tourin.domain.tourspot.dto.TourSpotDto;
import com.positivewand.tourin.domain.tourspot.entity.TourSpot;
import com.positivewand.tourin.domain.user.entity.Bookmark;
import com.positivewand.tourin.domain.user.entity.User;

public record BookmarkDto(String username, Long tourSpotId, TourSpotDto tourSpotDto) {
    public static BookmarkDto createFromBookmark(Bookmark bookmark) {
        User user = bookmark.getUser();
        TourSpot tourSpot = bookmark.getTourSpot();
        return new BookmarkDto(
            user.getUsername(),
            tourSpot.getId(),
            TourSpotDto.create(tourSpot)
        );
    }
}
