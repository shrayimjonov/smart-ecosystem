package uz.uzkassa.cts.dto.changelog;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangeLogListDTO implements Serializable {

    String id;

    Long entityId;

    EntityTypeEnum entityType;

    String entityName;

    String field;

    String oldValue;

    String newValue;

    CommonDTO updater;

    LocalDateTime updatedDate;
}
