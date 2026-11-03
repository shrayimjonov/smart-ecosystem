package uz.uzkassa.horeca.dto.eps;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.horeca.domain.EpsServiceDomain;
import uz.uzkassa.horeca.enums.terminal.EpsMethodType;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class TerminalEpsServicesLookupDto implements Serializable {
    private UUID id;
    private String name;
    private String nameRu;
    private String nameUz;
    private String nameCyrillic;
    private String code;
    private String logoUrl;
    private EpsMethodType qrMethod;
    private Short position;

    public TerminalEpsServicesLookupDto(EpsServiceDomain entity) {
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
