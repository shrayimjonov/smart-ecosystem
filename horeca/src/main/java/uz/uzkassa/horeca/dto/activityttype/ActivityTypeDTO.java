package uz.uzkassa.horeca.dto.activityttype;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ActivityTypeDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 43489583L;

    Long parentId;

    Long code;

    ActivityTypeDTO parent;

    public ActivityTypeDTO(Long id, String name, Long parentId) {
        super(id, name);
        this.parentId = parentId;
    }
}
