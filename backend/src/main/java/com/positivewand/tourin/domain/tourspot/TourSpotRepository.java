package com.positivewand.tourin.domain.tourspot;

import com.positivewand.tourin.domain.tourspot.entity.TourSpot;
import com.positivewand.tourin.domain.tourspot.entity.TourSpotCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface TourSpotRepository extends JpaRepository<TourSpot, Long> {
    Page<TourSpot> findByNameContaining(String query, Pageable pageable);

    @Query("""
        SELECT DISTINCT ts FROM TourSpot ts
        LEFT JOIN ts.tags tag
        WHERE (:query IS NULL OR ts.name LIKE CONCAT('%', :query, '%'))
        AND (tag.tag IN (:tags))
    """)
    Page<TourSpot> findByNameAndTags(
            @Param("query") String query,
            @Param("tags") Collection<String> tags,
            Pageable pageable
    );

    @Query("""
        SELECT DISTINCT ts FROM TourSpot ts
        WHERE (:query IS NULL OR ts.name LIKE CONCAT('%', :query, '%'))
        AND (:minLat IS NULL OR ts.lat >= :minLat)
        AND (:maxLat IS NULL OR ts.lat <= :maxLat)
        AND (:minLng IS NULL OR ts.lng >= :minLng)
        AND (:maxLng IS NULL OR ts.lng <= :maxLng)
    """)
    List<TourSpot> findByNameAndLatLngBounds(
            @Param("query") String query,
            @Param("minLat") Double minLat,
            @Param("minLng") Double minLng,
            @Param("maxLat") Double maxLat,
            @Param("maxLng") Double maxLng
    );

    @Query("""
        SELECT DISTINCT ts FROM TourSpot ts
        LEFT JOIN ts.tags tag
        WHERE (:query IS NULL OR ts.name LIKE CONCAT('%', :query, '%'))
        AND (tag.tag IN (:tags))
        AND (:minLat IS NULL OR ts.lat >= :minLat)
        AND (:maxLat IS NULL OR ts.lat <= :maxLat)
        AND (:minLng IS NULL OR ts.lng >= :minLng)
        AND (:maxLng IS NULL OR ts.lng <= :maxLng)
    """)
    List<TourSpot> findByNameAndTagsAndLatLngBounds(
            @Param("query") String query,
            @Param("tags") Collection<String> tags,
            @Param("minLat") Double minLat,
            @Param("minLng") Double minLng,
            @Param("maxLat") Double maxLat,
            @Param("maxLng") Double maxLng
    );
}
