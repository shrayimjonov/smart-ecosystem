package uz.uzkassa.smartpos.integration.supply.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Setter
@NoArgsConstructor
public class SupplierOrderProductDTO {
    Long id;
    Long orderProductId;
    Long unitId;
    BigDecimal qty;
    BigDecimal price;//price with NDS for 1x
    BigDecimal priceWithoutVat;//price without NDS for 1x
    BigDecimal totalPrice; //qty * price. Total price with NDS
    BigDecimal totalPriceWithoutVat; //qty * priceWithoutVat. Total price without NDS
    BigDecimal vatRate; //12%, 15%, null
    BigDecimal vatPrice; //NDS for 1x
    BigDecimal totalVatPrice;//qty * vatPrice

    public SupplierOrderProductDTO(CreateOrderProductDTO dto) {
        this.id = dto.getId();
        this.orderProductId = dto.getOrderProductId();
        this.unitId = dto.getUnitId();
        this.qty = dto.getQty();
        this.price = dto.getPrice();
        this.vatRate = dto.getVatRate();
        this.vatPrice = vatRate != null ? price
            .multiply(vatRate)
            .divide(BigDecimal.valueOf(100).add(vatRate), 2, RoundingMode.HALF_UP) : null;
        this.priceWithoutVat = vatRate != null ? price
            .multiply(BigDecimal.valueOf(100))
            .divide(BigDecimal.valueOf(100).add(dto.getVatRate()), 2, RoundingMode.HALF_UP) : price;
        this.totalPrice = qty.multiply(price);
        this.totalPriceWithoutVat = vatRate != null ? totalPrice
            .multiply(BigDecimal.valueOf(100))
            .divide(BigDecimal.valueOf(100).add(dto.getVatRate()), 2, RoundingMode.HALF_UP) : totalPrice;
        this.totalVatPrice = vatRate != null ? totalPrice
            .multiply(vatRate)
            .divide(BigDecimal.valueOf(100).add(vatRate), 2, RoundingMode.HALF_UP) : null;
    }
}
