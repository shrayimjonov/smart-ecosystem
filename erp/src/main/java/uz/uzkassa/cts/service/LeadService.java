package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.Lead;
import uz.uzkassa.cts.dto.lead.*;
import uz.uzkassa.cts.dto.lead.admin.LeadDetailAdminDTO;
import uz.uzkassa.cts.dto.lead.admin.LeadListAdminDTO;
import uz.uzkassa.cts.enums.CallStatus;
import uz.uzkassa.cts.enums.Language;
import uz.uzkassa.cts.enums.LeadState;
import uz.uzkassa.cts.enums.LeadStatus;
import uz.uzkassa.cts.filter.LeadFilter;

public interface LeadService extends BaseService<Lead, Long> {

    Long create(LeadDTO leadDTO);

    Long update(Long id, LeadDTO leadDTO);

    Page<LeadListAdminDTO> getList(LeadFilter filter);

    LeadResponseDTO getListSmartpos(String tin);

    LeadDetailAdminDTO get(Long id);

    void updateCallStatus(Long id, CallStatus callStatus);

    void updateState(Long id, LeadState status);

    void updateStatus(Long id, LeadStatus leadStatus);

    void sendSms(LeadSmsDTO leadSmsDTO);

    void createFromTelegramBot(LeadRequestDTO requestDTO);

    Long createFromWebSites(LeadRequestDTO hamkorSmartposDTO, String source);

    Long createFromMeta(LeadMetaDTO leadMetaDTO);

    Long createFromSmartpos(LeadSmartposDTO leadSmartposDTO, String source);

    String generateMessage(Long templateId, Language language);
}
