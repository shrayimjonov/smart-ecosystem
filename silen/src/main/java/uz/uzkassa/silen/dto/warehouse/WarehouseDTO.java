package uz.uzkassa.silen.dto.warehouse;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.silen.enumeration.PackageType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class WarehouseDTO implements Serializable {
    private String productId;
    private String productName;
    private String operation;
    private String operationName;
    private LocalDateTime operationDate;
    private BigDecimal amount = BigDecimal.ZERO;
    private PackageType unit;
    private String unitName;
}
