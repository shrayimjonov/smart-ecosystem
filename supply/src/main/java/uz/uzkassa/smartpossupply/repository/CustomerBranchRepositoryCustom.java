package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.customerbranch.CustomerBranch;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.CustomerBranchFilter;

import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 19.01.2023 15:08
 */
@Repository
public interface CustomerBranchRepositoryCustom {

    ResultList<CustomerBranch> getResultList(CustomerBranchFilter filter);

    List<CustomerBranch> lookUp(CustomerBranchFilter filter);
}
