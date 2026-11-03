package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.hall.Hall;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.filters.BaseFilter;

public interface HallRepositoryCustom {
    ResultList<Hall> getResultList(BaseFilter filter);
}
