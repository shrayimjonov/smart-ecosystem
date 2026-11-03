package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.mark.MarkOrderProduct;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.BaseFilter;

@Repository
public interface MarkOrderProductRepositoryCustom {
    ResultList<MarkOrderProduct> getResultList(BaseFilter filter);
}
