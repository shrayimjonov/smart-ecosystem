package uz.uzkassa.smartpossupply.domain.address;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

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

    @Column(name = "street")
    String street;

    @Column(name = "house")
    String house;

    @Column(name = "apartment")
    String apartment;

    @Column(name = "longitude")
    String longitude;

    @Column(name = "latitude")
    String latitude;

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

    public String getFullAddress() {
        StringBuilder address = new StringBuilder();
        if (getRegion() != null) {
            address.append(getRegion().getName()).append(" ");
        }
        if (getDistrict() != null) {
            address.append(getDistrict().getName()).append(" ");
        }
        if (getStreet() != null) {
            address.append(getStreet()).append(" ");
        }
        if (getHouse() != null) {
            address.append(getHouse()).append(" ");
        }
        if (getApartment() != null) {
            address.append(getApartment()).append(" ");
        }

        return address.toString();
    }


    @Override
    public String toString() {
        return "Address{" +
                "street='" + street + '\'' +
                ", house='" + house + '\'' +
                ", apartment='" + apartment + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", regionId=" + regionId +
                ", region=" + region +
                ", districtId=" + districtId +
                ", district=" + district +
                '}';
    }
}
