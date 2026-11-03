package uz.uzkassa.cts.domain.address;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.enums.AddressSeparatorType;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaseAddress implements Serializable {

    @Serial
    private static final long serialVersionUID = 84958349L;

    @Column(name = "landmark")
    String landmark;

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

    @Column(name = "neighborhood_id")
    Long neighborhoodId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "neighborhood_id", updatable = false, insertable = false)
    Neighborhood neighborhood;

    public String getAddress() {
        StringBuilder address = new StringBuilder();
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

    public String getFullAddressAddress(AddressSeparatorType separatorType) {
        String separator = switch (separatorType) {
            case SPACE ->
                    " ";
            case COMMA ->
                    ", ";
            case DOT ->
                    ". ";
        };

        StringBuilder address = new StringBuilder();
        if (getRegion() != null) {
            address.append(getRegion().getName()).append(separator);
        }
        if (getDistrict() != null) {
            address.append(getDistrict().getName()).append(separator);
        }
        if (StringUtils.isNotEmpty(getStreet())) {
            address.append(getStreet()).append(separator);
        }
        if (StringUtils.isNotEmpty(getHouse())) {
            address.append(getHouse()).append(separator);
        }
        if (StringUtils.isNotEmpty(getApartment())) {
            address.append(getApartment()).append(separator);
        }
        return address.toString().trim();
    }

    public String getFullAddressAddress() {
        return this.getFullAddressAddress(AddressSeparatorType.SPACE);
    }

}
