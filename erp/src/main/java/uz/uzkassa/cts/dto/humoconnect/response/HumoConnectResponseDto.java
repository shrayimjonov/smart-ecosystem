package uz.uzkassa.cts.dto.humoconnect.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.address.base.BaseAddressDetailDTO;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HumoConnectResponseDto implements Serializable {
    Long id;
    Long caseId;
    String creatorBranch;
    HumoConnectStatus status;
    HumoConnectOperatorStatus operatorStatus;
    String customerName;
    String customerTin;
    String customerOldTin;
    Long customerId;
    String mfo;
    BaseAddressDetailDTO address;
    String accountNumber;
    String deviceSerialNumber;
    String phone;
    LocalDateTime lastModifiedDate;
    String lastModifier;
    Long caseCreatorId;
    String caseCreatorName;
    BusinessType businessType;
    CustomerAppType appType;
    Boolean license;
    Long deviceLicenseId;
    BankType bankType;
    PaymentType paymentType;
    String activityType;
    String fileUrl;
    String fileName;
    DocFlowStatus docFlowStatus;
    CaseType caseType;
    LocalDateTime firstReceiptDate;
    String firstReceiptFiscalUrl;
    LocalDateTime lastReceiptDate;
    String lastReceiptFiscalUrl;

    @JsonIgnore
    Long regionId;
    @JsonIgnore
    String region;
    @JsonIgnore
    Long districtId;
    @JsonIgnore
    String district;
    @JsonIgnore
    String house;
    @JsonIgnore
    String street;
    @JsonIgnore
    String apartment;

    public BaseAddressDetailDTO getAddress() {
        return new BaseAddressDetailDTO(new CommonDTO(regionId, region), new CommonDTO(districtId, district), null, null, street, house, apartment, null, null);
    }
}
