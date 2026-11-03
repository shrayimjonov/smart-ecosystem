package uz.uzkassa.smartpossupply.dto.order.hippo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderProductHippoDTO implements Serializable {

    String catalogCode;

    String catalogName;

    String barcode;

    String name;

    String packageCode;

    String packageName;

    BigDecimal count;

    BigDecimal summa;

    BigDecimal deliverySum;

    BigDecimal vatRate;

    BigDecimal vatSum;

    BigDecimal deliverySumWithVat;
}
