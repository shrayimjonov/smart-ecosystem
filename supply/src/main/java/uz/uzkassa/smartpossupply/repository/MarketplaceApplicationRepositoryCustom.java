package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.MarketplaceApplication;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.marketplaceapplication.MarketplaceApplicationStatisticDTO;
import uz.uzkassa.smartpossupply.filters.BaseFilter;

@Repository
public interface MarketplaceApplicationRepositoryCustom {
    ResultList<MarketplaceApplication> getResultList(BaseFilter filter);

    MarketplaceApplicationStatisticDTO getStatistics(BaseFilter filter);
}
