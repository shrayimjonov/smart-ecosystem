package uz.uzkassa.cts.dto.tin.integration;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.ApplicationType;
import uz.uzkassa.cts.enums.OfdStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class OfdApplicationMakeObjectListDTO implements Serializable {

    Long id;

    Long ofdId;

    CommonDTO customer;

    CommonDTO customerBranch;

    CommonDTO branch;

    String tin;

    String deviceSerial;

    String fiscalModuleSerial;

    OfdStatus ofdStatus;

    LocalDateTime createdDate;

    String reason;

    String swapReason;

    String applicationFileUrl;

    ApplicationType applicationType;

    Long fiscalizationId;

    @JsonIgnore
    Long cusId;

    @JsonIgnore
    String cusName;

    public CommonDTO getCustomer() {
        return new CommonDTO(cusId, cusName);
    }

    @JsonIgnore
    Long cbrId;

    @JsonIgnore
    String cbrName;

    public CommonDTO getCustomerBranch() {
        return new CommonDTO(cbrId, cbrName);
    }

    @JsonIgnore
    Long brId;

    @JsonIgnore
    String brName;

    public CommonDTO getBranch() {
        return new CommonDTO(brId, brName);
    }
}
