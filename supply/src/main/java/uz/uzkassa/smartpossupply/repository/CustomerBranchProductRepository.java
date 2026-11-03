package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.customerbranch.CustomerBranchProduct;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 30.11.2023
 */
@Repository
public interface CustomerBranchProductRepository extends BaseRepository<CustomerBranchProduct, Long>, CustomerBranchProductRepositoryCustom {

    List<CustomerBranchProduct> findAllByPlanRouteCustomerBranchIdAndCreatedDateBetween(Long planRouteCustomerBranchId, LocalDateTime createdDate, LocalDateTime createdDate2);

}
