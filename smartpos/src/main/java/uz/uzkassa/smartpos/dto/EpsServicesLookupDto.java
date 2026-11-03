package uz.uzkassa.smartpos.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.EpsServices;
import uz.uzkassa.smartpos.enums.EPSMethodType;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link EpsServices}
 */
@Getter
@Setter
@NoArgsConstructor
public class EpsServicesLookupDto implements Serializable {
    private UUID id;
    private String name;
    private String nameRu;
    private String nameUz;
    private String nameCyrillic;
    private String code;
    private String logoUrl;
    private EPSMethodType qrMethod;
    private Short position;

    public EpsServicesLookupDto(EpsServices entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.nameRu = entity.getNameRu();
        this.nameUz = entity.getNameUz();
        this.nameCyrillic = entity.getNameCyrillic();
        this.code = entity.getType();
        if (entity.getLogoUrl() != null) {
            this.logoUrl = entity.getLogoUrl();
        } else if (entity.getFile() != null) {
            this.logoUrl = entity.getFile().getUrl();
        }
        this.qrMethod = entity.getQrMethod();
        this.position = entity.getPosition();
    }
}
