package com.positivewand.tourin.domain.tourspot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

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
    @Column(name = "lat")
    private Double lat;
    @Column(name = "lng")
    private Double lng;

    @OneToMany(mappedBy = "tourSpot")
    @BatchSize(size = 50)
    private List<TourSpotTag> tags;
}
