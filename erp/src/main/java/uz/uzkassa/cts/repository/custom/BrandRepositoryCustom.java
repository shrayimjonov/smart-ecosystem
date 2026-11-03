package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.inventory.Brand;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.BaseFilter;

@Repository
public interface BrandRepositoryCustom {

    ResultList<Brand> getResultList(BaseFilter filter);
}
