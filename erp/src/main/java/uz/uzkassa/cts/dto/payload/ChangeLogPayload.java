package uz.uzkassa.cts.dto.payload;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangeLogPayload implements Serializable {

    Long entityId;

    EntityTypeEnum entityType;

    String entityName;

    String field;

    String oldValue;

    String newValue;

    Long updaterId;

    LocalDateTime updatedDate;

    String updaterName;
}
