package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.apay.APayCategory;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.ApayCategoryFilter;

@Repository
public interface ApayCategoryRepositoryCustom {

    ResultList<APayCategory> getResultList(ApayCategoryFilter filter);
}
