package uz.uzkassa.smartpossupply.dto.payload;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 14.07.2023
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClickhouseLogHistoryPayload implements Serializable {

    Long entityId;

    String entityType;

    String entityName;

    String field;

    Object oldValue;

    Object newValue;

    Long updaterId;

    String updaterLogin;

    Long companyId;

    Long branchId;
}
