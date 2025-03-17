package com.positivewand.tourin.web.user.response;

import com.positivewand.tourin.domain.user.dto.BookmarkDto;
import com.positivewand.tourin.web.tourspot.response.TourSpotOverviewResponse;

public record BookmarkResponse(
        String userId,
        String tourSpotId,
        TourSpotOverviewResponse tourSpotOverview
) {
    public static BookmarkResponse createFromDto(BookmarkDto bookmarkDto) {
        return new BookmarkResponse(
                bookmarkDto.username(),
                bookmarkDto.tourSpotId().toString(),
                TourSpotOverviewResponse.createFromTourSpotDto(bookmarkDto.tourSpotDto())
        );
    }
}
