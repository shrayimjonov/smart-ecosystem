package uz.uzkassa.smartpos.dto.warehouse.v2;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for {@link uz.uzkassa.smartpos.domain.postgres.warehouse.IncomeOfProductDetail}
 */
@Getter
@Setter
public class IncomeDetailCreateFromInvoiceDto extends IncomeOfProductDetailCreateDto {
    private InvoiceProductDto product;
}