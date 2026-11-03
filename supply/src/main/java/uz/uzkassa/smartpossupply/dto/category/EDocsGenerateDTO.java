package uz.uzkassa.smartpossupply.dto.category;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;

import java.io.Serializable;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 07.05.2024
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class EDocsGenerateDTO implements Serializable {

    Long entityId;

    EntityTypeEnum entityType; // from entity

    EntityTypeEnum docType; // to doc

    Long companyId;

    public EDocsGenerateDTO(Long entityId, EntityTypeEnum entityType, EntityTypeEnum docType,Long companyId) {
        this.entityId = entityId;
        this.entityType = entityType;
        this.docType = docType;
        this.companyId = companyId;
    }

    public EDocsGenerateDTO(Long entityId, EntityTypeEnum entityType,Long companyId) {
        this.entityId = entityId;
        this.entityType = entityType;
        this.companyId = companyId;
    }
}
