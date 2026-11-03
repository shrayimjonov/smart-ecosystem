package uz.uzkassa.smartpos.dto.integration.promocode;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class PromoCodeApplyDto implements Serializable {
    @NotNull
    @NotEmpty
    private String tin;

    @NotNull
    private Long xizmatId;

    @NotNull
    @NotEmpty
    private String code;
}

