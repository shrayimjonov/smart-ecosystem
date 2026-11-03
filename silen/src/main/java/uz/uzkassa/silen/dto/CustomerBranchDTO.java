package uz.uzkassa.silen.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerBranchDTO implements Serializable {

    Long id;
    String name;

    String num;

    Long regionId;

    String regionName;

    String districtId;

    String districtName;

    String directorTin;

    String directorName;

    Long customerId;

    SelectItem customer;

    private Double lat;

    private Double lon;

    private String address;
}
