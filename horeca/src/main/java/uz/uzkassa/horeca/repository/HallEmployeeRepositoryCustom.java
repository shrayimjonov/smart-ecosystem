package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.hall.HallEmployee;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.filters.BaseFilter;

public interface HallEmployeeRepositoryCustom {

    ResultList<HallEmployee> getResultList(BaseFilter filter);
}
