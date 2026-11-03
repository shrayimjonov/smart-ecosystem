package uz.uzkassa.cts.service;

import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.ActOfTransmission;
import uz.uzkassa.cts.dto.actoftransmission.response.ActOfTransmissionFullResponseDto;
import uz.uzkassa.cts.dto.actoftransmission.response.ActOfTransmissionResponseDto;
import uz.uzkassa.cts.dto.contractforleasing.request.GeneratePoaTemplateDTO;

import java.time.LocalDate;
import java.util.List;

public interface ActOfTransmissionStrategyService extends BaseService<ActOfTransmission, Long> {
    List<ActOfTransmissionResponseDto> getByContractId(Long id);

    String getHtmlTemplate(Long id);

    String getScheduleHtmlTemplate(Long id);

    ActOfTransmissionFullResponseDto uploadFile(Long id, MultipartFile file);

    ActOfTransmissionFullResponseDto accept(Long id, LocalDate date);

    ActOfTransmissionFullResponseDto changeToAttach(Long id);

    ActOfTransmissionFullResponseDto defect(Long id);

    ActOfTransmissionFullResponseDto uploadScheduleFile(Long id, MultipartFile file);

    ActOfTransmissionFullResponseDto uploadPowerAttorneyFile(Long id, MultipartFile file);

//    List<ActOfTransmission> findForInvoiceCreate();

    String generatePoaTemplate(GeneratePoaTemplateDTO dto);
}
