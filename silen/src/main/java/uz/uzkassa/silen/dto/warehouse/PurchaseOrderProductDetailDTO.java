package uz.uzkassa.silen.dto.warehouse;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.VatRateDTO;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A DTO for the {@link uz.uzkassa.silen.domain.PurchaseOrderProduct} entity.
 */
@Getter
@Setter
@ToString
public class PurchaseOrderProductDetailDTO implements Serializable {

    Long id;

    SelectItem serial;

    SelectItem product;

    VatRateDTO vatRate;

    BigDecimal vatSum;

    BigDecimal baseSumma;

    BigDecimal count;

    BigDecimal summa;

    BigDecimal profitRate;

    BigDecimal withoutVat;
}
