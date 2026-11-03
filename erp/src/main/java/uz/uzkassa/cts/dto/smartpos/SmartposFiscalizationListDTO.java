package uz.uzkassa.cts.dto.smartpos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.BusinessType;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class SmartposFiscalizationListDTO implements Serializable {

    SmartposCustomerDetailDTO customer;

    SmartposCustomerBranchDetailDTO customerBranch;

    String deviceSerial;

    String fiscalModuleSerial;

    Long terminalId;

    @JsonIgnore
    Long cusId;

    @JsonIgnore
    String cusName;

    @JsonIgnore
    String cusTin;

    @JsonIgnore
    BusinessType cusBusinessType;

    @JsonIgnore
    BigDecimal cusVat;

    @JsonIgnore
    Long regionCode;

    @JsonIgnore
    Long districtCode;

    @JsonIgnore
    String street;

    @JsonIgnore
    String house;

    @JsonIgnore
    String apartment;

    public SmartposCustomerDetailDTO getCustomer() {
        return new SmartposCustomerDetailDTO(cusId, cusName, cusTin, cusBusinessType, cusVat,
            new SmartPosAddressDTO(regionCode, districtCode, street, house, apartment, null, null));
    }

    @JsonIgnore
    Long brId;

    @JsonIgnore
    String brName;

    @JsonIgnore
    BusinessType brBusinessType;

    @JsonIgnore
    String brLongitude;

    @JsonIgnore
    String brLatitude;

    @JsonIgnore
    Long brRegionCode;

    @JsonIgnore
    Long brDistrictCode;

    @JsonIgnore
    String brStreet;

    @JsonIgnore
    String brHouse;

    @JsonIgnore
    String brApartment;

    public SmartposCustomerBranchDetailDTO getCustomerBranch() {
        return new SmartposCustomerBranchDetailDTO(brId, brName, brBusinessType,
            new SmartPosAddressDTO(brRegionCode, brDistrictCode, brStreet, brHouse, brApartment, brLongitude, brLatitude),
            brLongitude, brLatitude);
    }
}
