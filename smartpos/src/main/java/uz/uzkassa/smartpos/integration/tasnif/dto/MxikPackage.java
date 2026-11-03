package uz.uzkassa.smartpos.integration.tasnif.dto;

import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class MxikPackage implements Serializable {
    private Integer code;
    private String mxikCode;
    private String nameUz;
    private String packageType;
    private String nameRu;
    private String nameLat;

    @Transient
    public String getDescription() {
        return code + " - " + nameRu;
    }

    public MxikPackage(BasicMxikPackage basic) {
        this.code = basic.getBasicPackageCode();
        this.mxikCode = basic.getMxikCode();
        this.nameUz = basic.getBasicPackageNameUz();
        this.nameRu = basic.getBasicPackageNameRu();
        this.nameLat = basic.getBasicPackageNameLat();
    }
}
