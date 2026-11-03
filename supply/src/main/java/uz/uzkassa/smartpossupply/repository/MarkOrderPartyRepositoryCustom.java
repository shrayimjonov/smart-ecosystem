package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.mark.MarkOrderParty;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.MarkOrderPartyFilter;

@Repository
public interface MarkOrderPartyRepositoryCustom {
    ResultList<MarkOrderParty> getResultList(MarkOrderPartyFilter filter);
}
