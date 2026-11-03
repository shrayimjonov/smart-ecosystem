package uz.uzkassa.smartpos.domain.postgres.common;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Embeddable
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Address implements Serializable {

    @Serial
    private static final long serialVersionUID = 5L;
    String street;
    String house;
    String apartment;
    String longitude;
    String latitude;
    Long regionId;
    Long regionCode;
    String regionName;
    Long districtId;
    Long districtCode;
    String districtName;

}
