package uz.uzkassa.smartpossupply.dto.activitytype;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.ActivityType;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ActivityTypeDTO extends CommonDTO {

    CommonDTO parent;

    public ActivityTypeDTO(Long id, String name, ActivityType parent) {
        super(id, name);
        if (parent != null) {
            this.parent = parent.toCommonDTO();
        }
    }
}
