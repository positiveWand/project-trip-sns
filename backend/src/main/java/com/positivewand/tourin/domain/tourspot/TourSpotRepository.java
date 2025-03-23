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
    @Query(
        value = """
            SELECT *
            FROM tour_spot ts
            WHERE (MATCH(ts.name) AGAINST(:query IN BOOLEAN MODE))
        """,
        countQuery = """
            SELECT COUNT(*)
            FROM tour_spot ts
            WHERE (MATCH(ts.name) AGAINST(:query IN BOOLEAN MODE))
        """,
            nativeQuery = true
    )
    Page<TourSpot> findByNameContaining(@Param("query") String query, Pageable pageable);

    @Query(
        value = """
            SELECT DISTINCT
                filtered_ts.id as id,
                filtered_ts.name as name,
                filtered_ts.description as description,
                filtered_ts.image_url as image_url,
                filtered_ts.full_address as full_address,
                filtered_ts.address1 as address2,
                filtered_ts.address2 as address1,
                filtered_ts.province_code as province_code,
                filtered_ts.district_code as district_code,
                filtered_ts.lat as lat,
                filtered_ts.lng as lng
            FROM (SELECT ts.* FROM tour_spot ts WHERE MATCH(ts.name) AGAINST(:query IN BOOLEAN MODE)) filtered_ts
            LEFT JOIN tour_spot_tag tag ON filtered_ts.id = tag.tour_spot_id
            WHERE (tag.tag IN (:tags))
        """,
        countQuery = """
            SELECT COUNT(DISTINCT *)
            FROM (SELECT ts.* FROM tour_spot ts WHERE MATCH(ts.name) AGAINST(:query IN BOOLEAN MODE)) filtered_ts
            LEFT JOIN tour_spot_tag tag ON filtered_ts.id = tag.tour_spot_id
            WHERE (tag.tag IN (:tags))
        """,
        nativeQuery = true)
    Page<TourSpot> findByNameAndTags(
            @Param("query") String query,
            @Param("tags") Collection<String> tags,
            Pageable pageable
    );

    @Query(
        value = """
            SELECT DISTINCT
                filtered_ts.id as id,
                filtered_ts.name as name,
                filtered_ts.description as description,
                filtered_ts.image_url as image_url,
                filtered_ts.full_address as full_address,
                filtered_ts.address1 as address2,
                filtered_ts.address2 as address1,
                filtered_ts.province_code as province_code,
                filtered_ts.district_code as district_code,
                filtered_ts.lat as lat,
                filtered_ts.lng as lng
            FROM (SELECT ts.* FROM tour_spot ts) filtered_ts
            LEFT JOIN tour_spot_tag tag ON filtered_ts.id = tag.tour_spot_id
            WHERE (tag.tag IN (:tags))
        """,
        countQuery = """
            SELECT COUNT(DISTINCT *)
            FROM (SELECT ts.* FROM tour_spot ts) filtered_ts
            LEFT JOIN tour_spot_tag tag ON filtered_ts.id = tag.tour_spot_id
            WHERE (tag.tag IN (:tags))
        """,
            nativeQuery = true)
    Page<TourSpot> findByTags(
            @Param("tags") Collection<String> tags,
            Pageable pageable
    );

    @Query(
        value = """
            SELECT *
            FROM tour_spot ts
            WHERE (MATCH(ts.name) AGAINST(:query IN BOOLEAN MODE))
            AND (ts.lat >= :minLat)
            AND (ts.lat <= :maxLat)
            AND (ts.lng >= :minLng)
            AND (ts.lng <= :maxLng)
        """,
        countQuery = """
            SELECT COUNT(*)
            FROM tour_spot ts
            WHERE (MATCH(ts.name) AGAINST(:query IN BOOLEAN MODE))
            AND (ts.lat >= :minLat)
            AND (ts.lat <= :maxLat)
            AND (ts.lng >= :minLng)
            AND (ts.lng <= :maxLng)
        """,
        nativeQuery = true
    )
    List<TourSpot> findByNameAndLatLngBounds(
            @Param("query") String query,
            @Param("minLat") Double minLat,
            @Param("minLng") Double minLng,
            @Param("maxLat") Double maxLat,
            @Param("maxLng") Double maxLng
    );

    @Query(
        value = """
            SELECT DISTINCT
                filtered_ts.id as id,
                filtered_ts.name as name,
                filtered_ts.description as description,
                filtered_ts.image_url as image_url,
                filtered_ts.full_address as full_address,
                filtered_ts.address1 as address2,
                filtered_ts.address2 as address1,
                filtered_ts.province_code as province_code,
                filtered_ts.district_code as district_code,
                filtered_ts.lat as lat,
                filtered_ts.lng as lng
            FROM (
                SELECT ts.* FROM tour_spot ts
                WHERE (MATCH(ts.name) AGAINST(:query IN BOOLEAN MODE))
                AND (ts.lat >= :minLat)
                AND (ts.lat <= :maxLat)
                AND (ts.lng >= :minLng)
                AND (ts.lng <= :maxLng)
            ) filtered_ts
            LEFT JOIN tour_spot_tag tag ON filtered_ts.id = tag.tour_spot_id
            WHERE (tag.tag IN (:tags))
        """,
        countQuery = """
            SELECT COUNT(DISTINCT *)
            FROM (
                SELECT ts.* FROM tour_spot ts
                WHERE (MATCH(ts.name) AGAINST(:query IN BOOLEAN MODE))
                AND (ts.lat >= :minLat)
                AND (ts.lat <= :maxLat)
                AND (ts.lng >= :minLng)
                AND (ts.lng <= :maxLng)
            ) filtered_ts
            LEFT JOIN tour_spot_tag tag ON filtered_ts.id = tag.tour_spot_id
            WHERE (tag.tag IN (:tags))
        """,
        nativeQuery = true
    )
    List<TourSpot> findByNameAndTagsAndLatLngBounds(
            @Param("query") String query,
            @Param("tags") Collection<String> tags,
            @Param("minLat") Double minLat,
            @Param("minLng") Double minLng,
            @Param("maxLat") Double maxLat,
            @Param("maxLng") Double maxLng
    );

    @Query(
        value = """
            SELECT DISTINCT
                filtered_ts.id as id,
                filtered_ts.name as name,
                filtered_ts.description as description,
                filtered_ts.image_url as image_url,
                filtered_ts.full_address as full_address,
                filtered_ts.address1 as address2,
                filtered_ts.address2 as address1,
                filtered_ts.province_code as province_code,
                filtered_ts.district_code as district_code,
                filtered_ts.lat as lat,
                filtered_ts.lng as lng
            FROM (
                SELECT ts.* FROM tour_spot ts
                WHERE (ts.lat >= :minLat)
                AND (ts.lat <= :maxLat)
                AND (ts.lng >= :minLng)
                AND (ts.lng <= :maxLng)
            ) filtered_ts
            LEFT JOIN tour_spot_tag tag ON filtered_ts.id = tag.tour_spot_id
            WHERE (tag.tag IN (:tags))
        """,
        countQuery = """
            SELECT COUNT(DISTINCT *)
            FROM (
                SELECT ts.* FROM tour_spot ts
                WHERE (ts.lat >= :minLat)
                AND (ts.lat <= :maxLat)
                AND (ts.lng >= :minLng)
                AND (ts.lng <= :maxLng)
            ) filtered_ts
            LEFT JOIN tour_spot_tag tag ON filtered_ts.id = tag.tour_spot_id
            WHERE (tag.tag IN (:tags))
        """,
            nativeQuery = true
    )
    List<TourSpot> findByTagsAndLatLngBounds(
            @Param("tags") Collection<String> tags,
            @Param("minLat") Double minLat,
            @Param("minLng") Double minLng,
            @Param("maxLat") Double maxLat,
            @Param("maxLng") Double maxLng
    );
}
