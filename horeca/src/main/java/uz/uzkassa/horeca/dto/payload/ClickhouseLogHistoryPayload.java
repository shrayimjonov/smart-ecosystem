package uz.uzkassa.horeca.dto.payload;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ClickhouseLogHistoryPayload implements Serializable {

    Long entityId;

    String entityType;

    String field;

    Object oldValue;

    Object newValue;

    Long updaterId;

    String updaterLogin;

    Long companyId;
}
