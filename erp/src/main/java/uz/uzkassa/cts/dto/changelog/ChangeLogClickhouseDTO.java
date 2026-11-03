package uz.uzkassa.cts.dto.changelog;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangeLogClickhouseDTO implements Serializable {

    UUID id;

    Long entityId;

    String entityName;

    String entityType;

    String field;

    String oldValue;

    String newValue;

    Long updaterId;

    String updaterName;

    String updatedDate;
}
