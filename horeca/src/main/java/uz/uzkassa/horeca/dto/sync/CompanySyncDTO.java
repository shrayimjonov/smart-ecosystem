package uz.uzkassa.horeca.dto.sync;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.company.CompanyCommonDTO;
import uz.uzkassa.horeca.enums.RelationType;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@ToString
public class CompanySyncDTO extends CompanyCommonDTO implements Serializable {

    String relationId;

    RelationType relationType;

    String companyName;

    String phone;

    String oked;

    BigDecimal vatRate;

    String businessType;

    ActivityTypeSyncDTO activityType;

    AddressSyncDTO address;

    CompanyOwnerSyncDTO director;

    CompanyOwnerSyncDTO manager;

    BankSyncDTO bank;
}
