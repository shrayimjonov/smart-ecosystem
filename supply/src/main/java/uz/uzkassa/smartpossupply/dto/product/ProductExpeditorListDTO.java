package uz.uzkassa.smartpossupply.dto.product;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.PaymentType;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductExpeditorListDTO implements Serializable {

    ProductListDTO product;

    CommonDTO expeditor;

    CommonDTO discount;

    CommonDTO customerBranch;

    BigDecimal qty;

    BigDecimal totalPrice;

    PaymentType paymentType;
}
