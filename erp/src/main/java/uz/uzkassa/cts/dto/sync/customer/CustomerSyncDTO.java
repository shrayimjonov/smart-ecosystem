package uz.uzkassa.cts.dto.sync.customer;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.company.CompanyCommonDTO;
import uz.uzkassa.cts.dto.sync.ActivityTypeSyncDTO;
import uz.uzkassa.cts.dto.sync.AddressSyncDTO;
import uz.uzkassa.cts.dto.sync.BankSyncDTO;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@ToString
public class CustomerSyncDTO extends CompanyCommonDTO implements Serializable {

    String relationId;

    String relationType;

    String companyName;

    String phone;

    String oked;

    BigDecimal vatRate;

    String businessType;

    ActivityTypeSyncDTO activityType;

    AddressSyncDTO address;

    CustomerDirectorSyncDTO director;

    CustomerDirectorSyncDTO manager;

    BankSyncDTO bank;
}
