package uz.uzkassa.horeca.domain.address;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;


@Getter
@Setter
@EqualsAndHashCode
@Embeddable
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Address implements Serializable {

    @Serial
    private static final long serialVersionUID = 5L;

    @Column(name = "landmark")
    String landmark;

    @Column(name = "street")
    String street;

    @Column(name = "house")
    String house;

    @Column(name = "apartment")
    String apartment;

    @Column(name = "longitude")
    Double longitude;

    @Column(name = "latitude")
    Double latitude;

    @Column(name = "region_id")
    Long regionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", updatable = false, insertable = false)
    Region region;

    @Column(name = "district_id")
    Long districtId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", updatable = false, insertable = false)
    District district;

    @Column(name = "neighborhood_id")
    Long neighborhoodId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "neighborhood_id", updatable = false, insertable = false)
    Neighborhood neighborhood;

    public String getFullAddress() {
        StringBuilder address = new StringBuilder();
        if (getRegionId() != null) {
            address.append(getRegion().getName()).append(" ");
        }
        if (getDistrictId() != null) {
            address.append(getDistrict().getName()).append(" ");
        }
        if (getNeighborhoodId() != null) {
            address.append(getNeighborhood().getName()).append(" ");
        }
        if (StringUtils.isNotEmpty(getStreet())) {
            address.append(getStreet()).append(" ");
        }
        if (StringUtils.isNotEmpty(getHouse())) {
            address.append(getHouse()).append(" ");
        }
        if (StringUtils.isNotEmpty(getApartment())) {
            address.append(getApartment()).append(" ");
        }
        return address.toString().trim();
    }
}
