package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.Lgota;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.BaseFilter;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 21.07.2023
 */
@Repository
public interface LgotaRepositoryCustom {
    ResultList<Lgota> getResultList(BaseFilter filter);
}
