package uz.uzkassa.smartpos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.EpsServices;
import uz.uzkassa.smartpos.enums.EPSServicesStatusEnum;

import java.io.Serializable;

/**
 * DTO for {@link EpsServices}
 */
@Getter
@Setter
public class EpsServicesCrudDto implements Serializable {
    private String name;
    private String nameRu;
    private String nameUz;
    private String nameCyrillic;
    @NotBlank
    private String type;
    private EPSServicesStatusEnum status;
    private Long fileId;
    @NotNull
    private boolean defaultService;
    @NotNull
    private Short position;
}
