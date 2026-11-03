package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.Approver;
import uz.uzkassa.cts.domain.Template;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.domain.hrms.VacancyApplication;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.dto.base.LocalizedMessageDTO;
import uz.uzkassa.cts.dto.template.TemplateDTO;
import uz.uzkassa.cts.dto.template.admin.TemplateDetailAdminDTO;
import uz.uzkassa.cts.dto.template.admin.TemplateListAdminDTO;
import uz.uzkassa.cts.enums.ApproverStatus;
import uz.uzkassa.cts.enums.ChannelType;
import uz.uzkassa.cts.enums.Language;
import uz.uzkassa.cts.enums.TemplateType;
import uz.uzkassa.cts.filter.TemplateFilter;

import java.util.List;

public interface TemplateService extends BaseService<Template, Long> {

    Long create(TemplateDTO templateDTO);

    Long update(Long id, TemplateDTO templateDTO);

    Page<TemplateListAdminDTO> getListForAdmin(TemplateFilter filter);

    TemplateDetailAdminDTO getForAdmin(Long id);

    List<CommonCodeDTO> lookUp(TemplateFilter filter);

    String getActivationMessage(String activationCode, ChannelType channelType, TemplateType templateType);

    String getChangePasswordMessage(User user, String activationCode, ChannelType channelType);

    String getChangeUsernameMessage(User user, String activationCode, ChannelType channelType);

    LocalizedMessageDTO getBirthdayGreeting(User user);

    String getLeadMessage(Long templateId, Language language);

    String getSubject(TemplateType templateType);

    String getVacanyApplicationCreateMessage(TemplateType templateType, VacancyApplication vacancyApplication);

    String getVacanyApplicationRejectMessage(TemplateType templateType, VacancyApplication vacancyApplication);

    String getVacanyApplicationAcceptedMessage(TemplateType templateType, VacancyApplication vacancyApplication);

    LocalizedMessageDTO getApprovalProcessMessageForCreator(Approver approver, ApproverStatus status);

    LocalizedMessageDTO getApprovalProcessMessageForApprover(Approver approver, User user);
}
