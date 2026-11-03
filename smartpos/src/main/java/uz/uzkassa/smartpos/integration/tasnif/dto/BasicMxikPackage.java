package uz.uzkassa.smartpos.integration.tasnif.dto;

import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class BasicMxikPackage implements Serializable {
    private Integer basicPackageCode;
    private String mxikCode;
    private String basicPackageNameUz;
    private String basicPackageNameRu;
    private String basicPackageNameLat;

    @Transient
    public String getDescription() {
        return basicPackageCode + " - " + basicPackageNameRu;
    }
}
