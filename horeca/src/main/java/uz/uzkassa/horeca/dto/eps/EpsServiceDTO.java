package uz.uzkassa.horeca.dto.eps;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.horeca.domain.EpsServiceDomain;
import uz.uzkassa.horeca.enums.terminal.EpsMethodType;
import uz.uzkassa.horeca.enums.terminal.EpsServiceStatusEnum;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class EpsServiceDTO implements Serializable {

    private UUID id;
    private String name;
    private String type;
    private EpsServiceStatusEnum status;
    private EpsLogoDTO logo;
    private EpsMethodType qrMethod;
    private EpsFileDTO file;
    private String createdBy;
    private LocalDateTime createdDate;
    private String updatedBy;
    private LocalDateTime updatedDate;
    private boolean fromApay;
    private boolean defaultService = false;
    private Short position;

    public EpsServiceDTO(EpsServiceDomain entity) {
        this.id = entity.getId();
        this.name = entity.getNameRu();
        this.type = entity.getType();
        this.status = entity.getStatus();
        if (entity.getLogoUrl() != null) {
            this.logo = new EpsLogoDTO(entity.getLogoId(), entity.getLogoUrl());
        }
        this.qrMethod = entity.getQrMethod();
        if (entity.getFile() != null) {
            this.file = new EpsFileDTO(entity.getFile().getId(), entity.getFile().getUrl());
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
