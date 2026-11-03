package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.category.Category;
import uz.uzkassa.smartpossupply.domain.product.ProductSort;
import uz.uzkassa.smartpossupply.dto.product.ProductSortDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductSortItemDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductSortOfflineDTO;
import uz.uzkassa.smartpossupply.enums.EnvironmentType;
import uz.uzkassa.smartpossupply.enums.SortOrderType;
import uz.uzkassa.smartpossupply.repository.ProductSortRepository;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProductSortService extends BaseService {

    ProductSortRepository productSortRepository;

    @Transactional
    public void create(ProductSortDTO productSortDTO) {
        productSortRepository.deleteAllByProductIdInAndEnvironmentTypeAndSortOrderType(productSortDTO.getItems().stream().map(ProductSortItemDTO::getProductId).toList(), productSortDTO.getEnvironmentType(), productSortDTO.getSortOrderType());
        List<ProductSort> productSortList = productSortDTO.getItems().stream().map(productSortItemDTO -> {
            ProductSort productSort = new ProductSort();
            productSort.setEnvironmentType(productSortDTO.getEnvironmentType());
            productSort.setSortOrderType(productSortDTO.getSortOrderType());
            productSort.setProductId(productSortItemDTO.getProductId());
            productSort.setSortOrder(productSortItemDTO.getSortOrder());
            return productSort;
        }).toList();
        productSortRepository.saveAll(productSortList);
    }

    public List<ProductSortOfflineDTO> getListForOffline(Long companyId, Long branchId, String sortOrderType) {
        return productSortRepository.findAllByProduct_CompanyIdAndProduct_BranchIdAndEnvironmentTypeAndSortOrderType(companyId, branchId, EnvironmentType.WEB, SortOrderType.valueOf(sortOrderType)).stream().map(productSort -> {
            ProductSortOfflineDTO productSortOfflineDTO = new ProductSortOfflineDTO();
            productSortOfflineDTO.setProductId(productSort.getProductId());
            productSortOfflineDTO.setSortOrder(productSort.getSortOrder());
            productSortOfflineDTO.setCategoryId(productSort.getProduct().getCategories().stream()
                    .filter(c -> c.getEnvironmentType() == EnvironmentType.WEB)
                    .map(Category::getId)
                    .findFirst()
                    .orElse(null)
            );
            return productSortOfflineDTO;
        }).toList();
    }
}