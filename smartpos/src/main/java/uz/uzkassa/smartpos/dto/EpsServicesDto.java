package uz.uzkassa.smartpos.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.EpsServices;
import uz.uzkassa.smartpos.enums.EPSMethodType;
import uz.uzkassa.smartpos.enums.EPSServicesStatusEnum;
import uz.uzkassa.smartpos.integration.apay.FileDTO;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link EpsServices}
 */
@Getter
@Setter
@NoArgsConstructor
public class EpsServicesDto implements Serializable {
    private UUID id;
    private String name;
    private String nameRu;
    private String nameUz;
    private String nameCyrillic;
    private String type;
    private EPSServicesStatusEnum status;
    private FileDTO logo;
    private EPSMethodType qrMethod;
    private CommonFileDTO file;
    private String createdBy;
    private LocalDateTime createdDate;
    private String updatedBy;
    private LocalDateTime updatedDate;
    private boolean fromApay;
    private boolean defaultService = false;
    private Short position;

    public EpsServicesDto(EpsServices entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.nameRu = entity.getNameRu();
        this.nameUz = entity.getNameUz();
        this.nameCyrillic = entity.getNameCyrillic();
        this.type = entity.getType();
        this.status = entity.getStatus();
        if (entity.getLogoUrl() != null) {
            this.logo = new FileDTO(entity.getLogoId(), entity.getLogoUrl());
        }
        this.qrMethod = entity.getQrMethod();
        if (entity.getFile() != null) {
            this.file = new CommonFileDTO(entity.getFile().getId(), entity.getFile().getUrl());
        }
        this.createdBy = entity.getCreatedBy();
        this.createdDate = entity.getCreatedDate();
        this.updatedBy = entity.getUpdatedBy();
        this.updatedDate = entity.getUpdatedDate();
        this.fromApay = entity.isFromApay();
        this.defaultService = entity.isDefaultService();
        this.position = entity.getPosition();
    }
}
