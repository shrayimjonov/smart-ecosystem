package uz.uzkassa.smartpossupply.dto.branch;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.dto.sync.company.ActivityTypeSyncDTO;
import uz.uzkassa.smartpossupply.dto.sync.company.AddressSyncDTO;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@ToString
public class BranchErpSyncDTO extends CommonDTO {

    String relationId;

    String relationType;

    CompanyBaseDTO company;

    ActivityTypeSyncDTO activityType;

    AddressSyncDTO address;
}
