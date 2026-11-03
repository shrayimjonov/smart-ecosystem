package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.factura.Contract;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.BaseFilter;

@Repository
public interface ContractRepositoryCustom {
    ResultList<Contract> getResultList(BaseFilter filter);
}
