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
public class EpsServiceLookupDTO implements Serializable {

    private UUID id;
    private String name;
    private String code;
    private String logoUrl;
    private EpsMethodType qrMethod;
    private Short position;

    public EpsServiceLookupDTO(EpsServiceDomain entity) {
        this.id = entity.getId();
        this.name = entity.getNameRu();
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
