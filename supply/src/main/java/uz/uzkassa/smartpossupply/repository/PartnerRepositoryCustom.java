package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.company.Partners;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.kpi.KPICalculateDTO;
import uz.uzkassa.smartpossupply.dto.kpi.KPICalculateTotalDTO;
import uz.uzkassa.smartpossupply.dto.stock.ActiveCustomerBaseDTO;
import uz.uzkassa.smartpossupply.filters.ActiveCustomerFilter;
import uz.uzkassa.smartpossupply.filters.CompanyFilter;
import uz.uzkassa.smartpossupply.filters.KPIFilter;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 13.07.2023
 */
@Repository
public interface PartnerRepositoryCustom {
    ResultList<Partners> getResultList(CompanyFilter filter, boolean isSupplier);

    ResultList<KPICalculateDTO> calculateANewClients(KPIFilter kpiFilter);

    KPICalculateTotalDTO calculateTotal(KPIFilter kpiFilter);

    ResultList<ActiveCustomerBaseDTO> getNotActiveCustomers(ActiveCustomerFilter filter);
}
