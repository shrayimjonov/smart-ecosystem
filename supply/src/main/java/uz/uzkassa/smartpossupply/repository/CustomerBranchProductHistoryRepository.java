package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.customerbranch.CustomerBranchProductHistory;


@Repository
public interface CustomerBranchProductHistoryRepository extends BaseRepository<CustomerBranchProductHistory, Long>,CustomerBranchProductHistoryRepositoryCustom {

}
