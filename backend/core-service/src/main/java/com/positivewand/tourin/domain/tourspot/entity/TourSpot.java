package com.positivewand.tourin.domain.tourspot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tour_spot")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TourSpot {
    @Id
    private Long id;

    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name="image_url")
    private String imageUrl;
    @Column(name="full_address")
    private String fullAddress;
    @Column(name = "address1")
    private String address1;
    @Column(name = "address2")
    private String address2;
    @Column(name="province_code")
    private Integer provinceCode;
    @Column(name="district_code")
    private Integer districtCode;
    @Column(name="phone_number")
    private String phoneNumber;
    @Column(name = "lat")
    private Double lat;
    @Column(name = "lng")
    private Double lng;

    @OneToMany(mappedBy = "tourSpot", cascade = CascadeType.ALL)
    @BatchSize(size = 50)
    private List<TourSpotTag> tags = new ArrayList<>();

    public static TourSpot create(
            long id,
            String name,
            String description,
            String imageUrl,
            String fullAddress,
            String address1,
            String address2,
            int provinceCode,
            int districtCode,
            String phoneNumber,
            double lat,
            double lng,
            List<TourSpotCategory> tags
    ) {
        TourSpot newTourSpot = new TourSpot();

        newTourSpot.id = id;
        newTourSpot.name = name;
        newTourSpot.description = description;
        newTourSpot.imageUrl = imageUrl;
        newTourSpot.fullAddress = fullAddress;
        newTourSpot.address1 = address1;
        newTourSpot.address2 = address2;
        newTourSpot.provinceCode = provinceCode;
        newTourSpot.districtCode = districtCode;
        newTourSpot.phoneNumber = phoneNumber;
        newTourSpot.lat = lat;
        newTourSpot.lng = lng;
        newTourSpot.tags = new ArrayList<>();

        for (TourSpotCategory tag: tags) {
            newTourSpot.addTag(tag);
        }

        return newTourSpot;
    }

    public void addTag(TourSpotCategory tag) {
        TourSpotTag newTag = TourSpotTag.create(this, tag);
        this.tags.add(newTag);
    }
}
