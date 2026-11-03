package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.company.RevenueByRegionDTO;
import uz.uzkassa.smartpossupply.dto.company.TopClientDTO;
import uz.uzkassa.smartpossupply.dto.customer.CustomerStatisticDTO;
import uz.uzkassa.smartpossupply.dto.customer.StatisticsByCustomerDTO;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.filters.CompanyFilter;
import uz.uzkassa.smartpossupply.filters.SimpleFilter;
import uz.uzkassa.smartpossupply.filters.UserFilter;

import java.util.List;

@Repository
public interface CompanyRepositoryCustom {
    ResultList<Company> getResultList(CompanyFilter filter);

    CustomerStatisticDTO getCustomerStatistics(BaseFilter filter);

    StatisticsByCustomerDTO getStatisticsByCustomer(Long id);

    List<TopClientDTO> getTopClients(BaseFilter filter);

    List<RevenueByRegionDTO> getRevenueByRegion();

    ResultList<String> getAllToSync(SimpleFilter filter);

    List<String> getCompanyTinsByManager(UserFilter filter);
}
