package uz.uzkassa.smartpossupply.dto.clickhouse;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
public class AuditClickhouseDTO implements Serializable {

    Long creatorId;
    String creatorName;
    String createdDate;

    Long updaterId;
    String updaterName;
    String updatedDate;
}
