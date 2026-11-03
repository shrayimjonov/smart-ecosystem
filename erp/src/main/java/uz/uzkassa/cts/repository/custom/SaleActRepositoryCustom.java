package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.sale.act.SaleActResponseDTO;
import uz.uzkassa.cts.filter.SaleActFilter;


@Repository
public interface SaleActRepositoryCustom {

    ResultList<SaleActResponseDTO> getResultList(SaleActFilter filter);
}
