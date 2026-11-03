package uz.uzkassa.cts.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.domain.Lead;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.lead.*;
import uz.uzkassa.cts.dto.lead.admin.LeadDetailAdminDTO;
import uz.uzkassa.cts.dto.lead.admin.LeadListAdminDTO;
import uz.uzkassa.cts.dto.note.NoteDTO;
import uz.uzkassa.cts.dto.sms.EtcRequestDTO;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.filter.LeadFilter;
import uz.uzkassa.cts.integrations.ErpMarketingClient;
import uz.uzkassa.cts.repository.LeadRepository;
import uz.uzkassa.cts.service.*;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class LeadServiceImpl extends BaseServiceImpl<Lead, Long> implements LeadService {

    private final LeadRepository leadRepository;
    private final SocialAddressService socialAddressService;
    private final SmsService smsService;
    private final NoteService noteService;
    private final CustomerService customerService;
    private final TemplateService templateService;
    private final ErpMarketingClient erpMarketingClient;
    private final ValidationContext validationContext;

    public LeadServiceImpl(LeadRepository leadRepository, SocialAddressService socialAddressService, SmsService smsService,
                           NoteService noteService, CustomerService customerService, TemplateService templateService,
                           ErpMarketingClient erpMarketingClient, ValidationContext validationContext) {
        super(leadRepository);
        this.leadRepository = leadRepository;
        this.socialAddressService = socialAddressService;
        this.smsService = smsService;
        this.noteService = noteService;
        this.customerService = customerService;
        this.templateService = templateService;
        this.erpMarketingClient = erpMarketingClient;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Long create(LeadDTO leadDTO) {
        validationContext.validate(leadDTO);
        Lead lead = new Lead();
        lead.setPhone(leadDTO.getPhone());
        lead.setName(leadDTO.getName());
        lead.setSource(leadDTO.getSource());
        lead.setCompanyName(leadDTO.getCompanyName());
        lead.setRole(leadDTO.getRole());
        lead.setReason(leadDTO.getReason());
        lead.setLeadPaymentType(leadDTO.getLeadPaymentType());
        if (leadDTO.getUtm() != null) {
            lead.setUtmSource(leadDTO.getUtm().getUtmSource());
            lead.setUtmMedium(leadDTO.getUtm().getUtmMedium());
            lead.setUtmTerm(leadDTO.getUtm().getUtmTerm());
            lead.setUtmContent(leadDTO.getUtm().getUtmContent());
            lead.setUtmCampaign(leadDTO.getUtm().getUtmCampaign());
        }
        save(lead);
        if (!CollectionUtils.isEmpty(leadDTO.getSocialAddresses())) {
            socialAddressService.createBatch(lead.getEntityType(), lead.getEntityId(), leadDTO.getSocialAddresses());
        }
        return lead.getId();
    }

    @Override
    @Transactional
    public Long update(Long id, LeadDTO leadDTO) {
        return leadRepository.findById(id).map(lead -> {
            lead.setName(leadDTO.getName());
            lead.setTin(leadDTO.getTin());
            lead.setRole(leadDTO.getRole());
            lead.setReason(leadDTO.getReason());
            lead.setCompanyName(leadDTO.getCompanyName());
            lead.setState(leadDTO.getState());
            lead.setStatus(leadDTO.getStatus());
            lead.setCallStatus(leadDTO.getCallStatus());
            lead.setPaymentStatus(leadDTO.getPaymentStatus());
            if (StringUtils.isNotEmpty(leadDTO.getComment())) {
                this.comments(id, leadDTO.getComment());
            }
            save(lead);
            if (!CollectionUtils.isEmpty(leadDTO.getSocialAddresses())) {
                socialAddressService.createBatch(lead.getEntityType(), lead.getEntityId(), leadDTO.getSocialAddresses());
            }
            return id;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(LEAD));
    }

    @Override
    public Page<LeadListAdminDTO> getList(LeadFilter filter) {
        ResultList<Lead> resultList = leadRepository.getResultList(filter);
        List<LeadListAdminDTO> result = resultList.getList().stream().map(lead -> {
            LeadListAdminDTO leadListAdminDTO = new LeadListAdminDTO();
            leadListAdminDTO.setId(lead.getId());
            leadListAdminDTO.setTin(lead.getTin());
            leadListAdminDTO.setName(lead.getName());
            leadListAdminDTO.setPhone(lead.getPhone());
            leadListAdminDTO.setStatus(lead.getStatus());
            leadListAdminDTO.setCallStatus(lead.getCallStatus());
            leadListAdminDTO.setCompanyName(lead.getCompanyName());
            leadListAdminDTO.setRole(lead.getRole());
            leadListAdminDTO.setReason(lead.getReason());
            leadListAdminDTO.setSource(lead.getSource());
            leadListAdminDTO.setState(lead.getState());
            leadListAdminDTO.setPaymentStatus(lead.getPaymentStatus());
            if (lead.getCreator() != null) {
                leadListAdminDTO.setCreator(lead.getCreator().toCommonDTO());
            }
            leadListAdminDTO.setCreatedDate(lead.getCreatedDate());
            leadListAdminDTO.setResolvedDate(lead.getLastModifiedDate());
            leadListAdminDTO.setUtm(lead.getUTM());
            leadListAdminDTO.setSocialAddresses(socialAddressService.getByEntityTypeAndEntityId(lead.getEntityType(), lead.getEntityId()));
            return leadListAdminDTO;
        }).collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public LeadResponseDTO getListSmartpos(String tin) {
        if (StringUtils.isEmpty(tin))
            return null;
        Customer customer = customerService.getByTin(tin);
        LeadResponseDTO responseDTO = new LeadResponseDTO();
        leadRepository
                .findFirstByPhoneOrderByIdDesc(customer.getPhone())
                .ifPresent(lead -> {
                    responseDTO.setId(lead.getId());
                    responseDTO.setName(lead.getName());
                    responseDTO.setPhone(lead.getPhone());
                    responseDTO.setStatus(lead.getStatus());
                    if (lead.getLeadPaymentType() != null) {
                        responseDTO.setLeadPaymentType(lead.getLeadPaymentType().getCode());
                    }
                });
        return responseDTO;
    }

    @Override
    public LeadDetailAdminDTO get(Long id) {
        return leadRepository.findById(id).map(lead -> {
            LeadDetailAdminDTO leadDetailAdminDTO = new LeadDetailAdminDTO();
            leadDetailAdminDTO.setId(lead.getId());
            leadDetailAdminDTO.setName(lead.getName());
            leadDetailAdminDTO.setTin(lead.getTin());
            leadDetailAdminDTO.setPhone(lead.getPhone());
            leadDetailAdminDTO.setCompanyName(lead.getCompanyName());
            leadDetailAdminDTO.setCallStatus(lead.getCallStatus());
            leadDetailAdminDTO.setStatus(lead.getStatus());
            leadDetailAdminDTO.setState(lead.getState());
            leadDetailAdminDTO.setPaymentStatus(lead.getPaymentStatus());
            leadDetailAdminDTO.setRole(lead.getRole());
            leadDetailAdminDTO.setReason(lead.getReason());
            leadDetailAdminDTO.setSource(lead.getSource());
            leadDetailAdminDTO.setUtm(lead.getUTM());
            leadDetailAdminDTO.setSocialAddresses(socialAddressService.getByEntityTypeAndEntityId(lead.getEntityType(), lead.getEntityId()));
            if (lead.getCreatorId() != null) {
                leadDetailAdminDTO.setCreator(lead.getCreator().toCommonDTO());
            }
            leadDetailAdminDTO.setCreatedDate(lead.getCreatedDate());
            leadDetailAdminDTO.setResolvedDate(lead.getLastModifiedDate());
            return leadDetailAdminDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(LEAD));
    }

    @Override
    @Transactional
    public void updateCallStatus(Long id, CallStatus callStatus) {
        leadRepository.findById(id).ifPresentOrElse(
                customerRequest -> {
                    customerRequest.setCallStatus(callStatus);
                    save(customerRequest);
                },
                () -> validationService.entityNotFoundExceptionThrow(LEAD).get());
    }

    @Override
    @Transactional
    public void updateState(Long id, LeadState status) {
        leadRepository.findById(id).ifPresentOrElse(
                customerRequest -> {
                    customerRequest.setState(status);
                    save(customerRequest);
                },
                () -> validationService.entityNotFoundExceptionThrow(LEAD).get());
    }

    @Override
    @Transactional
    public void updateStatus(Long id, LeadStatus leadStatus) {
        leadRepository.findById(id).ifPresentOrElse(
                customerRequest -> {
                    customerRequest.setStatus(leadStatus);
                    save(customerRequest);
                },
                () -> validationService.entityNotFoundExceptionThrow(LEAD).get());
    }

    @Override
    public String generateMessage(Long templateId, Language language) {
        return templateService.getLeadMessage(templateId, language);
    }

    @Override
    @Transactional
    public void sendSms(LeadSmsDTO leadSmsDTO) {
        Lead lead = findById(leadSmsDTO.getId());
        if (StringUtils.isNotEmpty(lead.getPhone())) {
            this.comments(lead.getId(), leadSmsDTO.getDescription());
            EtcRequestDTO etcRequestDto = new EtcRequestDTO();
            etcRequestDto.setPhone(lead.getPhone());
            etcRequestDto.setText(leadSmsDTO.getDescription());
            smsService.send(etcRequestDto);
        }
    }

    @Override
    @Transactional
    public Long createFromWebSites(LeadRequestDTO leadRequestDTO, String source) {
        LeadDTO leadDTO = new LeadDTO();
        leadDTO.setName(leadRequestDTO.getName());
        leadDTO.setPhone(leadRequestDTO.getPhone());
        leadDTO.setSource(source);
        leadDTO.setPhone(leadRequestDTO.getPhone());
        LeadUTMDTO leadUTMDTO = new LeadUTMDTO();
        leadUTMDTO.setUtmSource(leadRequestDTO.getUtmSource());
        leadUTMDTO.setUtmMedium(leadRequestDTO.getUtmMedium());
        leadUTMDTO.setUtmCampaign(leadRequestDTO.getUtmCampaign());
        leadUTMDTO.setUtmTerm(leadRequestDTO.getUtmTerm());
        leadUTMDTO.setUtmContent(leadRequestDTO.getUtmContent());
        leadDTO.setUtm(leadUTMDTO);
//        Thread.startVirtualThread(() -> erpMarketingClient.createGoogleSheet(leadRequestDTO));
        return this.create(leadDTO);
    }

    @Override
    @Transactional
    public void createFromTelegramBot(LeadRequestDTO requestDTO) {
        LeadDTO leadDTO = new LeadDTO();
        leadDTO.setName(requestDTO.getName());
        leadDTO.setPhone(requestDTO.getPhone());
        if (StringUtils.isNotEmpty(requestDTO.getBusinessType())) {
            LeadUTMDTO leadUTMDTO = new LeadUTMDTO();
            leadUTMDTO.setUtmSource("telegram");
            leadUTMDTO.setUtmTerm(requestDTO.getBusinessType());
            leadDTO.setUtm(leadUTMDTO);
        }
        leadDTO.setSource("telegram");
        this.create(leadDTO);
    }

    @Override
    @Transactional
    public Long createFromMeta(LeadMetaDTO leadMetaDTO) {
        LeadDTO leadDTO = new LeadDTO();
        leadDTO.setName(leadMetaDTO.getName());
        leadDTO.setPhone(leadMetaDTO.getPhone());
        String source = switch (leadMetaDTO.getSource()) {
            case "fb" -> "facebook";
            case "ig" -> "instagram";
            default -> leadMetaDTO.getSource();
        };
        leadDTO.setSource(source);

        LeadUTMDTO leadUTMDTO = new LeadUTMDTO();
        leadUTMDTO.setUtmSource(leadMetaDTO.getSource());
        leadUTMDTO.setUtmMedium(leadMetaDTO.getUtmMedium().replaceAll("_", " "));
        leadUTMDTO.setUtmCampaign(leadMetaDTO.getUtmCampaign().replaceAll("_", " "));
        leadUTMDTO.setUtmTerm(leadMetaDTO.getUtmTerm().replaceAll("_", " "));
        leadUTMDTO.setUtmContent(leadMetaDTO.getUtmContent());

        leadDTO.setUtm(leadUTMDTO);
        return this.create(leadDTO);
    }

    @Override
    @Transactional
    public Long createFromSmartpos(LeadSmartposDTO leadSmartposDTO, String source) {
        Customer customer = customerService.getByTin(leadSmartposDTO.getTin());
        LeadDTO leadDTO = new LeadDTO();
        leadDTO.setSource(source);
        leadDTO.setPhone(customer.getPhone());
        leadDTO.setName("_");
        leadDTO.setCompanyName(customer.getCompanyName());
        leadDTO.setLeadPaymentType(leadSmartposDTO.getLeadPaymentType());
        Long id = this.create(leadDTO);
        if (id != null && leadSmartposDTO.getLeadPaymentType() != null) {
            this.comments(id, leadSmartposDTO.getLeadPaymentType().getName());
        }
        return id;
    }

    private void comments(Long id, String comment) {
        NoteDTO noteDTO = new NoteDTO();
        noteDTO.setEntityId(id);
        noteDTO.setDescription(comment);
        noteDTO.setEntityType(EntityTypeEnum.LEAD);
        noteService.create(noteDTO);
    }
}
