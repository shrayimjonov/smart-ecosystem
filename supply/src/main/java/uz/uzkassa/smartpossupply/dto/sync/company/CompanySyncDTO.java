package uz.uzkassa.smartpossupply.dto.sync.company;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@ToString
public class CompanySyncDTO implements Serializable {

    Long id;

    String name;

    String companyName;

    String tin;

    String pinfl;

    String phone;

    String oked;

    BigDecimal vatRate;

    String businessType;

    ActivityTypeSyncDTO activityType;

    AddressSyncDTO address;

    CompanyEmployeeSyncDTO director;

    CompanyEmployeeSyncDTO manager;

    CompanyBankSyncDTO bank;
}
