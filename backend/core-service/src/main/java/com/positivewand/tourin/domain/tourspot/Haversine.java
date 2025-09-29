package com.positivewand.tourin.domain.tourspot;

public class Haversine {
    private static final double EARTH_RADIUS = 6378;

    public static double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        // 위도, 경도를 라디안 단위로 변환
        double diffLat = Math.toRadians(lat2 - lat1);
        double diffLng = Math.toRadians(lng2 - lng1);

        // Haversine 공식
        double a = Math.sin(diffLat / 2) * Math.sin(diffLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(diffLng / 2) * Math.sin(diffLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // 거리(km) 계산
        return EARTH_RADIUS * c;
    }
}
