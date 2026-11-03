package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.customerbranch.CustomerBranchProductHistory;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.CustomerBranchProductHistoryFilter;


@Repository
public interface CustomerBranchProductHistoryRepositoryCustom {
    ResultList<CustomerBranchProductHistory> getResultList(CustomerBranchProductHistoryFilter filter);
}
