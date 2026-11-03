package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.dto.subscriptionreport.SubscriptionReportServiceDTO;
import uz.uzkassa.cts.filter.SubscriptionReportFilter;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface SubscriptionReportRepositoryCustom {

    Map<String, HashMap<Long, BigInteger>> getTotalsByEmployees(SubscriptionReportFilter filter);

    List<SubscriptionReportServiceDTO> getBonusesByEmployeesList(SubscriptionReportFilter filter);
}
