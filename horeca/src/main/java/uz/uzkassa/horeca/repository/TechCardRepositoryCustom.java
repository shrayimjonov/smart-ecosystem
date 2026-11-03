package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.techcard.TechCard;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.filters.BaseFilter;

public interface TechCardRepositoryCustom {
    ResultList<TechCard> getResultList(BaseFilter filter);
}
