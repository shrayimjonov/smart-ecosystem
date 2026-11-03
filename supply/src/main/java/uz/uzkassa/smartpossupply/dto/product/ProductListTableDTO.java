package uz.uzkassa.smartpossupply.dto.product;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.PaymentType;

import java.io.Serializable;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 13.01.2025
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductListTableDTO implements Serializable {

    CommonDTO discount;

    ProductListDTO product;

    PaymentType paymentType;
}
