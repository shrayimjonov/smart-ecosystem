package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.mark.Mark;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.mark.MarkStatisticsDTO;
import uz.uzkassa.smartpossupply.filters.MarkFilter;

@Repository
public interface MarkRepositoryCustom {
    ResultList<Mark> getResultList(MarkFilter filter);

    MarkStatisticsDTO getStatistics(MarkFilter filter);
}
