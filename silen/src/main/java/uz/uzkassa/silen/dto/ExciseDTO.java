package uz.uzkassa.silen.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.silen.enumeration.ProductType;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A DTO for the {@link uz.uzkassa.silen.domain.Excise} entity.
 */
@Getter
@Setter
@ToString
public class ExciseDTO implements Serializable {
    private Long id;
    @NotNull
    private ProductType type;
    private String typeName;
    @NotNull
    private BigDecimal amount;
}
