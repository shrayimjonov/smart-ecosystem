package uz.uzkassa.cts.dto.customer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.address.base.BaseAddressDetailDTO;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.CustomerBranchStatus;
import uz.uzkassa.cts.enums.OfdStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class AdminCustomerBranchListByFiscalDevicesDTO implements Serializable {

    boolean sync;

    boolean reSync;

    Long id;

    String name;

    String phone;

    CustomerBranchStatus status;

    OfdStatus ofdStatus;

    Long ofdId;

    String cadastreNumber;

    CommonDTO customer;

    CommonDTO branch;

    String tin;

    BaseAddressDetailDTO address;

    boolean hasAct;

    boolean hasInvoicePayment;

    LocalDateTime registrationDate;

    @JsonIgnore
    Long cusId;

    @JsonIgnore
    String cusName;

    public CommonDTO getCustomer() {
        return new CommonDTO(cusId, cusName);
    }

    @JsonIgnore
    Long brId;

    @JsonIgnore
    String brName;

    public CommonDTO getBranch() {
        return new CommonDTO(brId, brName);
    }

    @JsonIgnore
    String street;

    @JsonIgnore
    String house;

    @JsonIgnore
    String apartment;

    @JsonIgnore
    String addLongitude;

    @JsonIgnore
    String addLatitude;

    @JsonIgnore
    Long regId;

    @JsonIgnore
    String regNameRu;

    @JsonIgnore
    String regNameUzCyrillic;

    @JsonIgnore
    String regNameUzLatin;

    @JsonIgnore
    Long regCode;

    @JsonIgnore
    Long disId;

    @JsonIgnore
    String disNameRu;

    @JsonIgnore
    String disNameUzCyrillic;

    @JsonIgnore
    String disNameUzLatin;

    @JsonIgnore
    Long disCode;

    public BaseAddressDetailDTO getAddress() {
        BaseAddressDetailDTO addressDetailDTO = new BaseAddressDetailDTO();
        addressDetailDTO.setRegion(new CommonDTO(regId, regNameRu));
        addressDetailDTO.setDistrict(new CommonDTO(disId, disNameRu));
        addressDetailDTO.setStreet(street);
        addressDetailDTO.setHouse(house);
        addressDetailDTO.setApartment(apartment);
        addressDetailDTO.setLongitude(addLongitude);
        addressDetailDTO.setLatitude(addLatitude);
        return addressDetailDTO;
    }
}
