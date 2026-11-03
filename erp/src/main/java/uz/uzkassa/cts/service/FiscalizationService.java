package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.Fiscalization;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.dto.fiscalization.FiscalizationListAdminDTO;
import uz.uzkassa.cts.dto.ofdapplication.OfdApplicationMergeDTO;
import uz.uzkassa.cts.dto.applicationfile.ApplicationTemplateDTO;
import uz.uzkassa.cts.dto.device.DeviceFiscalModuleDTO;
import uz.uzkassa.cts.dto.fm.*;
import uz.uzkassa.cts.dto.smartpos.SmartposFiscalizationListDTO;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.filter.BaseFilter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public interface FiscalizationService extends BaseService<Fiscalization, Long> {

    Page<FiscalizationListAdminDTO> getListForAdmin(BaseFilter filter);

    List<DeviceStatisticsAdminDTO> getStatisticsForAdmin(BaseFilter filter);

    Long fiscalization(FiscalizationDTO fiscalizationDTO);

    Page<SmartposFiscalizationListDTO> list(BaseFilter filter);

    FiscalizationDetailDTO getDetail(FiscalCheckDTO fiscalCheckDTO);

    FiscalizationCheckDeviceDTO fiscalizationCheck(FiscalCheckDTO fiscalCheckDTO);

    List<DeviceFiscalModuleDTO> getLookupForOfd(BaseFilter filter);

    ArrayList<BigInteger> getListToSendOfd();

    void swapDevice(SwapDeviceDTO swapDeviceDTO);

    void maskAsPaid(Long id);

    void mergeWithNIC(OfdApplicationMergeDTO ofdApplicationMergeDTO);

    void swapFiscalModule(FiscalSwapFiscalModuleDTO fiscalSwapFiscalModuleDTO);

    void syncWithSmartpos(FiscalizationSyncDTO fiscalizationSyncDTO);

    void syncAllWithSmartpos();

    String downloadApplicationTemplate(ApplicationTemplateDTO applicationTemplateDTO);

    void clearFiscalCache(FiscalClearCacheDTO fiscalClearCacheDTO);

    void updateStatus(Long id, DeviceStatus status);

    Fiscalization findFirstByDeviceIdOrderByIdDesc(Long deviceId);

    void clearMapByHash(String hash);
}
