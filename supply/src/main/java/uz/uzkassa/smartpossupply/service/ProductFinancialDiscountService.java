package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.constants.Constants;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.dto.product.ProductLookUpDTO;
import uz.uzkassa.smartpossupply.enums.ProductGroup;
import uz.uzkassa.smartpossupply.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class ProductFinancialDiscountService extends BaseService implements Constants {

    ProductRepository productRepository;

    public List<ProductLookUpDTO> getFinancialDiscountProduct(ProductGroup productGroup) {
        List<Product> products;
        if (!ProductGroup.Pharma.equals(productGroup)) {
            products = productRepository.findAllByDiscountTrueAndDeletedIsFalse()
                    .stream().filter(product -> !ProductGroup.Pharma.equals(product.getProductType().getProductGroup()))
                    .toList();
        } else {
            products = productRepository.findAllByDiscountTrueAndDeletedIsFalse();
        }
        return products
                .stream().map(product -> {
                    ProductLookUpDTO productLookUpDTO = new ProductLookUpDTO();
                    productLookUpDTO.setId(product.getId());
                    productLookUpDTO.setName(product.getName());
                    productLookUpDTO.setVat(product.getVat());
                    productLookUpDTO.setUnit(product.getUnit().toDTO());
                    productLookUpDTO.setPrice(BigDecimal.ZERO);
                    productLookUpDTO.setVatBarcode(product.getVatBarcode());
                    productLookUpDTO.setCatalogName(product.getCatalogName());
                    productLookUpDTO.setPackageName(product.getPackageName());
                    productLookUpDTO.setPackageCode(product.getPackageCode());
                    productLookUpDTO.setDispenseType(product.getDispenseType());
                    return productLookUpDTO;
                }).toList();
    }

    public List<ProductLookUpDTO> getFinancialDiscountProduct() {
        return this.getFinancialDiscountProduct(getCurrentCompany().getProductGroup());
    }
}
