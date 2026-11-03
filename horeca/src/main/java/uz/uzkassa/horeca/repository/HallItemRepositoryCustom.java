package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.hall.HallItem;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.dto.hallitem.BranchFeeStatisticsDTO;
import uz.uzkassa.horeca.dto.hallitem.HallItemTableStatisticsDTO;
import uz.uzkassa.horeca.filters.HallItemFilter;

import java.util.List;

public interface HallItemRepositoryCustom {
    ResultList<HallItem> getResultList(HallItemFilter filter);

    List<HallItemTableStatisticsDTO> getTableStatistics(HallItemFilter filter);

    List<BranchFeeStatisticsDTO> getFeeByBranch(Long companyId);
}
