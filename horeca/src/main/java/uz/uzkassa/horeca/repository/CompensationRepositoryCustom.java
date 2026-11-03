package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.Compensation;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.filters.BaseFilter;

public interface CompensationRepositoryCustom {
    ResultList<Compensation> getResultList(BaseFilter filter);
}
