package uz.uzkassa.smartpossupply.repository;

import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.expeditor.ExpeditorListDTO;
import uz.uzkassa.smartpossupply.dto.expeditor.ExpeditorStatisticsDTO;
import uz.uzkassa.smartpossupply.filters.ExpeditorFilter;

public interface ExpeditorRepositoryCustom {

    ResultList<ExpeditorListDTO> getResultList(ExpeditorFilter filter);

    ExpeditorStatisticsDTO getStatistics(ExpeditorFilter filter);
}
