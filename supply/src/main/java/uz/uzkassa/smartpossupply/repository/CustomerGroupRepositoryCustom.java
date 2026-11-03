package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.CustomerGroup;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.BaseFilter;

import java.util.List;

@Repository
public interface CustomerGroupRepositoryCustom {

    ResultList<CustomerGroup> getResultList(BaseFilter filter);

    List<CustomerGroup> lookUp(BaseFilter filter);

}
