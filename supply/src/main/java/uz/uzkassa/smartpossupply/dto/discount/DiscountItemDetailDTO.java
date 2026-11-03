package uz.uzkassa.smartpossupply.dto.discount;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductCommonDTO;
import uz.uzkassa.smartpossupply.enums.DiscountRangeType;
import uz.uzkassa.smartpossupply.enums.DiscountType;

import java.io.Serializable;
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
public class DiscountItemDetailDTO implements Serializable {

    Long discountItemId;

    DiscountType discountType;

    DiscountRangeType rangeType;

    boolean applyAllCustomers;

    boolean applyAllProducts;

    ProductCommonDTO product;

    CompanyBaseDTO customer;

    List<DiscountRangeDetailDTO>ranges=new ArrayList<>();

    public void addRanges(DiscountRangeDetailDTO rangeDetailDTO) {
        ranges.add(rangeDetailDTO);
    }
}