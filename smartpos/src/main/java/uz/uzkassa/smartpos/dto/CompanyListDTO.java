package uz.uzkassa.smartpos.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.dto.factura.BankDTO;
import uz.uzkassa.smartpos.enums.BusinessType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CompanyListDTO implements Serializable {
    Long id;
    String name;
    String inn;
    String oldInn;
    List<ActivityTypeDTO> activityType;
    List<CommonDTO> branches;
    String phone;
    BusinessType businessType;
    LocalDateTime createdDate;
    String createdBy;
    BankDTO bank;
    Long deviceTotal;
    Long fiscalModuleTotal;
    String address;
    RegionDTO region;
    CityDTO city;
    String mfo;
    BigDecimal ndsPercent;
    String accountNumber;
    String oked;
    String apayTariffId;

}

