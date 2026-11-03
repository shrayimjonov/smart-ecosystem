package uz.uzkassa.cts.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.config.ApplicationProperties;
import uz.uzkassa.cts.domain.FileUpload;
import uz.uzkassa.cts.domain.Integration;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.file.FileListDTO;
import uz.uzkassa.cts.dto.integration.admin.IntegrationDTO;
import uz.uzkassa.cts.dto.integration.admin.IntegrationDetailAdminDTO;
import uz.uzkassa.cts.dto.integration.admin.IntegrationListAdminDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.DocumentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.integrations.*;
import uz.uzkassa.cts.repository.IntegrationRepository;
import uz.uzkassa.cts.service.EmailService;
import uz.uzkassa.cts.service.FileUploadService;
import uz.uzkassa.cts.service.IntegrationService;
import uz.uzkassa.cts.service.SmsService;
import uz.uzkassa.cts.social.TelegramService;
import uz.uzkassa.cts.validator.ValidationContext;
import uz.uzkassa.cts.web.rest.errors.IntegrationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class IntegrationServiceImpl extends BaseServiceImpl<Integration, Long> implements IntegrationService {

    IntegrationRepository integrationRepository;
    FileUploadService fileUploadService;
    ApplicationProperties applicationProperties;
    ApayClient apayClient;
    ApayGrpcClient apayGrpcClient;
    SmartposClient smartposClient;
    SupplyClient supplyClient;
    OfdClient ofdClient;
    SoliqClient soliqClient;
    HorecaClient horecaClient;
    LauncherClient launcherClient;
    XfileClient xfileClient;
    FacturaClient facturaClient;
    BillingServiceFeignClient billingServiceFeignClient;
    AiTimeTrackClient aiTimeTrackClient;
    TelegramService telegramService;
    SmsService smsService;
    EmailService emailService;
    ValidationContext validationContext;

    public IntegrationServiceImpl(IntegrationRepository integrationRepository,
                                  FileUploadService fileUploadService,
                                  ApplicationProperties applicationProperties,
                                  ApayClient apayClient,
                                  ApayGrpcClient apayGrpcClient, SmartposClient smartposClient,
                                  SupplyClient supplyClient,
                                  OfdClient ofdClient,
                                  SoliqClient soliqClient,
                                  HorecaClient horecaClient,
                                  LauncherClient launcherClient,
                                  XfileClient xfileClient,
                                  FacturaClient facturaClient,
                                  BillingServiceFeignClient billingServiceFeignClient,
                                  AiTimeTrackClient aiTimeTrackClient,
                                  TelegramService telegramService,
                                  SmsService smsService,
                                  EmailService emailService, ValidationContext validationContext
    ) {
        super(integrationRepository);
        this.integrationRepository = integrationRepository;
        this.fileUploadService = fileUploadService;
        this.applicationProperties = applicationProperties;
        this.apayClient = apayClient;
        this.apayGrpcClient = apayGrpcClient;
        this.smartposClient = smartposClient;
        this.supplyClient = supplyClient;
        this.ofdClient = ofdClient;
        this.soliqClient = soliqClient;
        this.horecaClient = horecaClient;
        this.launcherClient = launcherClient;
        this.xfileClient = xfileClient;
        this.facturaClient = facturaClient;
        this.billingServiceFeignClient = billingServiceFeignClient;
        this.aiTimeTrackClient = aiTimeTrackClient;
        this.telegramService = telegramService;
        this.smsService = smsService;
        this.emailService = emailService;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Long create(IntegrationDTO integrationDTO) {
        validationContext.validate(integrationDTO);
        Integration integration = new Integration();
        integration.setName(integrationDTO.getName());
        integration.setDescription(integrationDTO.getDescription());
        integration.setType(integrationDTO.getType());
        integrationRepository.save(integration);
        return integration.getId();
    }

    @Override
    @Transactional
    public Long update(Long id, IntegrationDTO integrationDTO) {
        integrationDTO.setId(id);
        validationContext.validate(integrationDTO);
        return integrationRepository.findById(integrationDTO.getId()).map(integration -> {
            integration.setName(integrationDTO.getName());
            integration.setDescription(integrationDTO.getDescription());
            integration.setType(integrationDTO.getType());
            integrationRepository.save(integration);
            return integration.getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(INTEGRATION));
    }

    @Override
    public IntegrationDetailAdminDTO get(Long id) {
        return integrationRepository.findById(id).map(integration -> {
            IntegrationDetailAdminDTO integrationDetailAdminDTO = new IntegrationDetailAdminDTO();
            integrationDetailAdminDTO.setId(integration.getId());
            integrationDetailAdminDTO.setName(integration.getName());
            integrationDetailAdminDTO.setDescription(integration.getDescription());
            integrationDetailAdminDTO.setType(integration.getType());
            integrationDetailAdminDTO.setStatus(integration.getStatus());
            integrationDetailAdminDTO.setErrorMessage(integration.getErrorMessage());
            integrationDetailAdminDTO.setErrorDate(integration.getErrorDate());
            if (integration.getLogoId() != null) {
                FileUpload logo = integration.getLogo();
                FileListDTO logoDTO = new FileListDTO();
                logoDTO.setId(logo.getId());
                logoDTO.setName(logo.getFileName());
                logoDTO.setUrl(logo.getDownloadUrl());
                logoDTO.setDocumentType(logo.getDocumentType());
                integrationDetailAdminDTO.setLogo(logoDTO);
            }
            return integrationDetailAdminDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(INTEGRATION));
    }

    @Override
    public Page<IntegrationListAdminDTO> getListForAdmin(BaseFilter filter) {
        ResultList<Integration> resultList = integrationRepository.getResultList(filter);
        List<IntegrationListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(integration -> {
                    IntegrationListAdminDTO integrationListAdminDTO = new IntegrationListAdminDTO();
                    integrationListAdminDTO.setId(integration.getId());
                    integrationListAdminDTO.setName(integration.getName());
                    integrationListAdminDTO.setDescription(integration.getDescription());
                    integrationListAdminDTO.setType(integration.getType());
                    integrationListAdminDTO.setStatus(integration.getStatus());
                    integrationListAdminDTO.setErrorMessage(integration.getErrorMessage());
                    integrationListAdminDTO.setErrorDate(integration.getErrorDate());
                    if (integration.getLogoId() != null) {
                        FileUpload logo = integration.getLogo();
                        FileListDTO logoDTO = new FileListDTO();
                        logoDTO.setId(logo.getId());
                        logoDTO.setName(logo.getFileName());
                        logoDTO.setUrl(logo.getDownloadUrl());
                        logoDTO.setDocumentType(logo.getDocumentType());
                        integrationListAdminDTO.setLogo(logoDTO);
                    }
                    return integrationListAdminDTO;
                }).toList();
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    @Transactional
    public void check(Long id) {
        Integration integration = this.findById(id);
        try {
            switch (integration.getType()) {
                case APAY -> apayClient.authenticate();
                case APAY_GRPC -> apayGrpcClient.authenticate();
                case SMARTPOS -> smartposClient.authenticate();
                case SUPPLY -> supplyClient.authenticate();
                case OFD -> ofdClient.authenticate();
                case HORECA -> horecaClient.authenticate();
                case LAUNCHER -> launcherClient.authenticate();
                case X_FILE -> xfileClient.authenticate();
                case SOLIQ -> soliqClient.getCustomerByTin(applicationProperties.getTxkmProperty().getP100p10Tin());
                case FACTURA -> facturaClient.getBanks("ru");
                case BILLING -> billingServiceFeignClient.authenticate();
                case AI_TIME_TRACK -> aiTimeTrackClient.authenticate();
                case TELEGRAM -> telegramService.checkConnection();
                case SMS -> smsService.checkConnection();
                case EMAIL -> emailService.checkConnection();
                default -> throw new IntegrationException("Not implemented");
            }
            integration.setStatus(CommonStatus.ACTIVE);
            integration.setErrorMessage(null);
            integration.setErrorDate(null);
        } catch (Exception e) {
            integration.setStatus(CommonStatus.IN_ACTIVE);
            integration.setErrorMessage(e.getMessage());
            integration.setErrorDate(LocalDateTime.now());
        }
        integrationRepository.save(integration);
    }

    @Override
    @Transactional
    public void uploadLogo(Long id, MultipartFile file) {
        Integration integration = this.findById(id);
        FileUpload logo = fileUploadService.upload(file, EntityTypeEnum.INTEGRATION, id, DocumentType.INTEGRATION_LOGO);
        integration.setLogoId(logo.getId());
        integration.setLogo(logo);
        this.save(integration);
    }
}
