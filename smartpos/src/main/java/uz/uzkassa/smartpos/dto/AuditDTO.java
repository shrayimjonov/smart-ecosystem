package uz.uzkassa.smartpos.dto;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.enums.AuditedFields;
import uz.uzkassa.smartpos.enums.EntityType;

import java.time.LocalDateTime;

@Getter
@Setter
public class AuditDTO {
    private String id;
    private EntityType entityType;
    private Object entityID;
    private String updater;
    private AuditedFields field;
    private LocalDateTime modificationDate;
    private String oldValue;
    private String newValue;
    private Long companyId;
}
