package uz.uzkassa.smartpos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.receipt.ReceiptLimit;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link ReceiptLimit}
 */
@Getter
@Setter
@NoArgsConstructor
public class ReceiptLimitDto implements Serializable {
    @NotBlank
    private BigDecimal price;
    @NotNull
    private String code;
    private String nameRu;
    private String nameUzLatin;
    private String nameUzCyrillic;
}