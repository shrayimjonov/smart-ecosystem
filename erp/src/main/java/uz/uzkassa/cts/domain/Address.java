package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.cts.domain.address.District;
import uz.uzkassa.cts.domain.address.Neighborhood;
import uz.uzkassa.cts.domain.address.Region;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.AddressSeparatorType;
import uz.uzkassa.cts.enums.AddressType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.interfaces.Auditable;

import java.io.Serial;
import java.util.Objects;

@Entity
@Table(name = "address", indexes = {
        @Index(columnList = "entity_id", name = "address_entity_idx"),
        @Index(columnList = "entity_type", name = "address_entity_type_idx"),
        @Index(columnList = "type", name = "address_type_idx")
})
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE address SET deleted = 'true' WHERE id=?")
public class Address extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 11094759723833L;

    @Column(name = "entity_id", nullable = false)
    Long entityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false)
    EntityTypeEnum entityType;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    AddressType type;

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
            case SPACE -> " ";
            case COMMA -> ", ";
            case DOT -> ". ";
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

    public void setEntityId(Long entityId) {
        if (!Objects.equals(this.entityId, entityId)) {
            addChangeLog("entityId", this.entityId != null ? this.entityId.toString() : null, entityId != null ? entityId.toString() : null);
        }
        this.entityId = entityId;
    }

    public void setEntityType(EntityTypeEnum entityType) {
        if (!Objects.equals(this.entityType, entityType)) {
            addChangeLog("entityType", this.entityType != null ? this.entityType.getName() : null, entityType != null ? entityType.getName() : null);
        }
        this.entityType = entityType;
    }

    public void setType(AddressType type) {
        if (!Objects.equals(this.type, type)) {
            addChangeLog("type", this.type != null ? this.type.getName() : null, type != null ? type.getName() : null);
        }
        this.type = type;
    }

    public void setLandmark(String landmark) {
        if (!Objects.equals(this.landmark, landmark)) {
            addChangeLog("landmark", this.landmark, landmark);
        }
        this.landmark = landmark;
    }

    public void setStreet(String street) {
        if (!Objects.equals(this.street, street)) {
            addChangeLog("street", this.street, street);
        }
        this.street = street;
    }

    public void setHouse(String house) {
        if (!Objects.equals(this.house, house)) {
            addChangeLog("house", this.house, house);
        }
        this.house = house;
    }

    public void setApartment(String apartment) {
        if (!Objects.equals(this.apartment, apartment)) {
            addChangeLog("apartment", this.apartment, apartment);
        }
        this.apartment = apartment;
    }

    public void setLongitude(String longitude) {
        if (!Objects.equals(this.longitude, longitude)) {
            addChangeLog("longitude", this.longitude, longitude);
        }
        this.longitude = longitude;
    }

    public void setLatitude(String latitude) {
        if (!Objects.equals(this.latitude, latitude)) {
            addChangeLog("latitude", this.latitude, latitude);
        }
        this.latitude = latitude;
    }

    public void setRegion(Region region) {
        if (!Objects.equals(this.region, region)) {
            addChangeLog("region", this.region != null ? this.region.getName() : null, region != null ? region.getName() : null);
        }
        this.region = region;
    }

    public void setDistrict(District district) {
        if (!Objects.equals(this.district, district)) {
            addChangeLog("district", this.district != null ? this.district.getName() : null, district != null ? district.getName() : null);
        }
        this.district = district;
    }

    public void setNeighborhood(Neighborhood neighborhood) {
        if (!Objects.equals(this.neighborhood, neighborhood)) {
            addChangeLog("neighborhood", this.neighborhood != null ? this.neighborhood.getName() : null, neighborhood != null ? neighborhood.getName() : null);
        }
        this.neighborhood = neighborhood;
    }
}
