package com.positivewand.tourin.domain.tourspot.entity;

public enum TourSpotCategory {
    NATURE("자연"),
    HISTORY("역사"),
    REST("휴양"),
    EXPERIENCE("체험"),
    INDUSTRY("산업"),
    ARCHITECTURE("건축/조형"),
    CULTURE("문화"),
    FESTIVAL("축제"),
    CONCERT("공연/행사");

    private final String viewString;

    private TourSpotCategory(String viewString) {
        this.viewString = viewString;
    }

    public String getViewString() {
        return this.viewString;
    }
}
