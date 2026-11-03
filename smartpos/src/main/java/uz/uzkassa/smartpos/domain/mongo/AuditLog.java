package uz.uzkassa.smartpos.domain.mongo;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import uz.uzkassa.smartpos.dto.AuditDTO;
import uz.uzkassa.smartpos.enums.AuditedFields;
import uz.uzkassa.smartpos.enums.EntityType;

import java.time.LocalDateTime;

@Getter
@Setter
@Document(collection = "audit_log")
public class AuditLog {
    @Id
    private String id;
    @Indexed
    @Enumerated(EnumType.STRING)
    private AuditedFields field;
    private String fromStringValue;
    private String toStringValue;
    private Long entityID;
    @Enumerated(EnumType.STRING)
    private EntityType entityType;
    private String updater;
    private LocalDateTime modificationDate;
    private Long companyId;

    public AuditDTO toDTO() {
        AuditDTO dto = new AuditDTO();
        dto.setId(getId());
        dto.setField(getField());
        dto.setEntityID(getEntityID());
        dto.setEntityType(getEntityType());
        dto.setUpdater(getUpdater());
        dto.setModificationDate(getModificationDate());
        dto.setOldValue(getFromStringValue().replaceAll("\\|", ""));
        dto.setNewValue(getToStringValue().replaceAll("\\|", ""));
        dto.setCompanyId(getCompanyId());
        return dto;
    }
}
