package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.OneTimeOrder;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.serviceorder.ServiceOrderListDTO;
import uz.uzkassa.cts.filter.EmployeeReportFilter;
import uz.uzkassa.cts.filter.OneTimeOrderFilter;

import java.math.BigDecimal;

@Repository
public interface OneTimeOrderRepositoryCustom {

    ResultList<ServiceOrderListDTO> getListByEmployee(EmployeeReportFilter filter);

    BigDecimal totalBonusListByEmployee(EmployeeReportFilter filter);

    ResultList<OneTimeOrder> getResultList(OneTimeOrderFilter filter);
}
