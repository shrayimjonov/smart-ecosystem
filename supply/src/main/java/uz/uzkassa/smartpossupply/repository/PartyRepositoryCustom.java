package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.party.Party;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.BaseFilter;

@Repository
public interface PartyRepositoryCustom {
    ResultList<Party> getResultList(BaseFilter filter);
}
