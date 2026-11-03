package uz.uzkassa.smartpossupply.dto.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductListTableDTO;
import uz.uzkassa.smartpossupply.enums.PaymentType;
import uz.uzkassa.smartpossupply.enums.ProductListType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 04.11.2024
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductExpeditorReportDTO {

    Map<CommonDTO, Map<ProductListTableDTO, BigDecimal>> productExpeditorReports = new HashMap<>();

    ProductListType productListType;


    @JsonIgnore
    public void add(CommonDTO entity, ProductListTableDTO product, BigDecimal qty) {
        if (productExpeditorReports.containsKey(entity)) {
            if (productExpeditorReports.get(entity).containsKey(product)) {
                productExpeditorReports.get(entity).put(product, productExpeditorReports.get(entity).get(product).add(qty));
            } else {
                productExpeditorReports.get(entity).put(product, qty);
            }
        } else {
            Map<ProductListTableDTO, BigDecimal> map = new HashMap<>();
            map.put(product, qty);
            productExpeditorReports.put(entity, map);
        }
    }

    @JsonIgnore
    public CommonDTO getExpeditor(Long expeditorId) {
        return productExpeditorReports.keySet().stream().filter(commonDTO -> commonDTO != null && commonDTO.getId().equals(expeditorId)).findFirst().orElse(null);
    }

    @JsonIgnore
    public CommonDTO getCustomerBranch(Long customerBranchId) {
        return productExpeditorReports.keySet().stream().filter(commonDTO -> commonDTO != null && commonDTO.getId().equals(customerBranchId)).findFirst().orElse(null);
    }

    @JsonIgnore
    public ProductListTableDTO getProduct(Long expeditorId, Long productId) {
        Map<ProductListTableDTO, BigDecimal> map = productExpeditorReports.get(getExpeditor(expeditorId));
        if (!CollectionUtils.isEmpty(map)) {
            ProductListTableDTO productListTableDTO = null;
            for (ProductListTableDTO productDTO : map.keySet()) {
                if (productDTO.getProduct().getId().equals(productId) && productDTO.getDiscount() == null) {
                    productListTableDTO = productDTO;
                    break;
                } else if (productDTO.getProduct().getId().equals(productId)) {
                    productListTableDTO = productDTO;
                }
            }
            return productListTableDTO;
        }
        return null;
    }

}
