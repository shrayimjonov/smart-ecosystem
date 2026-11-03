package uz.uzkassa.silen.dto.warehouse;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A DTO for the {@link uz.uzkassa.silen.domain.PurchaseOrderProduct} entity.
 */
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PurchaseOrderProductDTO implements Serializable {

    Long orderId;

    String serialId;

    String productId;

    Long vatRate;

    BigDecimal vatSum;

    BigDecimal baseSumma;

    int count;

    BigDecimal summa;

    BigDecimal profitRate;

    BigDecimal withoutVat;
}
