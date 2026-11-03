package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.ActOfTransmission;
import uz.uzkassa.cts.dto.actoftransmission.response.ActChangeDocFlowRequestDto;
import uz.uzkassa.cts.dto.actoftransmission.response.ActOfTransmissionFullResponseDto;
import uz.uzkassa.cts.dto.actoftransmission.response.ActOfTransmissionResponseDto;
import uz.uzkassa.cts.dto.actoftransmission.smartpos.ActOfTransmissionSmartposDTO;
import uz.uzkassa.cts.dto.actoftransmission.smartpos.ActPaymentScheduleSmartposDTO;
import uz.uzkassa.cts.dto.contractforleasing.request.GeneratePoaTemplateDTO;
import uz.uzkassa.cts.filter.ActOfTransmissionFilter;
import uz.uzkassa.cts.filter.BaseFilter;

import java.time.LocalDate;
import java.util.List;

public interface ActOfTransmissionFactoryService extends BaseService<ActOfTransmission, Long> {
    List<ActOfTransmissionResponseDto> getByContractId(Long id);

    Page<ActOfTransmissionResponseDto> getList(ActOfTransmissionFilter filter);

    Page<ActOfTransmissionSmartposDTO> getPrimeList(BaseFilter filter);

    List<ActPaymentScheduleSmartposDTO> getScheduleList(Long actId);

    ActOfTransmissionFullResponseDto getById(Long id);

    ActOfTransmission getByDeviceId(Long deviceId);

    ActOfTransmission getByDeviceIdAndCustomerId(Long deviceId, Long customerId);

    String getHtmlTemplate(Long id);

    String getScheduleHtmlTemplate(Long id);

    ActOfTransmissionFullResponseDto uploadFile(Long id, MultipartFile file);

    ActOfTransmissionFullResponseDto accept(Long id, LocalDate date);

    ActOfTransmissionFullResponseDto changeToAttach(Long id);

    ActOfTransmissionFullResponseDto defect(Long id);

    ActOfTransmissionFullResponseDto uploadScheduleFile(Long id, MultipartFile file);

    ActOfTransmissionFullResponseDto uploadPowerAttorneyFile(Long id, MultipartFile file);

    List<ActOfTransmission> findForInvoiceCreate();

    String generatePoaTemplate(GeneratePoaTemplateDTO dto);

    ActOfTransmissionFullResponseDto changeDocFlowStatus(ActChangeDocFlowRequestDto dto);
}
