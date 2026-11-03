package uz.uzkassa.silen.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class TransferDTO implements Serializable {
    @NotNull
    private String fromProductId;
    @NotNull
    private String toProductId;
    @NotNull
    private BigDecimal amount;
    private String note;
}
