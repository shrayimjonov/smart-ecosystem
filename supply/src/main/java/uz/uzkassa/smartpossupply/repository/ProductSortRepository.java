package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.product.ProductSort;
import uz.uzkassa.smartpossupply.enums.EnvironmentType;
import uz.uzkassa.smartpossupply.enums.SortOrderType;

import java.util.List;

@Repository
public interface ProductSortRepository extends BaseRepository<ProductSort, Long> {

    void deleteAllByProductIdInAndEnvironmentTypeAndSortOrderType(List<Long> productIds, EnvironmentType environmentType, SortOrderType sortOrderType);

    @EntityGraph(attributePaths = {"product.categories"})
    List<ProductSort> findAllByProduct_CompanyIdAndProduct_BranchIdAndEnvironmentTypeAndSortOrderType(Long companyId, Long branchId, EnvironmentType environmentType, SortOrderType sortOrderType);
}
