package uz.uzkassa.horeca.dto.sync;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.company.CompanyCommonDTO;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@ToString
public class BranchSyncDTO extends CommonDTO implements Serializable {

    CompanyCommonDTO company;

    String brand;

    String phone;

    ActivityTypeSyncDTO activityType;

    AddressSyncDTO address;

    String cadastreNumber;

    CompanyOwnerSyncDTO owner;
}
