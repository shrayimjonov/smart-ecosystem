package uz.uzkassa.silen.dto.waybill;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.domain.Product;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class WaybillProductsDTO {

    String id;

    String productId;

    Product product;

    BigDecimal amount;

    BigDecimal price;

    BigDecimal deliverySum;

    BigDecimal weightBrutto;

    BigDecimal weightNetto;

    private String packageCode;

    private String packageName;

    private String catalogCode;

    private String catalogName;

}
