package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.Reference;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.ReferenceFilter;

@Repository
public interface ReferenceRepositoryCustom {

    ResultList<Reference> getResultList(ReferenceFilter filter);
}
