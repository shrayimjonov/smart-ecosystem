package uz.uzkassa.smartpossupply.dto.sync.company;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@ToString
public class CompanyBranchSyncDTO extends CommonDTO {

    CompanyBaseDTO company;

    String brand;

    String phone;

    ActivityTypeSyncDTO activityType;

    AddressSyncDTO address;

    String cadastreNumber;
}
