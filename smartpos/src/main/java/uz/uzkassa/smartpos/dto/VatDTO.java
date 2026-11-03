package uz.uzkassa.smartpos.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;


@Getter
@Setter
public class VatDTO extends BaseAuditDTO implements Serializable {
    private Long id;
    @NotEmpty(message = "Name required")
    private String name;
    @NotNull(message = "Percent required")
    private BigDecimal percent;
}
