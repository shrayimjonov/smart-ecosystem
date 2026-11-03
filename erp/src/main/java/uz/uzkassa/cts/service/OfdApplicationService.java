package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.OfdApplication;
import uz.uzkassa.cts.dto.ofdapplication.OfdApplicationCreateDTO;
import uz.uzkassa.cts.dto.ofdapplication.OfdApplicationMergeDTO;
import uz.uzkassa.cts.dto.ofdapplication.SwapDeviceDTO;
import uz.uzkassa.cts.dto.ofdapplication.SwapFiscalModuleDTO;
import uz.uzkassa.cts.dto.ofdapplication.admin.OfdApplicationListAdminDTO;
import uz.uzkassa.cts.dto.ofdapplication.cabinet.OfdApplicationListCabinetDTO;
import uz.uzkassa.cts.dto.payload.OfdApplicationPayload;
import uz.uzkassa.cts.dto.payload.OfdApplicationSendPayload;
import uz.uzkassa.cts.dto.payload.Payload;
import uz.uzkassa.cts.dto.tin.integration.OfdApplicationListDTO;
import uz.uzkassa.cts.dto.tin.integration.OfdApplicationMakeObjectListDTO;
import uz.uzkassa.cts.enums.ApplicationType;
import uz.uzkassa.cts.enums.OfdStatus;
import uz.uzkassa.cts.filter.OfdApplicationFilter;

import java.util.List;

public interface OfdApplicationService extends BaseService<OfdApplication, Long> {

    void create(OfdApplicationCreateDTO dto);

    Page<OfdApplicationListAdminDTO> getListForAdmin(OfdApplicationFilter filter);

    Page<OfdApplicationListCabinetDTO> getListForCabinet(OfdApplicationFilter filter);

    void sync();

    void sync(Long customerId);

    void syncApplication(OfdApplicationPayload ofdApplicationPayload);

    void batchSyncApplication(Payload payload);

    void sendApplication(OfdApplicationSendPayload ofdApplicationSendPayload);

    Page<OfdApplicationMakeObjectListDTO> getApplications(OfdApplicationFilter filter);

    List<OfdApplicationListDTO> getListByFiscalizationId(Long fiscalizationId);

    OfdApplication reject(MultipartFile file, Long id);

    Long swapFiscalModule(SwapFiscalModuleDTO swapFiscalModuleDTO);

    String swapDevice(SwapDeviceDTO swapDeviceDTO);

    String changeAddress(Long id);

    void merge(OfdApplicationMergeDTO ofdApplicationMergeDTO);

    void deleteById(Long id);

    void changeApplicationType(Long id, ApplicationType applicationType);

    void updateStatus(Long id, OfdStatus status);

    void rejectWithRecalculation(Long id, MultipartFile file);
}
