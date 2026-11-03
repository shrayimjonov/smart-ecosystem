package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.Branch;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.branch.CommonStatisticsDTO;
import uz.uzkassa.smartpossupply.filters.BaseFilter;

import java.util.List;

@Repository
public interface BranchRepositoryCustom {
    ResultList<Branch> getResultList(BaseFilter filter);

    List<Branch> lookUp(BaseFilter filter);

    CommonStatisticsDTO getStatistics(BaseFilter filter);
}
