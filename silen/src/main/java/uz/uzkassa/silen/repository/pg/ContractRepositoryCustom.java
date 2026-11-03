package uz.uzkassa.silen.repository.pg;

import org.springframework.data.domain.Page;
import uz.uzkassa.silen.domain.Contract;
import uz.uzkassa.silen.dto.filter.ContractFilter;

import java.io.Serializable;

public interface ContractRepositoryCustom extends Serializable {
    Page<Contract> findAllByFilter(ContractFilter filter);
}
