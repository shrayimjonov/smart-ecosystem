package uz.uzkassa.smartpossupply.dto.order;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PROTECTED)
public class OrderProductBaseDTO implements Serializable {

    BigDecimal qty;

    BigDecimal price;//price with NDS for 1x

    BigDecimal foreignPrice;//price with NDS for 1x

    BigDecimal priceWithoutVat;//price without NDS for 1x

    BigDecimal totalPrice; //qty * price. Total price with NDS

    BigDecimal totalForeignPrice; //qty * price. Total price with NDS

    BigDecimal totalPriceWithoutVat; //qty * priceWithoutVat. Total price without NDS

    BigDecimal vatRate; //12%, 15%, null

    BigDecimal vatPrice; //NDS for 1x

    BigDecimal totalVatPrice;//qty * vatPrice

    BigDecimal priceListPrice;

    BigDecimal exciseRate;

    BigDecimal exciseSum;
}