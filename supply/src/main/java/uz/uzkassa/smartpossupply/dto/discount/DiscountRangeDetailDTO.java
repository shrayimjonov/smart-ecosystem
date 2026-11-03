package uz.uzkassa.smartpossupply.dto.discount;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 16.12.2024
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiscountRangeDetailDTO implements Serializable {

    Long id;

    BigDecimal fixedAmount;

    BigDecimal fromAmount;

    BigDecimal toAmount;

    List<DiscountItemProductDetailDTO> itemProducts = new ArrayList<>();

    public void addItemProduct(DiscountItemProductDetailDTO itemProductDetailDTO) {
        itemProducts.add(itemProductDetailDTO);
    }
}