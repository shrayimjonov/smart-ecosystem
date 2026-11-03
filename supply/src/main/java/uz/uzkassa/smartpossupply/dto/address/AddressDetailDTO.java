package uz.uzkassa.smartpossupply.dto.address;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@ToString
public class AddressDetailDTO implements Serializable {

    CommonDTO region;

    CommonDTO district;

    String street;

    String house;

    String apartment;

    String longitude;

    String latitude;

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

    public String getMobileFullAddress() {
        StringBuilder address = new StringBuilder();
        if (getStreet() != null) {
            address.append(getStreet()).append(" ");
        }
        if (getHouse() != null) {
            address.append(getHouse()).append(" ");
        }
        if (getApartment() != null) {
            address.append(getApartment()).append(" ");
        }
        if (StringUtils.isEmpty(address.toString())) {
            if (getRegion() != null) {
                address.append(getRegion().getName()).append(" ");
            }
            if (getDistrict() != null) {
                address.append(getDistrict().getName()).append(" ");
            }
        }
        return address.toString();
    }
}
