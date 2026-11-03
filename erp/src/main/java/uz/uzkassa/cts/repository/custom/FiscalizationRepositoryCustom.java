package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.Fiscalization;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.device.DeviceFiscalModuleDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.dto.smartpos.SmartposFiscalizationListDTO;
import uz.uzkassa.cts.filter.BaseFilter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Repository
public interface FiscalizationRepositoryCustom {
    ResultList<SmartposFiscalizationListDTO> getResultListDTO(BaseFilter filter);

    ResultList<Fiscalization> getResultList(BaseFilter filter);

    List<DeviceStatisticsAdminDTO> getStatisticsForAdmin(BaseFilter filter);

    List<DeviceFiscalModuleDTO> getLookupForOfd(BaseFilter filter);

    ArrayList<BigInteger> getListToSendOfd();

    Long getActiveFiscalizationCount(Long customerId);
}
