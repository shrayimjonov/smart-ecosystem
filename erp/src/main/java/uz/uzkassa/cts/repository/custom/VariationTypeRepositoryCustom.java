package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.inventory.VariationType;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.BaseFilter;

@Repository
public interface VariationTypeRepositoryCustom {

    ResultList<VariationType> getResultList(BaseFilter filter);
}
