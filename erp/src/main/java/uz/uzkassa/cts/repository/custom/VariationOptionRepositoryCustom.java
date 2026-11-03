package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.inventory.VariationOption;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.VariationOptionFilter;

@Repository
public interface VariationOptionRepositoryCustom {

    ResultList<VariationOption> getResultList(VariationOptionFilter filter);
}
