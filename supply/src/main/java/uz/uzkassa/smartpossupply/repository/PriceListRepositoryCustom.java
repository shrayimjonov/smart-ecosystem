package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.pricelist.PriceList;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.PriceListFilter;

import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 14.02.2023 13:45
 */
@Repository
public interface PriceListRepositoryCustom {
    ResultList<PriceList> getResultList(PriceListFilter filter);
    List<PriceList> lookUp(PriceListFilter filter);
}
