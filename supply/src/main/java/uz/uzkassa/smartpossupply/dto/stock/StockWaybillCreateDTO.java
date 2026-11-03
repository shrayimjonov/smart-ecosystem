package uz.uzkassa.smartpossupply.dto.stock;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductWaybillDTO;
import uz.uzkassa.smartpossupply.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class StockWaybillCreateDTO {
    CommonDTO owner;
    CommonDTO customer;
    CommonDTO company;
    List<ProductWaybillDTO> products = new ArrayList<>();
    OrderStatus orderStatus;
    String stockNumber;

    @Schema(title = "сумма договора")
    BigDecimal price;

    BigDecimal salePrice;

    @Schema(title = "сумма оплаты")
    BigDecimal paymentAmount;
}
