package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.sale.contract.SaleContractResponseDTO;
import uz.uzkassa.cts.filter.SaleContractFilter;


@Repository
public interface SaleContractRepositoryCustom {

    ResultList<SaleContractResponseDTO> getResultList(SaleContractFilter filter);
}
