package uz.uzkassa.smartpos.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.common.Excise;

import java.math.BigDecimal;


@Getter
@Setter
public class ExciseDTO {
    @NotNull(message = "Product is required")
    private String mxik;
    private String name;
    private BigDecimal price;

    public ExciseDTO(Excise entity) {
        this.mxik = entity.getMxik();
        this.name = entity.getName();
        this.price = entity.getPrice();
    }
}
