package uz.uzkassa.horeca.dto.terminal;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.activityttype.ActivityTypeDTO;
import uz.uzkassa.horeca.dto.sync.AddressSyncDTO;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApayBranchRegisterDTO {

    String referenceId;

    String name;

    String phone;

    AddressSyncDTO address;

    ActivityTypeDTO activityType;
}
