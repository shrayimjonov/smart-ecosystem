package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.customerbranch.CustomerBranchProduct;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.CustomerBranchFilter;
import uz.uzkassa.smartpossupply.filters.CustomerBranchProductHistoryFilter;

import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 30.11.2023
 */
@Repository
public interface CustomerBranchProductRepositoryCustom {
    List<CustomerBranchProduct> getResultList(CustomerBranchFilter filter);

    ResultList<CustomerBranchProduct> getResultPageableList(CustomerBranchProductHistoryFilter filter);
}
