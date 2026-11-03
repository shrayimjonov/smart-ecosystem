package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.aic.Aic;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.BaseFilter;

import java.util.List;

@Repository
public interface AicRepositoryCustom {

    ResultList<Aic> getResultList(BaseFilter filter);

    List<Aic> lookUp(BaseFilter filter);
}
