package uz.uzkassa.cts.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.config.ApplicationProperties;
import uz.uzkassa.cts.constants.RegexConstants;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.domain.cases.*;
import uz.uzkassa.cts.domain.report.ContractReport;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.ResultList;
//import uz.uzkassa.cts.dto.caseAgreement.CaseAgreementDTO;
import uz.uzkassa.cts.dto.caseTypeEmployees.CaseTypeEmployeesGetDTO;
import uz.uzkassa.cts.dto.cases.*;
import uz.uzkassa.cts.dto.cases.caseproperty.CaseAction;
import uz.uzkassa.cts.dto.cases.caseproperty.DeactivateDeviceProperty;
import uz.uzkassa.cts.dto.cases.request.CaseAcceptDto;
import uz.uzkassa.cts.dto.casestatuslog.CaseStatusLogDTO;
import uz.uzkassa.cts.dto.payload.CaseBotPayload;
import uz.uzkassa.cts.dto.payload.NotificationPayload;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.enums.billing.AgreementStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.CaseFilter;
import uz.uzkassa.cts.helper.excelhelper.XlsxWriter;
import uz.uzkassa.cts.integrations.SmartposClient;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqCaseBotProducer;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqNotificationProducer;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.*;
import uz.uzkassa.cts.social.TelegramService;
import uz.uzkassa.cts.utils.DateUtil;
import uz.uzkassa.cts.utils.Utils;
import uz.uzkassa.cts.validator.LocalizationMessage;
import uz.uzkassa.cts.web.rest.errors.BadRequestException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
@Slf4j
public class CaseServiceImpl extends BaseServiceImpl<Cases, Long> implements CaseService {

    private final CaseRepository caseRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final Validator validator;
    private final AutowireCapableBeanFactory autowireCapableBeanFactory;
    private final CustomerOldService customerOldService;
    private final CustomerRepository customerRepository;
    private final CaseNoteService caseNoteService;
    private final XlsxWriter xlsxWriter;
    private final CaseFileRepository caseFileRepository;
    private final RabbitMqCaseBotProducer rabbitMqCaseBotProducer;
    private final CaseTypeEmployeesRepository caseTypeEmployeesRepository;
    private static CaseTypeAccessRepository caseTypeAccessRepository;
    private final ContractReportRepository contractReportRepository;
    private final TelegramUsernameRepository telegramUsernameRepository;
    private final CustomerBranchDeviceRepository customerBranchDeviceRepository;
    private final CustomerEmployeeRepository customerEmployeeRepository;
    private final TelegramService telegramService;
    private final DeviceRepository deviceRepository;
    private final ApplicationProperties applicationProperties;
    private final SmartposService smartposService;
    private final CaseAgreementService caseAgreementService;
    private final ReferenceService referenceService;
    private final SmartposClient smartposClient;
    private final RabbitMqNotificationProducer rabbitMqNotificationProducer;
    private final AddressService addressService;
    private final LocalizationMessage localizationMessage;
    private final CaseStatusLogService caseStatusLogService;

    @Autowired
    public void setCaseTypeAccessRepository(CaseTypeAccessRepository caseTypeAccessRepository) {
        CaseServiceImpl.caseTypeAccessRepository = caseTypeAccessRepository;
    }

    public CaseServiceImpl(CaseRepository caseRepository,
                           UserRepository userRepository,
                           ObjectMapper objectMapper,
                           Validator validator,
                           AutowireCapableBeanFactory autowireCapableBeanFactory,
                           CustomerOldService customerOldService,
                           CustomerRepository customerRepository,
                           CaseNoteService caseNoteService,
                           XlsxWriter xlsxWriter,
                           CaseFileRepository caseFileRepository,
                           RabbitMqCaseBotProducer rabbitMqCaseBotProducer,
                           CaseTypeEmployeesRepository caseTypeEmployeesRepository,
                           ContractReportRepository contractReportRepository,
                           TelegramUsernameRepository telegramUsernameRepository,
                           CustomerBranchDeviceRepository customerBranchDeviceRepository,
                           TelegramService telegramService, DeviceRepository deviceRepository,
                           ApplicationProperties applicationProperties,
                           SmartposService smartposService,
                           CaseAgreementService caseAgreementService,
                           CustomerEmployeeRepository customerEmployeeRepository,
                           ReferenceService referenceService,
                           SmartposClient smartposClient, RabbitMqNotificationProducer rabbitMqNotificationProducer, AddressService addressService, LocalizationMessage localizationMessage, CaseStatusLogService caseStatusLogService) {
        super(caseRepository);
        this.caseRepository = caseRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
        this.validator = validator;
        this.autowireCapableBeanFactory = autowireCapableBeanFactory;
        this.customerOldService = customerOldService;
        this.customerRepository = customerRepository;
        this.caseNoteService = caseNoteService;
        this.xlsxWriter = xlsxWriter;
        this.caseFileRepository = caseFileRepository;
        this.rabbitMqCaseBotProducer = rabbitMqCaseBotProducer;
        this.caseTypeEmployeesRepository = caseTypeEmployeesRepository;
        this.contractReportRepository = contractReportRepository;
        this.telegramUsernameRepository = telegramUsernameRepository;
        this.customerBranchDeviceRepository = customerBranchDeviceRepository;
        this.customerEmployeeRepository = customerEmployeeRepository;
        this.telegramService = telegramService;
        this.deviceRepository = deviceRepository;
        this.applicationProperties = applicationProperties;
        this.smartposService = smartposService;
        this.referenceService = referenceService;
        this.caseAgreementService = caseAgreementService;
        this.smartposClient = smartposClient;
        this.rabbitMqNotificationProducer = rabbitMqNotificationProducer;
        this.addressService = addressService;
        this.localizationMessage = localizationMessage;
        this.caseStatusLogService = caseStatusLogService;
    }

    @Override
    public CaseDTO create(CaseDTO caseDTO) {
        Cases cases = mapToEntity(caseDTO);
        cases.setJsonProperties(convertToString(caseDTO));
        this.operationByCaseType(cases, caseDTO);
        save(cases);
        caseDTO.setId(cases.getId());
        if (cases.getCaseType() == CaseType.TEMPORARY_REPLACEMENT_REVERT) {
            Object object = getFieldValue(cases, "parentCaseId");
            Cases caseParent = findById((Long) object);
            if (CaseType.TEMPORARY_REPLACEMENT != caseParent.getCaseType())
                throw validationService.badRequestExceptionThrow(TYPE_CASE_SHOULD_BE, CaseType.TEMPORARY_REPLACEMENT.getName()).get();
            if (CaseStatus.FIRST_STEP_FINISHED == caseParent.getStatus())
                throw validationService.badRequestExceptionThrow(CASE_STATUS, cases.getStatus().getName()).get();
            if (CaseStatus.ACCEPTED != caseParent.getStatus())
                throw validationService.badRequestExceptionThrow(TYPE_CASE_SHOULD_BE, CaseStatus.ACCEPTED.getName()).get();
            caseParent.setStatus(CaseStatus.FIRST_STEP_FINISHED);
            save(caseParent);
            List<CaseFile> caseFiles = caseFileRepository.findByCaseId(caseParent.getId());
            for (CaseFile file : caseFiles) {
                CaseFile caseFile = new CaseFile();
                caseFile.setCaseId(cases.getId());
                caseFile.setFileUrl(file.getFileUrl());
                caseFile.setFileName(file.getFileName());
                caseFile.setDescription(file.getDescription());
                caseFile.setDocumentType(file.getDocumentType());
                caseFileRepository.save(caseFile);
            }
        } else if (cases.getCaseType() == CaseTypeEnum.CHANGE_MANAGER) {
            Customer customer = customerOldService.findById(caseDTO.getCustomerId());
            if (customer.getManagerId() == null) {
                customer.setManagerId(cases.getCreatorId());
                customerRepository.save(customer);
                cases.setStatus(CaseStatusEnum.ACCEPTED);
                save(cases);
            }
        }
        CaseStatusLogDTO caseStatusLogDTO = new CaseStatusLogDTO();
        caseStatusLogDTO.setCaseId(cases.getId());
        caseStatusLogDTO.setStatus(caseDTO.getStatus());
        caseStatusLogService.create(caseStatusLogDTO);
        this.billingAgreement(cases);
        return caseDTO;
    }

    private void operationByCaseType(Cases cases, CaseDTO caseDTO) {
        String deviceId = getClassFieldValue(cases, "deviceId");
        String deviceSerialNumber = getClassFieldValue(cases, "deviceSerialNumber");
        if (caseRepository.findByTypeAndDeviceAndStatuses(
                cases.getCustomerId(),
                cases.getCaseType().getCode(),
                deviceId,
                deviceSerialNumber,
                List.of(CaseStatus.DRAFT.ordinal(), CaseStatus.NEW.ordinal(), CaseStatus.PENDING.ordinal(), CaseStatus.DEFECTIVE.ordinal())).isPresent()) {
            throw validationService.entityAlreadyExistExceptionThrow(CASE).get();
        }
        switch (caseDTO.getType()) {
            case DEREGISTER_KKM -> {
                String serialNumber = getClassFieldValue(cases, "deviceSerialNumber");
                Optional<ContractReport> contractOptional =
                        contractReportRepository.findBySerialNumberAndSaleStatusIn(serialNumber, List.of(
                                ContractReportInvoiceSaleStatus.DEFECT,
                                ContractReportInvoiceSaleStatus.DEVICE_FOR_TEST,
                                ContractReportInvoiceSaleStatus.EXCHANGE_DEVICE,
                                ContractReportInvoiceSaleStatus.DISMANTLING,
                                ContractReportInvoiceSaleStatus.EMPTY,
                                ContractReportInvoiceSaleStatus.FREE_REPLACEMENT_KKM));
                if (contractOptional.isPresent()) {
                    throw validationService.badRequestExceptionThrow(CONTRACT_REPORT_SALE_STATUS).get();
                }
                this.sendToTelegramBot(cases);
            }
            case APP_TYPE_CHANGE -> {
                String appType = getClassFieldValue(cases, "appType");
                if (StringUtils.isNotEmpty(appType) && CustomerAppType.valueOf(appType) == CustomerAppType.CTS) {
                    customerBranchDeviceRepository
                            .findByCustomerId(caseDTO.getCustomerId())
                            .forEach(customerBranchDevice -> {
                                if (customerBranchDevice.getDevice() != null && Objects.equals(customerBranchDevice.getDevice().getModel().getCode(), DeviceModelType.P10.name())) {
                                    throw new BadRequestException("error.add.device");
                                }
                            });
                }
            }
            case HUMO_CONNECTION_CHANGE -> {
                String secretKey = getClassFieldValue(cases, "secretKey");
                if (StringUtils.isNotEmpty(secretKey.trim()) && !secretKey.matches(RegexConstants.BANK_SECRET_KEY)) {
                    throw validationService.badRequestExceptionThrow(INCORRECT_INPUT, PASSWORD).get();
                }
                String serialNumber = getClassFieldValue(cases, "deviceSerialNumber");
                Device device = deviceRepository.findFirstBySerialNumber(serialNumber);
                if (device != null && DeviceModelType.P100.name().equals(device.getModel().getCode())) {
                    throw validationService.badRequestExceptionThrow(CASE_TYPE).get();
                }
            }
            case BRANCH_DATA_CHANGE -> {
                Object name = getFieldValue(cases, "name");
                Object activityTypeId = getFieldValue(cases, "activityTypeId");
                Object parentActivityTypeId = getFieldValue(cases, "parentActivityTypeId");
                if (name == null && activityTypeId == null && parentActivityTypeId == null) {
                    throw validationService.badRequestExceptionThrow(CASE_TYPE_TT_INFO).get();
                }
            }
            case REPLACEMENT_KKM -> {
                String serialNumber = getClassFieldValue(cases, "deviceSerialNumber");
                Optional<ContractReport> contractOptional =
                        contractReportRepository.findBySerialNumberAndSaleStatusIn(serialNumber, List.of(ContractReportInvoiceSaleStatus.SOLD));
                if (contractOptional.isEmpty()) {
                    throw validationService.badRequestExceptionThrow(CONTRACT_REPORT_BY_STATUS_SOLD).get();
                }
            }
            case CHANGE_MANAGER -> {
                String phoneNumber = getClassFieldValue(cases, "phoneNumber");
                if (!Utils.isValidPhone(phoneNumber)) {
                    throw validationService.badRequestExceptionThrow(PHONE_NUMBER_INVALID_FORMAT).get();
                }
                Customer customer = customerOldService.findById(caseDTO.getCustomerId());
                if (Objects.equals(getCurrentUserDomain().getId(), customer.getManagerId())) {
                    throw validationService.badRequestExceptionThrow(SAME_MANAGER).get();
                }
                Reference reference = referenceService.findById(Long.valueOf(getClassFieldValue(cases, "referenceId")));
                Map<String, Object> map = new HashMap<>();
                map.put("customerId", caseDTO.getCustomerId());
                map.put("oldManagerName", customer.getManagerId() != null ? customer.getManager().getName() : null);
                map.put("newManagerName", getCurrentUserDomain().getName());
                map.put("phoneNumber", phoneNumber);
                map.put("reason", reference.getNameRu());
                cases.setJsonProperties(convertToString(map));
                if (customer.getManagerId() == null) {
                    userRepository
                            .findById(getCurrentUserId())
                            .ifPresent(user -> {
                                customer.setManagerId(user.getId());
                                customer.setManager(user);
                                smartposService.attachManager(List.of(customer.getTin()), user);
                            });
                    customerRepository.save(customer);
                    cases.setStatus(CaseStatus.ACCEPTED);
                }
            }
            case CHANGE_DEVICE_TYPE -> {
                String deviceType = getClassFieldValue(cases, "deviceType");
                if (DeviceType.SOFTWARE.getCode().equals(deviceType)) {
                    throw validationService.badRequestExceptionThrow(OBJECT_TYPE, CASE, deviceType).get();
                }
            }
            case DEACTIVATE_DEVICE -> deviceRepository.findById(Long.valueOf(deviceId)).ifPresent(device -> {
                if (!Arrays.asList(DeviceType.TERMINAL, DeviceType.SOFTWARE).contains(device.getType())) {
                    throw validationService.badRequestExceptionThrow(DEVICE_DEACTIVATION_ERROR).get();
                }
                Reference reference = referenceService.findById(Long.valueOf(getClassFieldValue(cases, "reasonId")));
                DeactivateDeviceProperty obj = convertToDto(cases.getJsonProperties(), DeactivateDeviceProperty.class);
                obj.setReason(reference.getNameRu());
                cases.setJsonProperties(convertToString(obj));
            });
        }
    }

    private void billingAgreement(Cases cases) {
        if (!cases.getCustomer().isWhiteList() && EnumSet.of(CaseType.DEREGISTER_KKM, CaseType.REPLACEMENT_FM, CaseType.HUMO_CONNECTION_CHANGE).contains(cases.getCaseType())) {
            CaseAgreementDTO caseOperationDTO = new CaseAgreementDTO();
            caseOperationDTO.setEntityId(cases.getEntityId());
            caseOperationDTO.setEntityType(cases.getEntityType());
            caseOperationDTO.setCaseType(cases.getCaseType());
            caseOperationDTO.setType(CaseXizmatType.CASE);
            caseOperationDTO.setTin(cases.getCustomer().getTin());
            switch (cases.getCaseType()) {
                case DEREGISTER_KKM -> {
                    boolean lossFiscalModule = Boolean.parseBoolean(getClassFieldValue(cases, "lossFiscalModule"));
                    caseOperationDTO.setReason(lossFiscalModule ? FMReportReason.FM_LOST : FMReportReason.FM_NOT_LOST);
                    caseAgreementService.createAgreement(caseOperationDTO);
                }
                case REPLACEMENT_FM -> {
                    String reasonStr = getClassFieldValue(cases, "reason");
                    if (StringUtils.isNotEmpty(reasonStr)) {
                        if (Reason.LOST.equals(Reason.valueOf(reasonStr))) {
                            caseOperationDTO.setReason(FMReportReason.LOST);
                            caseAgreementService.createAgreement(caseOperationDTO);
                        } else if (Reason.BURNT_OUT.equals(Reason.valueOf(reasonStr))) {
                            caseOperationDTO.setReason(FMReportReason.BURNT_OUT);
                            caseAgreementService.createAgreement(caseOperationDTO);
                        }
                    }
                }
                case HUMO_CONNECTION_CHANGE -> {
                    String paymentType = getClassFieldValue(cases, "paymentType");
                    String deviceSerialNumber = getClassFieldValue(cases, "deviceSerialNumber");
                    Device device = deviceRepository.findBySerialNumber(deviceSerialNumber);
                    if (StringUtils.isNotEmpty(paymentType)
                            && PaymentType.UZ_CARD_MASTERCARD.equals(PaymentType.valueOf(paymentType))
                            && DeviceModelType.P10.name().equalsIgnoreCase(device.getModel().getCode())) {
                        return;
                    }
                    caseAgreementService.createAgreement(caseOperationDTO);
                }
            }
        }
    }

    private Object getFieldValue(Cases cases, String fieldName) {
        try {
            Class<?> clazz = Class.forName(cases.getCaseType().getClassName());
            Object obj = convertToDto(cases.getJsonProperties(), clazz);
            Field f = obj.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            return f.get(obj);
        } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CaseDetailDTO accept(CaseAcceptDto acceptDto) {
        Cases cases = findById(acceptDto.getCaseId());
        if (!EnumSet.of(CaseType.APP_TYPE_CHANGE, CaseType.ACT_OF_RECONCILIATION).contains(cases.getCaseType())) {
            caseFileRepository.findByCaseIdAndDocumentType(cases.getId(), DocumentType.APPLICATION)
                    .orElseThrow(validationService.entityNotFoundExceptionThrow(FILE, TYPE, DocumentType.APPLICATION.getName()));
        }
        if (CaseStatus.REJECTED == cases.getStatus()) {
            throw validationService.badRequestExceptionThrow(CASE_REJECTED, cases.getStatus().getName()).get();
        }
        acceptAction(cases);
        createCaseNoteOnStatusChange(cases);
        this.sendNotification(cases);
        return mapToDto(cases);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createCaseNoteOnStatusChange(Cases cases) {
        CaseNote note = new CaseNote();
        note.setCaseId(cases.getId());
        note.setCases(cases);
        note.setMessageType(CaseMessageType.TEXT);
        note.setMessage("Статус кейса изменен на: " + cases.getStatus().getName());
        caseNoteService.save(note);
        sendProducer(cases);
    }

    private void sendProducer(Cases cases) {
        CaseBotPayload caseBotPayload = new CaseBotPayload();
        caseBotPayload.setCaseId(cases.getId());
        rabbitMqCaseBotProducer.send(caseBotPayload);
    }

    @Override
    public void sendToTelegramBot(CaseBotPayload caseBotPayload) {
        if (Objects.isNull(caseBotPayload.getCaseId()))
            return;
        caseRepository
                .findById(caseBotPayload.getCaseId())
                .ifPresent(this::messageDistribution);
    }

    private void messageDistribution(Cases cases) {
        URI operatorUrl = URI.create(applicationProperties.getTelegramBotCaseProperty().getOperatorUrl() + cases.getId() + "/chat");
        URI adminUrl = URI.create(applicationProperties.getTelegramBotCaseProperty().getAdminUrl() + cases.getId());
        List<CaseTypeEmployeesGetDTO> usernameListByType = caseTypeEmployeesRepository.getUsernameListByType(cases.getCaseType().getCode());
        Locale locale = Locale.of("ru");
        switch (cases.getStatus()) {
            case NEW -> {
                if (CollectionUtils.isNotEmpty(usernameListByType)) {
                    StringBuilder user_ids = new StringBuilder();
                    StringBuilder message_to_admin = new StringBuilder();
                    User creator = cases.getCreator();
                    Address address = addressService.getPermanentAddress(creator.getEntityId(), creator.getEntityType());
                    message_to_admin.append(localizationMessage.localize("telegram.message.admin", new Object[]{
                            cases.getId().toString(),
                            cases.getCaseType().getName(),
                            cases.getStatus().getName(),
                            creator.getFullName(),
                            creator.getBranch().getName(),
                            address != null ? (address.getRegion() != null ? address.getRegion() : "") : "",
                            adminUrl.toString(),
                            adminUrl.toString(),
                            ""
                    }, locale));
                    usernameListByType.forEach(dto -> {
                        if (StringUtils.isNotEmpty(dto.getUsername())) {
                            user_ids.append("<a href = " + "\"tg://user?id=").append(dto.getUsername().trim()).append("\"><b>").append(dto.getFullName()).append("</b></a>\n");

                            /*SEND TO MANAGER(USER) OF CTO*/
                            this.sendToTelegramUserGroup(message_to_admin.toString(), dto.getUsername().trim());
                        }
                    });
                    String message_to_group = localizationMessage.localize("telegram.message.admin", new Object[]{
                            cases.getId().toString(),
                            cases.getCaseType().getName(),
                            cases.getStatus().getName(),
                            creator.getFullName(),
                            creator.getBranch().getName(),
                            address != null ? (address.getRegion() != null ? address.getRegion().getName() : "") : "",
                            adminUrl.toString(),
                            adminUrl.toString(),
                            user_ids.toString()
                    }, locale);
                    /*SEND IT TO GROUP OF CTO*/
                    this.sendToTelegramUserGroup(message_to_group, applicationProperties.getTelegramBotCaseProperty().getGroupId());
                }
                Optional<TelegramUsernames> telegramUsernamesOptional = telegramUsernameRepository.findByEmployeeId(cases.getCreatorId());
                if (telegramUsernamesOptional.isPresent() && StringUtils.isNotEmpty(telegramUsernamesOptional.get().getUsername())) {
                    String message_to_himself = localizationMessage.localize("telegram.message.operator.own", new Object[]{
                            cases.getId().toString(),
                            cases.getCaseType().getName(),
                            cases.getStatus().getName(),
                            cases.getCreator().getFullName(),
                            cases.getCreator().getBranch().getName(),
                            operatorUrl.toString(),
                            operatorUrl.toString()
                    }, locale);
                    /*SEND IT TO the OPERATOR WHO IS CREATED*/
                    this.sendToTelegramUserGroup(message_to_himself, telegramUsernamesOptional.get().getUsername().trim());
                }
            }
            case PENDING, REJECTED, ACCEPTED, DEFECTIVE -> {
                StringBuilder message = new StringBuilder();
                Optional<TelegramUsernames> telegramUsernamesOptional = telegramUsernameRepository.findByEmployeeId(cases.getCreatorId());
                Address address = addressService.getPermanentAddress(cases.getCreator().getEntityId(), cases.getCreator().getEntityType());
                message.append(localizationMessage.localize("telegram.message.operator", new Object[]{
                        cases.getId().toString(),
                        cases.getCaseType().getName(),
                        cases.getStatus().getName(),
                        DateUtil.getLocalDateTimeAsStringWithoutSecond(cases.getCreatedDate()),
                        DateUtil.getLocalDateTimeAsStringWithoutSecond(LocalDateTime.now()),
                        address != null ? (address.getRegion() != null ? address.getRegion().getName() : "") : "",
                        operatorUrl.toString(),
                        operatorUrl.toString()}, locale));
                if (telegramUsernamesOptional.isPresent() && StringUtils.isNotEmpty(telegramUsernamesOptional.get().getUsername())) {
                    /*SEND IT TO OPERATOR WHO CREATED*/
                    this.sendToTelegramUserGroup(message.toString(), telegramUsernamesOptional.get().getUsername().trim());
                }
                /*SEND IT TO GROUP OF CTO*/
                this.sendToTelegramUserGroup(message.toString(), applicationProperties.getTelegramBotCaseProperty().getGroupId());
            }
        }
    }

    private void sendToTelegramBot(Cases cases) {
        Locale locale = Locale.of("ru");
        if (cases.getCaseType() == CaseType.DEREGISTER_KKM) {
            String deviceSerialNumber = getClassFieldValue(cases, "deviceSerialNumber");
            String fmSerialNumber = getClassFieldValue(cases, "fmSerialNumber");
            List<Cases> casesList =
                    caseRepository.findByJsonPropertiesInfo(deviceSerialNumber, fmSerialNumber, cases.getCustomerId());
            if (CollectionUtils.isEmpty(casesList)) {
                String message = localizationMessage.localize("telegram.message.deregister.kkm", new Object[]{
                        cases.getCaseType().getName(),
                        cases.getCustomer().getTin(),
                        deviceSerialNumber,
                }, locale);
                /*SEND IT TO GROUP (DEREGISTER_KKM)*/
                this.sendToTelegramUserGroup(message, applicationProperties.getTelegramBotCaseProperty().getDeregisterkkmGroupId());
            }
        }
    }

    private void sendToTelegramUserGroup(String message, String idGroupUser) {
        telegramService.sendMessage(message, idGroupUser);
    }

    @Override
    public Object getFieldsByType(CaseType type) {
        try {
            return Class.forName(type.getClassName()).newInstance();
        } catch (InstantiationException | ClassNotFoundException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CaseDTO update(CaseDTO caseDTO) {
        Cases cases = mapToEntity(caseDTO);
        cases.setJsonProperties(convertToString(caseDTO));
        save(cases);
        return caseDTO;
    }

    @Override
    public String getDirectorFullName(Long customerId) {
        return customerEmployeeRepository.findFirstByCustomerIdAndRole(customerId, Role.DIRECTOR).map(CustomerEmployee::getName).orElse("");
    }

    @Override
    public String checkPhoneExistence(String phoneNumber) {
        Customer customer = customerRepository.findByPhone(phoneNumber.trim()).orElse(null);
        if (customer != null) {
            throw validationService.entityAlreadyExistExceptionThrow(USER, PHONE, phoneNumber).get();
        }
        return "There is no user with this Phone number: " + phoneNumber;
    }

    @Override
    public Boolean checkPhoneExists(String phoneNumber) {
        if (customerRepository.existsByPhone(phoneNumber.trim())) {
            return Boolean.FALSE;
        } else if (smartposClient.checkLoginExists(phoneNumber)) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;

    }

    @Override
    public String getCustomerByPhone(Long customerId) {
        Customer customer = customerOldService.findById(customerId);
        return customer.getPhone();
    }

    @Override
    public byte[] download(CaseFilter filter) {
        List<Cases> cases = caseRepository.findAll(getCasesSpecification(filter));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        List<CaseExcelExportDto> dto = cases.stream()
                .map(caseEntity -> {
                    CaseExcelExportDto resultDTO = new CaseExcelExportDto();
                    resultDTO.setCaseType(caseEntity.getCaseType().getName());
                    resultDTO.setId(caseEntity.getId());
                    resultDTO.setCustomerName(caseEntity.getCustomer().getName());
                    resultDTO.setCustomerTin(caseEntity.getCustomer().getTin());
                    resultDTO.setDeviceSerial(getClassFieldValue(caseEntity, "deviceSerialNumbers"));
                    resultDTO.setNewDeviceSerial(getClassFieldValue(caseEntity, "newDeviceSerialNumber"));
                    resultDTO.setNewFmSerial(getClassFieldValue(caseEntity, "newFiscalModuleSerial"));
                    resultDTO.setFmSerial(getClassFieldValue(caseEntity, "fmSerialNumber"));
                    resultDTO.setJsonProperties(getReason(caseEntity.getJsonProperties(), caseEntity.getCaseType()));
                    resultDTO.setStatus(caseEntity.getStatus().getName());
                    if (caseEntity.getCreator() != null) {
                        resultDTO.setOperatorName(caseEntity.getCreator().getName());
                        resultDTO.setOperatorBranch(caseEntity.getCreator().getBranch() != null ? caseEntity.getCreator().getBranch().getName() : "");
                    }
                    resultDTO.setCreateDate(caseEntity.getCreatedDate().format(formatter));
                    resultDTO.setLastModifiedDate(caseEntity.getLastModifiedDate().format(formatter));
                    if (CaseType.CHANGE_MANAGER.equals(caseEntity.getCaseType())) {
                        resultDTO.setOldManager(getFieldValue(caseEntity, "oldManagerName") != null ? getFieldValue(caseEntity, "oldManagerName").toString() : "");
                        resultDTO.setNewManager(getFieldValue(caseEntity, "newManagerName") != null ? getFieldValue(caseEntity, "newManagerName").toString() : "");
                    }
                    return resultDTO;
                })
                .collect(Collectors.toList());

        try (Workbook workbook = new XSSFWorkbook()) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            xlsxWriter.write(dto, bos, workbook);
            return bos.toByteArray();
        } catch (IOException e) {
            log.error("ByteArrayOutputStream ex --- ", e);
            return new byte[0];
        }
    }

    private String getClassFieldValue(Cases cases, String fieldName) {
        try {
            Class<?> clazz = Class.forName(cases.getCaseType().getClassName());
            Object obj = convertToDto(cases.getJsonProperties(), clazz);
            Field f = obj.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            return f.get(obj).toString();
        } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException e) {
            if (e instanceof NoSuchFieldException && fieldName.equals("deviceSerialNumbers"))
                return getClassFieldValue(cases, "deviceSerialNumber");
            else if (e instanceof NoSuchFieldException && fieldName.equals("fmSerialNumber"))
                return getClassFieldValue(cases, "fiscalModuleSerial");
            else if (e instanceof NoSuchFieldException)
                return "";
            else
                throw new RuntimeException(e);
        }
    }

    private String getReason(String json, CaseType type) {
        Map<String, Object> reasonMap = convertToDto(json, HashMap.class);
        if (reasonMap != null && !reasonMap.isEmpty()) {
            if (Objects.nonNull(reasonMap.get("reason")) && StringUtils.isNotBlank(reasonMap.get("reason").toString())) {
                switch (type) {
                    case DEREGISTER_KKM -> {
                        String str1 = convertToString(reasonMap.get("reason"));
                        DeregisterReason deregisterReason = convertToDto(str1, DeregisterReason.class);
                        if (Objects.nonNull(deregisterReason)) {
                            return deregisterReason.getName();
                        }
                    }
                    case REPLACEMENT_FM -> {
                        String str2 = convertToString(reasonMap.get("reason"));
                        Reason reasonEnum = convertToDto(str2, Reason.class);
                        if (Objects.nonNull(reasonEnum)) {
                            return reasonEnum.getName();
                        }
                    }
                    default -> {
                        return reasonMap.get("reason").toString();
                    }
                }
            }
        }
        return "";
    }

    private String convertToString(CaseDTO caseDTO) {
        try {
            Class<?> clazz = Class.forName(caseDTO.getType().getClassName());

            Object obj = objectMapper.convertValue(caseDTO.getData(), clazz);
            Set<ConstraintViolation<Object>> constraintViolations = validator.validate(obj);
            if (!constraintViolations.isEmpty()) {
                throw new ConstraintViolationException(constraintViolations);
            }

            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private String convertToString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    private void acceptAction(Cases cases) {
        if (CaseStatus.ACCEPTED == cases.getStatus())
            throw validationService.badRequestExceptionThrow(CASE_ALREADY_ACCEPTED).get();
        try {
            Class<?> clazz = Class.forName(cases.getCaseType().getClassName());
            Object obj = convertToDto(cases.getJsonProperties(), clazz);
            autowireCapableBeanFactory.autowireBean(obj);

            setCaseIdIfExists(cases, obj);
            setStepChange(obj, cases);

            CaseAction action = (CaseAction) obj;
            action.accept();

            if (action.is2Step() && CaseStatus.PENDING != cases.getStatus())
                cases.setStatus(CaseStatus.PENDING);
            else
                cases.setStatus(CaseStatus.ACCEPTED);
            //update data after change
            cases.setJsonProperties(convertToString(obj));
            save(cases);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void setCaseIdIfExists(Cases cases, Object obj) {
        try {
            Field f = obj.getClass().getDeclaredField("caseId");
            f.setAccessible(true);
            f.set(obj, cases.getId());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error(e.getMessage());
        }
    }

    private void setStepChange(Object obj, Cases cases) {
        try {
            Field f = obj.getClass().getDeclaredField("step");
            f.setAccessible(true);
            int objectValue = (int) f.get(obj);
            if (objectValue == 0 && CaseStatus.PENDING != cases.getStatus()) {
                objectValue = 1;
            } else if (objectValue == 1 && CaseStatus.PENDING != cases.getStatus())
                objectValue = 0;
            else
                objectValue = 2;
            f.set(obj, objectValue);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public List<String> generateTemplate(Long caseId) {
        Cases cases = findById(caseId);
        try {
            Class<?> clazz = Class.forName(cases.getCaseType().getClassName());
            Object obj = convertToDto(cases.getJsonProperties(), clazz);
            autowireCapableBeanFactory.autowireBean(obj);
            setCaseIdIfExists(cases, obj);
            CaseAction action = (CaseAction) obj;
            return action.generateTemplate();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private Cases mapToEntity(CaseDTO dto) {
        Cases cases = new Cases();
        if (dto.getId() != null) {
            cases = findById(dto.getId());
        }
        cases.setCustomer(entityManager.find(Customer.class, dto.getCustomerId()));
        cases.setCustomerId(dto.getCustomerId());
//        cases.setAssignee(entityManager.find(User.class, dto.getAssigneeId()));
//        cases.setAssigneeId(dto.getAssigneeId());
        cases.setPriority(Optional.ofNullable(dto.getPriority()).orElse(Priority.LOW));
        cases.setStatus(Optional.ofNullable(dto.getStatus()).orElse(CaseStatus.DRAFT));
        cases.setCaseType(dto.getType());
        return cases;
    }

    @SuppressWarnings("unchecked")
    private <T> T convertToDto(String json, CaseType typeEnum) {
        try {
            return (T) objectMapper.readValue(json, Class.forName(typeEnum.getClassName()));
        } catch (JsonProcessingException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T convertToDto(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CaseDetailDTO> list(CaseFilter filter) {
        Specification<Cases> casesSpecification = getCasesSpecification(filter);

        Pageable pageable = PageRequest.of(filter.getPage(),
                filter.getSize(),
                Sort.by(filter.getOrderBy()).ascending());

        return caseRepository.findAll(casesSpecification, pageable)
                .map(this::mapToDto);
    }

    private static Specification<Cases> getCasesSpecification(CaseFilter filter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);

            ArrayList<Predicate> list = new ArrayList<>();

            Join<Cases, User> user = root.join(Cases_.CREATOR, JoinType.INNER);
            Path<Long> branchIdPath = user.get(User_.BRANCH_ID);
            if (SecurityUtils.isCurrentUserInRoles(Role.ADMIN.getRoleCode(), Role.ACCOUNTANT.getRoleCode())) {
                if (filter.getBranchId() != null) {
                    list.add(criteriaBuilder.equal(branchIdPath, filter.getBranchId()));
                }
            } else {
                list.add(criteriaBuilder.equal(branchIdPath, SecurityUtils.getBranchId()));
            }
            if (StringUtils.isNotEmpty(filter.getSearch())) {
                Join<Cases, Customer> customer = root.join(Cases_.CUSTOMER, JoinType.INNER);
                Path<String> customerName = customer.get(Customer_.NAME);
                Path<String> customerCompName = customer.get(Customer_.COMPANY_NAME);
                Path<String> tin = customer.get(Customer_.TIN);
                Path<String> json = root.get(Cases_.JSON_PROPERTIES);

                Predicate customerNamePred = criteriaBuilder.like(criteriaBuilder.lower(customerName), filter.getSearchForQuery());
                Predicate customerCompNamePred = criteriaBuilder.like(criteriaBuilder.lower(customerCompName), filter.getSearchForQuery());
                Predicate tinPred = criteriaBuilder.like(criteriaBuilder.lower(tin), filter.getSearchForQuery());
                Predicate jsonPred = criteriaBuilder.like(criteriaBuilder.lower(json), filter.getSearchForQuery());

                list.add(criteriaBuilder.or(customerNamePred, tinPred, customerCompNamePred, jsonPred));
            }
            if (filter.getFrom() != null && filter.getTo() != null) {
                Path<LocalDateTime> createDate = root.get(Cases_.CREATED_DATE);
                list.add(criteriaBuilder.between(createDate, filter.getFrom().atStartOfDay(), filter.getTo().atTime(LocalTime.MAX)));
            }
            if (filter.getId() != null) {
                list.add(criteriaBuilder.equal(root.get(Cases_.ID), filter.getId()));
            }
            if (CollectionUtils.isNotEmpty(filter.getStatuses())) {
                list.add(criteriaBuilder.in(root.get(Cases_.STATUS)).value(filter.getStatuses()));
            }
            if (isAdmin()) {
                List<CaseType> typeEnumList = caseTypeAccessRepository.findByEmployeeId(SecurityUtils.getCurrentUserId()).stream().map(CaseTypeAccess::getCaseType).collect(Collectors.toList());
                list.add(criteriaBuilder.in(root.get(Cases_.CASE_TYPE)).value(typeEnumList));
            }
            if (CollectionUtils.isNotEmpty(filter.getCaseTypes())) {
                list.add(criteriaBuilder.in(root.get(Cases_.CASE_TYPE)).value(filter.getCaseTypes()));
            }
            if (CollectionUtils.isNotEmpty(filter.getCreatorIds())) {
                list.add(criteriaBuilder.in(user.get(User_.ID)).value(filter.getCreatorIds()));
            }
            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
    }

    @Override
    public CaseUpdateDTO update(CaseUpdateDTO caseUpdateDTO) {
        Cases cases = findById(caseUpdateDTO.getId());
        if (caseUpdateDTO.getPriority() != null) {
            cases.setPriority(caseUpdateDTO.getPriority());
        }
        if (caseUpdateDTO.getCustomerId() != null) {
            cases.setCustomer(entityManager.find(Customer.class, caseUpdateDTO.getCustomerId()));
            cases.setCustomerId(caseUpdateDTO.getCustomerId());
        }
        if (caseUpdateDTO.getAssigneeId() != null) {
            cases.setAssignee(entityManager.find(User.class, caseUpdateDTO.getAssigneeId()));
            cases.setAssigneeId(caseUpdateDTO.getAssigneeId());
        }
        if (caseUpdateDTO.getCaseType() != null) {
            cases.setCaseType(caseUpdateDTO.getCaseType());
        }
        caseRepository.save(cases);
        return caseUpdateDTO;
    }

    @Override
    public void updateStatus(CaseUpdateStatusDTO caseUpdateStatusDTO) {
        if (CaseStatus.REJECTED.equals(caseUpdateStatusDTO.getStatus()) && !SecurityUtils.isCurrentUserInRoles(Role.ADMIN.getRoleCode(), Role.SUPER_ADMIN.getRoleCode()))
            throw validationService.badRequestExceptionThrow(CASE_STATUS_CHANGE_OPERATOR).get();

        Cases cases = caseRepository.findById(caseUpdateStatusDTO.getId()).orElseThrow(validationService.entityNotFoundExceptionThrow(CASE));
        if (CaseStatus.REJECTED == cases.getStatus())
            throw validationService.badRequestExceptionThrow(CASE_REJECTED, cases.getStatus().getName()).get();

        if (!(EnumSet.of(CaseType.APP_TYPE_CHANGE, CaseType.ACT_OF_RECONCILIATION).contains(cases.getCaseType()) || CaseStatus.REJECTED == caseUpdateStatusDTO.getStatus())) {
            caseFileRepository.findByCaseIdAndDocumentType(cases.getId(), DocumentType.APPLICATION)
                    .orElseThrow(validationService.entityNotFoundExceptionThrow(FILE, TYPE, DocumentType.APPLICATION.getName()));
        }

        if (CaseStatus.NEW == caseUpdateStatusDTO.getStatus()) {
            cases.setPriority(Priority.HIGH);
            if (CaseType.DEREGISTER_KKM.equals(cases.getCaseType()) && !cases.getCustomer().isWhiteList() && CaseStatus.DRAFT.equals(cases.getStatus())) {
                CaseAgreement caseAgreement = caseAgreementService.checkAgreementStatus(cases.getEntityId(), cases.getEntityType());
                if (caseAgreement != null && !AgreementStatus.ACTIVE.equals(caseAgreement.getAgreementStatus())) {
                    throw validationService.badRequestExceptionThrow(CASE_AGREEMENT_STATUS).get();
                }
            }
        }

        cases.setStatus(caseUpdateStatusDTO.getStatus());
        caseRepository.save(cases);

        CaseStatusLogDTO caseStatusLogDTO = new CaseStatusLogDTO();
        caseStatusLogDTO.setCaseId(caseUpdateStatusDTO.getId());
        caseStatusLogDTO.setStatus(caseUpdateStatusDTO.getStatus());
        caseStatusLogService.create(caseStatusLogDTO);

        createCaseNoteOnStatusChange(cases);
        this.sendNotification(cases);
    }

    @Override
    public void assign(CaseUpdateAssigneeDTO caseUpdateAssigneeDTO) {
        Cases cases = caseRepository.findById(caseUpdateAssigneeDTO.getId()).orElseThrow(validationService.entityNotFoundExceptionThrow(CASE));
        cases.setAssignee(entityManager.find(User.class, caseUpdateAssigneeDTO.getAssigneeId()));
        cases.setAssigneeId(caseUpdateAssigneeDTO.getAssigneeId());
        caseRepository.save(cases);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<CommonDTO> getAssigneeLookUp(BaseFilter filter) {
        ResultList<User> resultList = userRepository.getAssigneeLookUp(filter);
        List<CommonDTO> result = resultList.getList().stream().map(User::toCommonDTO).collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }


    @Override
    @Transactional(readOnly = true)
    public CaseDetailDTO get(Long id) {
        Cases cases = caseRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(CASE));
        return mapToDto(cases);
    }

    private CaseDetailDTO mapToDto(Cases cases) {
        CaseDetailDTO caseDetailDTO = new CaseDetailDTO();
        caseDetailDTO.setId(cases.getId());
        caseDetailDTO.setStatus(cases.getStatus());
        caseDetailDTO.setPriority(cases.getPriority());
        caseDetailDTO.setCaseType(cases.getCaseType());
        caseDetailDTO.setOperator(cases.getCreator().getAsCaseCommonDTO());
        caseDetailDTO.setCustomer(cases.getCustomer().getAsLookUpDTO());
        caseDetailDTO.setAssignee(Optional.ofNullable(cases.getAssignee()).map(User::toCommonDTO).orElse(null));
        caseDetailDTO.setCreatedDate(cases.getCreatedDate());
        caseDetailDTO.setUpdatedDate(cases.getLastModifiedDate());
        caseDetailDTO.setData(convertToDto(cases.getJsonProperties(), cases.getCaseType()));
        return caseDetailDTO;
    }

    private void sendNotification(Cases cases) {
        List<Long> employeeIds = new ArrayList<>();
        String messageRu = localizationMessage.localize(NOTIFICATION_CASE_MESSAGE,
                new Object[]{
                        cases.getStatus().getName(),
                        cases.getId().toString()
                }, Locale.of(Language.ru.getCode()));
        String messageUzLatin = localizationMessage.localize(NOTIFICATION_CASE_MESSAGE,
                new Object[]{
                        cases.getStatus().getName(),
                        cases.getId().toString()
                }, Locale.of(Language.uz_latin.getCode()));
        String messageUzCyrillic = localizationMessage.localize(NOTIFICATION_CASE_MESSAGE,
                new Object[]{
                        cases.getStatus().getName(),
                        cases.getId().toString()
                }, Locale.of(Language.uz_cyrillic.getCode()));
        switch (cases.getStatus()) {
            case NEW ->
                    employeeIds = caseTypeAccessRepository.findAllByCaseType(cases.getCaseType()).stream().map(CaseTypeAccess::getEmployeeId).collect(Collectors.toList());
            case PENDING, REJECTED, ACCEPTED, DEFECTIVE -> employeeIds.add(cases.getCreatorId());
        }
        if (CollectionUtils.isNotEmpty(employeeIds)) {
            for (Long employeeId : employeeIds) {
                rabbitMqNotificationProducer.sendToQueue(NotificationPayload
                        .builder()
                        .companyId(SecurityUtils.getCompanyId())
                        .entityId(cases.getId())
                        .entityType(EntityTypeEnum.CASES)
                        .receiverId(employeeId)
                        .messageRu(messageRu)
                        .messageUzLatin(messageUzLatin)
                        .messageUzCyrillic(messageUzCyrillic)
                        .build());
            }
        }
    }
}
