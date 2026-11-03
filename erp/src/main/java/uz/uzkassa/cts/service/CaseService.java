package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.cases.Cases;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.cases.*;
import uz.uzkassa.cts.dto.cases.request.CaseAcceptDto;
import uz.uzkassa.cts.dto.payload.CaseBotPayload;
import uz.uzkassa.cts.enums.CaseType;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.CaseFilter;

import java.util.List;

public interface CaseService extends BaseService<Cases, Long> {

    CaseDTO create(CaseDTO caseDTO);

    Page<CaseDetailDTO> list(CaseFilter filter);

    CaseUpdateDTO update(CaseUpdateDTO caseUpdateDTO);

    void assign(CaseUpdateAssigneeDTO caseUpdateAssigneeDTO);

    CaseDetailDTO get(Long id);

    void updateStatus(CaseUpdateStatusDTO caseUpdateStatusDTO);

    Page<CommonDTO> getAssigneeLookUp(BaseFilter filter);

    CaseDetailDTO accept(CaseAcceptDto acceptDto);

    List<String> generateTemplate(Long caseId);

    Object getFieldsByType(CaseType type);

    CaseDTO update(CaseDTO caseDTO);

    String getDirectorFullName(Long customerId);

    String checkPhoneExistence(String phoneNumber);

    Boolean checkPhoneExists(String phoneNumber);

    String getCustomerByPhone(Long customerId);

    byte[] download(CaseFilter filter);

    void sendToTelegramBot(CaseBotPayload caseBotPayload);
}
