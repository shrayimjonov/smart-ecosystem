package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.config.ApplicationProperties;
import uz.uzkassa.cts.constants.OfdConstants;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.fm.FiscalizationSyncDTO;
import uz.uzkassa.cts.dto.ofd.OfdCustomerFiscalDTO;
import uz.uzkassa.cts.dto.ofd.OfdFileDTO;
import uz.uzkassa.cts.dto.ofdapplication.OfdApplicationCreateDTO;
import uz.uzkassa.cts.dto.ofdapplication.OfdApplicationMergeDTO;
import uz.uzkassa.cts.dto.ofdapplication.SwapDeviceDTO;
import uz.uzkassa.cts.dto.ofdapplication.SwapFiscalModuleDTO;
import uz.uzkassa.cts.dto.ofdapplication.admin.OfdApplicationListAdminDTO;
import uz.uzkassa.cts.dto.ofdapplication.cabinet.OfdApplicationListCabinetDTO;
import uz.uzkassa.cts.dto.payload.OfdApplicationPayload;
import uz.uzkassa.cts.dto.payload.OfdApplicationSendPayload;
import uz.uzkassa.cts.dto.payload.Payload;
import uz.uzkassa.cts.dto.tin.integration.*;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.filter.OfdApplicationFilter;
import uz.uzkassa.cts.integrations.OfdClient;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqOfdProducer;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqSmartPosProducer;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.CacheService;
import uz.uzkassa.cts.service.OfdApplicationService;
import uz.uzkassa.cts.service.ServicePaymentHistoryCronService;
import uz.uzkassa.cts.service.TinService;
import uz.uzkassa.cts.utils.DateUtil;
import uz.uzkassa.cts.utils.FileUtils;
import uz.uzkassa.cts.utils.Utils;
import uz.uzkassa.cts.validator.LocalizationMessage;
import uz.uzkassa.cts.validator.ValidationContext;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@Slf4j
public class OfdApplicationServiceImpl extends BaseServiceImpl<OfdApplication, Long> implements OfdApplicationService {

    private final OfdApplicationRepository ofdApplicationRepository;
    private final CustomerBranchRepository customerBranchRepository;
    private final CustomerBranchDeviceRepository customerBranchDeviceRepository;
    private final CustomerRepository customerRepository;
    private final FiscalizationRepository fiscalizationRepository;
    private final FileUploadRepository fileUploadRepository;
    private final DeviceModelRepository deviceModelRepository;
    private final DeviceRepository deviceRepository;
    private final FiscalModuleRepository fiscalModuleRepository;
    private final OfdSyncHistoryRepository ofdSyncHistoryRepository;
    private final TinService tinService;
    private final ApplicationProperties applicationProperties;
    private final RabbitMqOfdProducer rabbitMqOfdProducer;
    private final RabbitMqSmartPosProducer rabbitMqSmartPosProducer;
    private final OfdClient ofdClient;
    private final ServicePaymentHistoryCronService servicePaymentHistoryCronService;
    private final CacheService cacheService;
    private final ValidationContext validationContext;
    private final LocalizationMessage localizationMessage;

    public OfdApplicationServiceImpl(OfdApplicationRepository ofdApplicationRepository,
                                     CustomerBranchRepository customerBranchRepository,
                                     CustomerBranchDeviceRepository customerBranchDeviceRepository, FiscalizationRepository fiscalizationRepository,
                                     FileUploadRepository fileUploadRepository,
                                     OfdSyncHistoryRepository ofdSyncHistoryRepository,
                                     CustomerRepository customerRepository,
                                     TinService tinService,
                                     ApplicationProperties applicationProperties,
                                     DeviceModelRepository deviceModelRepository,
                                     DeviceRepository deviceRepository,
                                     FiscalModuleRepository fiscalModuleRepository,
                                     RabbitMqOfdProducer rabbitMqOfdProducer, RabbitMqSmartPosProducer rabbitMqSmartPosProducer,
                                     OfdClient ofdClient, ServicePaymentHistoryCronService servicePaymentHistoryCronService, CacheService cacheService, ValidationContext validationContext, LocalizationMessage localizationMessage) {
        super(ofdApplicationRepository);
        this.ofdApplicationRepository = ofdApplicationRepository;
        this.customerBranchRepository = customerBranchRepository;
        this.customerBranchDeviceRepository = customerBranchDeviceRepository;
        this.fiscalizationRepository = fiscalizationRepository;
        this.fileUploadRepository = fileUploadRepository;
        this.ofdSyncHistoryRepository = ofdSyncHistoryRepository;
        this.customerRepository = customerRepository;
        this.tinService = tinService;
        this.applicationProperties = applicationProperties;
        this.deviceRepository = deviceRepository;
        this.fiscalModuleRepository = fiscalModuleRepository;
        this.rabbitMqOfdProducer = rabbitMqOfdProducer;
        this.deviceModelRepository = deviceModelRepository;
        this.rabbitMqSmartPosProducer = rabbitMqSmartPosProducer;
        this.ofdClient = ofdClient;
        this.servicePaymentHistoryCronService = servicePaymentHistoryCronService;
        this.cacheService = cacheService;
        this.validationContext = validationContext;
        this.localizationMessage = localizationMessage;
    }

    @Override
    @Transactional
    public void create(OfdApplicationCreateDTO ofdApplicationCreateDTO) {
        validationContext.validate(ofdApplicationCreateDTO);
        Fiscalization fiscalization = fiscalizationRepository.findFirstByCustomerIdAndDeviceIdAndFiscalModuleId(ofdApplicationCreateDTO.getCustomerId(), ofdApplicationCreateDTO.getDeviceId(), ofdApplicationCreateDTO.getFiscalModuleId());
        OfdApplication application = new OfdApplication();
        application.setOfdStatus(OfdStatus.ACCEPT);
        application.setCustomerId(ofdApplicationCreateDTO.getCustomerId());
        application.setDeviceId(ofdApplicationCreateDTO.getDeviceId());
        application.setFiscalModuleId(ofdApplicationCreateDTO.getFiscalModuleId());
        application.setCustomerBranchId(fiscalization.getCustomerBranchId());
        application.setApplicationType(ApplicationType.REGISTER);
        ofdApplicationRepository.save(application);

        fiscalization.setOfdApplicationId(application.getId());
        fiscalizationRepository.save(fiscalization);
    }

    @Override
    public Page<OfdApplicationListAdminDTO> getListForAdmin(OfdApplicationFilter filter) {
        ResultList<OfdApplication> resultList = ofdApplicationRepository.getResultList(filter);
        List<OfdApplicationListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(ofdApplication -> {
                    OfdApplicationListAdminDTO ofdApplicationListDTO = new OfdApplicationListAdminDTO();
                    ofdApplicationListDTO.setId(ofdApplication.getId());
                    ofdApplicationListDTO.setOfdId(ofdApplication.getOfdId());
                    ofdApplicationListDTO.setCustomer(ofdApplication.getCustomer().toShortDTO());
                    ofdApplicationListDTO.setCustomerBranch(ofdApplication.getCustomerBranch().toCommonDTO());
                    ofdApplicationListDTO.setBranch(ofdApplication.getCustomer().getBranch().toCommonDTO());
                    ofdApplicationListDTO.setDevice(ofdApplication.getDevice().toCommonDTO());
                    ofdApplicationListDTO.setFiscalModule(ofdApplication.getFiscalModule().toCommonDTO());
                    ofdApplicationListDTO.setOfdStatus(ofdApplication.getOfdStatus());
                    ofdApplicationListDTO.setCreatedDate(ofdApplication.getCreatedDate());
                    ofdApplicationListDTO.setType(ofdApplication.getApplicationType());
                    ofdApplicationListDTO.setReason(ofdApplication.getReason());
                    ofdApplicationListDTO.setOfdFileUrl(ofdApplication.getApplicationFileUrl());
                    return ofdApplicationListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public Page<OfdApplicationListCabinetDTO> getListForCabinet(OfdApplicationFilter filter) {
        if (SecurityUtils.isCurrentUserInRole(Role.CALL_CENTER.getRoleCode()) && StringUtils.isEmpty(filter.getSearch())) {
            List<OfdApplicationListCabinetDTO> result = new ArrayList<>();
            return new PageImpl<>(result, filter.getOrderedPageable(), 0);
        }
        if (SecurityUtils.isCurrentUserInRole(Role.OPERATOR.getRoleCode())) {
            if (StringUtils.isEmpty(filter.getSearch())) {
                filter.setBranchId(SecurityUtils.getBranchId());
            } else {
                filter.setCustomerIds(customerRepository.getIdsByBranchId(SecurityUtils.getBranchId()));
            }
        }
        ResultList<OfdApplication> resultList = ofdApplicationRepository.getResultList(filter);
        List<OfdApplicationListCabinetDTO> result = resultList
                .getList()
                .stream()
                .map(ofdApplication -> {
                    OfdApplicationListCabinetDTO ofdApplicationListDTO = new OfdApplicationListCabinetDTO();
                    ofdApplicationListDTO.setId(ofdApplication.getId());
                    ofdApplicationListDTO.setOfdId(ofdApplication.getOfdId());
                    ofdApplicationListDTO.setCustomer(ofdApplication.getCustomer().toShortDTO());
                    ofdApplicationListDTO.setCustomerBranch(ofdApplication.getCustomerBranch().toCommonDTO());
                    ofdApplicationListDTO.setBranch(ofdApplication.getCustomer().getBranch().toCommonDTO());
                    ofdApplicationListDTO.setDevice(ofdApplication.getDevice().toCommonDTO());
                    ofdApplicationListDTO.setFiscalModule(ofdApplication.getFiscalModule().toCommonDTO());
                    ofdApplicationListDTO.setOfdStatus(ofdApplication.getOfdStatus());
                    ofdApplicationListDTO.setCreatedDate(ofdApplication.getCreatedDate());
                    ofdApplicationListDTO.setType(ofdApplication.getApplicationType());
                    ofdApplicationListDTO.setOfdFileUrl(ofdApplication.getApplicationFileUrl());
                    ofdApplicationListDTO.setReason(ofdApplication.getReason());
                    return ofdApplicationListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public void sync() {
        ofdApplicationRepository.findAllByOfdIdsByStatusNot(OfdStatus.ACCEPT).forEach(ofdId -> rabbitMqOfdProducer.batchSyncApplication(new Payload(ofdId)));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Fiscalization fiscalization = fiscalizationRepository.findFirstByOfdApplicationId(id);
        if (fiscalization != null) {
            fiscalization.setOfdApplicationId(null);
            fiscalization.setOfdApplication(null);
            fiscalizationRepository.save(fiscalization);
            cacheService.evictFiscalCache(fiscalization.getHashKey());
        }
        super.deleteById(id);
    }

    @Override
    @Transactional
    public void sync(Long customerId) {
        customerRepository.findById(customerId).ifPresentOrElse(customer -> {
            syncByCustomer(customer.getTin(), customerId);
            if (customer.getOldTin() != null && !customer.getTin().equals(customer.getOldTin())) {
                syncByCustomer(customer.getOldTin(), customerId);
            }
        }, () -> validationService.entityNotFoundExceptionThrow(CUSTOMER).get());
    }

    private void syncByCustomer(String tin, Long customerId) {
        log.info("Start application sync by customer:{}", customerId);
        String baseUrl = applicationProperties.getOfdProperty().getCustomerFiscalListUrl().concat("?page=1&size=10000000");
        String apiUrl;
        if (Utils.isTin(tin)) {
            apiUrl = baseUrl.concat("&tin=").concat(tin);
        } else {
            apiUrl = baseUrl.concat("&pinfl=").concat(tin);
        }
        OfdApplicationPayload ofdApplicationPayload = new OfdApplicationPayload();
        ofdApplicationPayload.setApiUrl(apiUrl);
        ofdApplicationPayload.setTin(tin);
        ofdApplicationPayload.setCustomerId(customerId);
        syncApplication(ofdApplicationPayload);

        log.info("End application sync by customer:{}", customerId);
    }


    @Transactional
    @Override
    public void batchSyncApplication(Payload payload) {
        OfdApplicationListResponseDTO result = ofdClient.batchSyncApplication(payload.getId());
        if (result.isSuccess() && CollectionUtils.isNotEmpty(result.getData())) {
            OfdApplicationDTO ofdApplicationDTO = result.getData().getFirst();
            ofdApplicationRepository.findFirstByOfdIdOrderByOfdIdDesc(ofdApplicationDTO.getId()).ifPresent(ofdApplication -> {
                if (ofdApplicationDTO.getStatus() != null) {
                    ofdApplication.setOfdStatus(OfdStatus.getById(ofdApplicationDTO.getStatus()));
                    ofdApplicationRepository.save(ofdApplication);
                }
            });
        }
    }

    @Transactional
    @Override
    public void syncApplication(OfdApplicationPayload ofdApplicationPayload) {
        OfdApplicationListResponseDTO result = ofdClient.syncApplication(ofdApplicationPayload);
        if (result.isSuccess() && result.getData() != null && !result.getData().isEmpty()) {

            List<OfdApplicationDTO> sortedList = result.getData();
            //Sort by id
            sortedList.sort(Comparator.comparing(OfdApplicationDTO::getId));

            for (OfdApplicationDTO ofdApplicationDTO : sortedList) {

                log.info("----------------------------  Start Cron ------------------------");
                log.info("ID: {}", ofdApplicationDTO.getId());
                log.info("KKM: {}", ofdApplicationDTO.getSerialNumber());
                log.info("FM: {}", ofdApplicationDTO.getTerminalId());
                log.info("Status: {}", ofdApplicationDTO.getStatus());

                OfdApplication ofdApplication = ofdApplicationRepository.findFirstByOfdIdOrderByOfdIdDesc(ofdApplicationDTO.getId()).orElse(null);
                if (ofdApplication == null) {
                    continue;
                }

                Fiscalization fiscalization = fiscalizationRepository.findFirstByOfdApplicationId(ofdApplication.getId());
                if (fiscalization == null) {
                    continue;
                }

                String createdDateString = ofdApplicationDTO.getCreatedDate().substring(0, ofdApplicationDTO.getCreatedDate().lastIndexOf("."));
                ofdApplication.setCreatedDate(LocalDateTime.parse(createdDateString, DateUtil.LONG_FORMATTER));

                ofdApplication.setOfdId(ofdApplicationDTO.getId());
                if (ofdApplicationDTO.getStatus() != null) {
                    ofdApplication.setOfdStatus(OfdStatus.getById(ofdApplicationDTO.getStatus()));
                }
                if (StringUtils.isEmpty(ofdApplication.getApplicationFileUrl())) {
                    ofdApplication.setApplicationFileUrl(ofdApplicationDTO.getRegistrationCardUrl());
                }
                ofdApplication.setReason(ofdApplicationDTO.getAnswerDescription());

                ofdApplication.setOfficeId(ofdApplicationDTO.getOfficeId());
                ofdApplication.setOfficeFileId(ofdApplicationDTO.getOfficeFileId());

                ofdApplication.setContractFileId(ofdApplicationDTO.getContractFileId());
                ofdApplication.setApplicationFileId(ofdApplicationDTO.getApplicationFileId());

                ofdApplicationRepository.save(ofdApplication);

                /*sync customer with smartPos */
                rabbitMqSmartPosProducer.syncCustomer(new FiscalizationSyncDTO(fiscalization.getId()));
            }
        } else {
            log.info("Empty Sync Application By Customer: {}", ofdApplicationPayload.getCustomerId());
        }
    }

    @Override
    @Transactional
    public void sendApplication(OfdApplicationSendPayload ofdApplicationSendPayload) {
        Fiscalization fiscalization = fiscalizationRepository.findById(ofdApplicationSendPayload.getFiscalizationId()).orElse(null);
        if (fiscalization == null) {
            log.error(localizationMessage.localize(ENTITY_NOT_FOUND_WITH, new Object[]{localizationMessage.localize(FISCALIZATION), ID, ofdApplicationSendPayload.getFiscalizationId()}));
            return;
        }
        if (fiscalization.getOfdApplicationId() != null) {
            log.error("Application has already been sent to {}", fiscalization.getCustomer().getTin());
            return;
        }

        if (fiscalization.getDevice().getModel().getOfdId() == null || fiscalization.getCustomerBranch().getOfdId() == null) {
            log.error("One of required field is null");
            return;
        }

        //Application File
        FileUpload applicationFile = fileUploadRepository.findFirstByDocumentTypeAndEntityTypeAndEntityId(DocumentType.APPLICATION, EntityTypeEnum.FISCALIZATION, fiscalization.getId());

        if (applicationFile == null) {
            log.error("Application file is null");
            return;
        }

        //Check client balance
        OfdCustomerBalanceResponseDTO customerBalanceResponseDTO;
        try {
            customerBalanceResponseDTO = tinService.getCustomerBalance(null, fiscalization.getCustomer().getTin());
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error("Error occurred while check customer balance {}", fiscalization.getCustomer().getTin());
            return;
        }
        if (customerBalanceResponseDTO == null
                || !customerBalanceResponseDTO.isSuccess()
                || customerBalanceResponseDTO.getData() == null
                || customerBalanceResponseDTO.getData().getOverpaymentSum().compareTo(BigDecimal.ZERO) == 0
                || customerBalanceResponseDTO.getData().getOverpaymentSum().intValue() == 0) {
            log.error("Ҳисобда маблағ етарли эмас");
            return;
        }

        OfdCustomerFiscalDTO ofdCustomerFiscalDTO = new OfdCustomerFiscalDTO();
        ofdCustomerFiscalDTO.setCcmCategoryId(fiscalization.getDevice().getModel().getOfdId());
        ofdCustomerFiscalDTO.setCcmSerialNumber(fiscalization.getDevice().getSerialNumber());
        ofdCustomerFiscalDTO.setOfficeId(fiscalization.getCustomerBranch().getOfdId());
        ofdCustomerFiscalDTO.setPhone(fiscalization.getCustomer().getPhone());
        ofdCustomerFiscalDTO.setTerminalId(fiscalization.getFiscalModule().getSerialNumber());
        if (Utils.isTin(fiscalization.getCustomer().getTin())) {
            ofdCustomerFiscalDTO.setTin(fiscalization.getCustomer().getTin());
        } else {
            ofdCustomerFiscalDTO.setPinfl(fiscalization.getCustomer().getTin());
        }

        //Contract File
        List<OfdFileDTO> contactFiles = new ArrayList<>();
        contactFiles.add(new OfdFileDTO(ofdApplicationSendPayload.getContractOfferName(), ofdApplicationSendPayload.getContractOfferBase64()));
        ofdCustomerFiscalDTO.setContractFile(contactFiles);

        //Application File
        List<OfdFileDTO> applicationFiles = new ArrayList<>();
        try {
            applicationFiles.add(new OfdFileDTO(applicationFile.getFileName(), FileUtils.fileToBase64(applicationFile.getDownloadUrl())));
        } catch (Exception e) {
            log.error("An unexpected error has occurred while reading file from storage: {}", e.getMessage());
            log.error("File url:{}", applicationFile.getDownloadUrl());

            String error = localizationMessage.localize(FILE_PARSE_FAILED, new Object[]{applicationFile.getDownloadUrl()}) +
                    " Customer: " + fiscalization.getCustomer().getTin() +
                    " TT: " + fiscalization.getCustomerBranch().getName() +
                    " File: " + applicationFile.getDownloadUrl();
            throw validationService.badRequestExceptionThrow(error).get();
        }
        ofdCustomerFiscalDTO.setApplicationFile(applicationFiles);

        OfdApplicationSendResponseDTO result = ofdClient.sendApplication(ofdCustomerFiscalDTO);
        if (result.isSuccess() && result.getData() != null && result.getData().getId() != null) {

            OfdApplication ofdApplication = new OfdApplication();
            ofdApplication.setOfdId(result.getData().getId());
            ofdApplication.setOfdStatus(OfdStatus.PENDING);
            ofdApplication.setApplicationFileUrl(result.getData().getFileUrl());
            ofdApplication.setCreatedDate(LocalDateTime.now());
            ofdApplication.setCustomerId(fiscalization.getCustomerId());
            ofdApplication.setCustomerBranchId(fiscalization.getCustomerBranchId());
            ofdApplication.setDeviceId(fiscalization.getDeviceId());
            ofdApplication.setFiscalModuleId(fiscalization.getFiscalModuleId());
            ofdApplication.setApplicationType(ApplicationType.REGISTER);
            ofdApplicationRepository.save(ofdApplication);

            fiscalization.setOfdApplicationId(ofdApplication.getId());
            fiscalizationRepository.save(fiscalization);
            cacheService.evictFiscalCache(fiscalization.getHashKey());
        } else {
            if (CollectionUtils.isNotEmpty(result.getErrors())) {
                result.getErrors().forEach(errorDTO -> {
                    OfdSyncHistory syncHistory = new OfdSyncHistory();
                    syncHistory.setSyncType(OfdSyncType.CUSTOMER_FISCAL);
                    syncHistory.setCustomerId(fiscalization.getCustomer().getId());
                    syncHistory.setCustomerBranchId(fiscalization.getCustomerBranch().getId());
                    syncHistory.setMessage(errorDTO.getMessage());
                    syncHistory.setField(errorDTO.getField());
                    syncHistory.setErrorCode(result.getCode());
                    ofdSyncHistoryRepository.save(syncHistory);
                });
            } else {
                OfdSyncHistory syncHistory = new OfdSyncHistory();
                syncHistory.setSyncType(OfdSyncType.CUSTOMER_FISCAL);
                syncHistory.setCustomerId(fiscalization.getCustomer().getId());
                syncHistory.setCustomerBranchId(fiscalization.getCustomerBranch().getId());
                syncHistory.setMessage(result.getText() != null ? result.getText() : "Ошибка проверки");
                syncHistory.setErrorCode(result.getCode());
                ofdSyncHistoryRepository.save(syncHistory);
            }
        }
    }

    @Override
    public List<OfdApplicationListDTO> getListByFiscalizationId(Long fiscalizationId) {
        return fiscalizationRepository.findById(fiscalizationId).map(fiscalization -> {
            Customer customer = fiscalization.getCustomer();
            List<OfdApplicationListDTO> result = new ArrayList<>();
            if (Utils.isTin(customer.getTin())) {
                List<OfdApplicationListDTO> tinResult = listByFiscalization(customer.getTin(), fiscalizationId);
                if (CollectionUtils.isNotEmpty(tinResult)) {
                    result.addAll(tinResult);
                }
            } else {
                List<OfdApplicationListDTO> pinflResult = listByFiscalization(customer.getTin(), fiscalizationId);
                if (CollectionUtils.isNotEmpty(pinflResult)) {
                    result.addAll(pinflResult);
                }
                if (customer.getOldTin() != null && !customer.getTin().equals(customer.getOldTin())) {
                    List<OfdApplicationListDTO> oldTinResult = listByFiscalization(customer.getOldTin(), fiscalizationId);
                    if (CollectionUtils.isNotEmpty(oldTinResult)) {
                        result.addAll(oldTinResult);
                    }
                }
            }
            return result;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(FISCALIZATION));
    }

    private List<OfdApplicationListDTO> listByFiscalization(String tin, Long fiscalizationId) {
        List<OfdApplicationListDTO> resultList = new ArrayList<>();
        OfdApplicationListResponseDTO result = ofdClient.getApplicationListByTin(tin);
        if (result != null && result.isSuccess() && CollectionUtils.isNotEmpty(result.getData())) {

            log.info("Response ofd application merge: {}", result.getData());
            List<OfdApplicationDTO> sortedList = result.getData();
            sortedList.sort(Comparator.comparing(OfdApplicationDTO::getId));

            sortedList.forEach(ofdApplicationDTO -> {
                OfdApplicationListDTO item = new OfdApplicationListDTO();
                item.setOfdId(ofdApplicationDTO.getId());

                CustomerBranch customerBranch = customerBranchRepository.findFirstByOfdId(ofdApplicationDTO.getOfficeId());
                if (customerBranch != null) {
                    item.setCustomerBranch(customerBranch.toCommonDTO());
                }


                OfdApplication ofdApplication = ofdApplicationRepository.findFirstByOfdIdOrderByOfdIdDesc(ofdApplicationDTO.getId()).orElse(null);
                if (ofdApplication != null) {
                    Fiscalization fiscalization1 = fiscalizationRepository.findFirstByOfdApplicationId(ofdApplication.getId());
                    if (fiscalization1 != null) {
                        item.setFiscalizationId(fiscalization1.getId());
                    }
                }
                if (item.getFiscalizationId() == null && StringUtils.isNotEmpty(ofdApplicationDTO.getSerialNumber()) && StringUtils.isNotEmpty(ofdApplicationDTO.getTerminalId())) {
                    Customer customer = customerRepository.findFirstByTinOrOldTin(ofdApplicationDTO.getPinfl(), ofdApplicationDTO.getTin());
                    if (customer == null) {
                        customer = customerRepository.findByTin(ofdApplicationDTO.getTin());
                    }

                    Fiscalization fiscalization1 = fiscalizationRepository.findFirstByCustomerIdAndDeviceIdAndFiscalModuleId(customer.getId(), deviceRepository.findBySerialNumber(ofdApplicationDTO.getSerialNumber()).getId(), fiscalModuleRepository.findBySerialNumber(ofdApplicationDTO.getTerminalId()).getId());
                    if (fiscalization1 != null) {
                        item.setFiscalizationId(fiscalization1.getId());
                    }
                }

                if (item.getFiscalizationId() == null) {
                    item.setFiscalizationId(fiscalizationId);
                }

                item.setDeviceSerial(ofdApplicationDTO.getSerialNumber());
                item.setFiscalModuleSerial(ofdApplicationDTO.getTerminalId());
                if (ofdApplicationDTO.getStatus() != null) {
                    item.setOfdStatus(OfdStatus.getById(ofdApplicationDTO.getStatus()));
                }
                item.setApplicationFileUrl(ofdApplicationDTO.getRegistrationCardUrl());
                item.setTin(Optional.ofNullable(ofdApplicationDTO.getPinfl()).orElse(ofdApplicationDTO.getTin()));
                item.setApplicationType(ApplicationType.getById(ofdApplicationDTO.getApplicationTypeCode()));
                resultList.add(item);
            });
        }
        return resultList;
    }

    @Override
    public Page<OfdApplicationMakeObjectListDTO> getApplications(OfdApplicationFilter filter) {
        if (SecurityUtils.isCurrentUserInRole(Role.CALL_CENTER.getRoleCode()) && StringUtils.isEmpty(filter.getSearch())) {
            List<OfdApplicationMakeObjectListDTO> result = new ArrayList<>();
            return new PageImpl<>(result, filter.getOrderedPageable(), 0);
        }

        ResultList<OfdApplicationMakeObjectListDTO> resultList = ofdApplicationRepository.getApplications(filter);
        return new PageImpl<>(resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    @Transactional
    public OfdApplication reject(MultipartFile file, Long id) {
        OfdApplication ofdApplication = ofdApplicationRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(APPLICATION));
        OfdApplication registerOfdApplication = null;
        if (ApplicationType.CHANGE_ADDRESS.equals(ofdApplication.getApplicationType())) {
            registerOfdApplication = ofdApplicationRepository.findFirstByCustomerIdAndCustomerBranchIdAndDeviceIdAndApplicationType(ofdApplication.getCustomerId(),
                    ofdApplication.getCustomerBranchId(), ofdApplication.getDeviceId(), ApplicationType.REGISTER).orElseThrow(validationService.entityNotFoundExceptionThrow(APPLICATION));
        }
        String base64;
        try {
            base64 = FileUtils.encodeFileToBase64(file.getBytes());
        } catch (IOException e) {
            log.error(e.getMessage());
            log.error("Application file parse error:{}", e.getMessage());
            String error = localizationMessage.localize(FILE_PARSE_FAILED, new Object[]{file.getOriginalFilename()}) +
                    " Customer: " + ofdApplication.getCustomer().getTin() +
                    " TT: " + ofdApplication.getCustomerBranch().getName() +
                    " File: " + file.getOriginalFilename();
            throw validationService.badRequestExceptionThrow(error).get();
        }
        if (base64 == null) {
            log.error("Application file parse error");
            throw validationService.badRequestExceptionThrow(FILE_PARSE_FAILED, file.getOriginalFilename()).get();
        }

        List<OfdFileDTO> applicationFiles = new ArrayList<>();
        applicationFiles.add(new OfdFileDTO(file.getOriginalFilename(), base64));

        OfdApplicationRejectDTO ofdApplicationRejectDTO = new OfdApplicationRejectDTO();
        ofdApplicationRejectDTO.setApplicationId(Objects.requireNonNullElse(registerOfdApplication, ofdApplication).getOfdId());
        ofdApplicationRejectDTO.setApplicationFile(applicationFiles);
        ofdApplicationRejectDTO.setReasonId(OfdConstants.REJECT_APPLICATION);

        OfdApplicationRejectResponseDTO result = ofdClient.rejectApplication(ofdApplicationRejectDTO);
        if (result.isSuccess() && result.getData() != null) {
            OfdApplication newApplication = new OfdApplication();
            newApplication.setOfdId(result.getData());
            newApplication.setOfdStatus(OfdStatus.PENDING);
            newApplication.setCustomerId(ofdApplication.getCustomerId());
            newApplication.setCustomer(ofdApplication.getCustomer());
            newApplication.setCustomerBranchId(ofdApplication.getCustomerBranchId());
            newApplication.setCustomerBranch(ofdApplication.getCustomerBranch());
            newApplication.setDeviceId(ofdApplication.getDeviceId());
            newApplication.setDevice(ofdApplication.getDevice());
            newApplication.setFiscalModuleId(ofdApplication.getFiscalModuleId());
            newApplication.setFiscalModule(ofdApplication.getFiscalModule());
            newApplication.setApplicationType(ApplicationType.DEREGISTER);
            newApplication.setCreatedDate(LocalDateTime.now());

            ofdApplicationRepository.save(newApplication);

            Fiscalization fiscalization = fiscalizationRepository.findFirstByOfdApplicationId(ofdApplication.getId());
            fiscalization.setOfdApplicationId(newApplication.getId());
            fiscalizationRepository.save(fiscalization);

            ofdApplication.setOfdStatus(OfdStatus.REJECT);
            ofdApplicationRepository.save(ofdApplication);
            cacheService.evictFiscalCache(fiscalization.getHashKey());
            return newApplication;
        } else {
            StringBuilder errorBuilder = new StringBuilder();
            if (CollectionUtils.isNotEmpty(result.getErrors())) {
                result.getErrors().forEach(ofdErrorDTO -> errorBuilder.append(ofdErrorDTO.getField()).append(" : ").append(ofdErrorDTO.getMessage()).append("\n"));
            } else {
                if (StringUtils.isNotEmpty(result.getText())) {
                    errorBuilder.append(result.getText());
                } else {
                    errorBuilder.append("Ошибка проверки");
                }
            }
            throw validationService.badRequestExceptionThrow(errorBuilder.toString()).get();
        }
    }

    @Override
    @Transactional
    public Long swapFiscalModule(SwapFiscalModuleDTO swapFiscalModuleDTO) {
        OfdApplication ofdApplication = ofdApplicationRepository.findById(swapFiscalModuleDTO.getApplicationId()).orElseThrow(validationService.entityNotFoundExceptionThrow(APPLICATION));

        OfdApplication registerOfdApplication = null;
        if (ApplicationType.CHANGE_ADDRESS.equals(ofdApplication.getApplicationType())) {
            registerOfdApplication = ofdApplicationRepository.findFirstByCustomerIdAndCustomerBranchIdAndDeviceIdAndApplicationType(ofdApplication.getCustomerId(),
                    ofdApplication.getCustomerBranchId(), ofdApplication.getDeviceId(), ApplicationType.REGISTER).orElseThrow(validationService.entityNotFoundExceptionThrow(APPLICATION));
        }

        Long ofdId = registerOfdApplication != null ? registerOfdApplication.getOfdId() : ofdApplication.getOfdId();

        Long ccmCategoryId = deviceModelRepository.findFirstByCode(SecurityUtils.getModelCode(ofdApplication.getDevice().getSerialNumber())).getOfdId();

        OfdSwapFiscalModuleRequestDTO ofdSwapFiscalModuleRequestDTO = new OfdSwapFiscalModuleRequestDTO();
        ofdSwapFiscalModuleRequestDTO.setApplicationId(ofdId);
        ofdSwapFiscalModuleRequestDTO.setCcmCategoryId(ccmCategoryId);
        ofdSwapFiscalModuleRequestDTO.setCcmSerialNumber(ofdApplication.getDevice().getSerialNumber());
        ofdSwapFiscalModuleRequestDTO.setReasonId(OfdConstants.SWAP_FM);
        ofdSwapFiscalModuleRequestDTO.setTerminalStatusChangeId(String.valueOf(swapFiscalModuleDTO.getReason().getNumber()));
        ofdSwapFiscalModuleRequestDTO.setTerminalId(swapFiscalModuleDTO.getFiscalModuleSerial());
        if (Reason.BURNT_OUT.equals(swapFiscalModuleDTO.getReason()) ||
                Reason.LOST.equals(swapFiscalModuleDTO.getReason()) ||
                Reason.MEMORY_IS_FULL.equals(swapFiscalModuleDTO.getReason())) {
            ofdSwapFiscalModuleRequestDTO.setReason(swapFiscalModuleDTO.getReason().getName());
        }

        log.info("Swap Fiscal Module request : {}", ofdSwapFiscalModuleRequestDTO);
        OfdSwapFiscalModuleResponseDTO result = ofdClient.swapFiscalModule(ofdSwapFiscalModuleRequestDTO);
        if (result.isSuccess() && result.getData() != null) {
            log.info("Swap Fiscal Module response : {}", result);
            ofdApplication.setApplicationFileUrl(result.getData());
            ofdApplication.setApplicationType(ApplicationType.DEREGISTER_FISCAL_MODULE);
            ofdApplication.setSwapReason(swapFiscalModuleDTO.getReason());
            if (StringUtils.isNotEmpty(swapFiscalModuleDTO.getReasonText())) {
                ofdApplication.setSwapReasonText(swapFiscalModuleDTO.getReasonText());
            } else {
                ofdApplication.setSwapReasonText(swapFiscalModuleDTO.getReason().getName());
            }
            ofdApplicationRepository.save(ofdApplication);
            return ofdApplication.getId();
        } else {
            StringBuilder errorBuilder = new StringBuilder();
            if (CollectionUtils.isNotEmpty(result.getErrors())) {
                result.getErrors().forEach(ofdErrorDTO -> errorBuilder.append(ofdErrorDTO.getField()).append(" : ").append(ofdErrorDTO.getMessage()).append("\n"));
            } else {
                if (StringUtils.isNotEmpty(result.getText())) {
                    errorBuilder.append(result.getText());
                } else {
                    errorBuilder.append("Ошибка НИЦ");
                }
            }
            throw validationService.badRequestExceptionThrow(errorBuilder.toString()).get();
        }
    }

    @Override
    @Transactional
    public String swapDevice(SwapDeviceDTO swapDeviceDTO) {
        OfdApplication ofdApplication = ofdApplicationRepository.findById(swapDeviceDTO.getApplicationId()).orElseThrow(validationService.entityNotFoundExceptionThrow(APPLICATION));
        if (ApplicationType.REGISTER.equals(ofdApplication.getApplicationType()) && OfdStatus.PENDING.equals(ofdApplication.getOfdStatus())) {
            throw validationService.badRequestExceptionThrow(ENTITY_NOT_FOUND, "Cash").get();
        }
        if (customerBranchDeviceRepository.existsByDevice_SerialNumberAndDeletedIsFalse(swapDeviceDTO.getDeviceSerial())) {
            throw validationService.entityAlreadyExistExceptionThrow(DEVICE).get();
        }

        Long ccmCategoryId = deviceModelRepository.findFirstByCode(SecurityUtils.getModelCode(swapDeviceDTO.getDeviceSerial())).getOfdId();

        OfdSwapDeviceRequestDTO ofdSwapDeviceRequestDTO = new OfdSwapDeviceRequestDTO();
        ofdSwapDeviceRequestDTO.setApplicationId(ofdApplication.getOfdId());
        ofdSwapDeviceRequestDTO.setCcmCategoryId(ccmCategoryId);
        ofdSwapDeviceRequestDTO.setReasonId(OfdConstants.SWAP_CCM);
        ofdSwapDeviceRequestDTO.setCcmSerialNumber(swapDeviceDTO.getDeviceSerial());
        ofdSwapDeviceRequestDTO.setTerminalStatusChangeId(String.valueOf(Reason.OTHER.getNumber()));

        OfdSwapDeviceResponseDTO result = ofdClient.swapDevice(ofdSwapDeviceRequestDTO);
        if (result.isSuccess() && result.getData() != null) {
            ofdApplication.setApplicationFileUrl(result.getData());
            ofdApplication.setApplicationType(ApplicationType.DEREGISTER_DEVICE);
            ofdApplicationRepository.save(ofdApplication);
            return result.getText();
        } else {
            StringBuilder errorBuilder = new StringBuilder();
            if (CollectionUtils.isNotEmpty(result.getErrors())) {
                result.getErrors().forEach(ofdErrorDTO -> errorBuilder.append(ofdErrorDTO.getField()).append(" : ").append(ofdErrorDTO.getMessage()).append("\n"));
            } else {
                if (StringUtils.isNotEmpty(result.getText())) {
                    errorBuilder.append(result.getText());
                } else {
                    errorBuilder.append("Ошибка НИЦ");
                }
            }
            throw validationService.badRequestExceptionThrow(errorBuilder.toString()).get();
        }
    }

    @Override
    @Transactional
    public String changeAddress(Long id) {
        OfdApplication ofdApplication = ofdApplicationRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(APPLICATION));

        if (!ofdApplication.getOfdStatus().equals(OfdStatus.ACCEPT) && ofdApplication.getApplicationType().equals(ApplicationType.REGISTER)) {
            throw validationService.badRequestExceptionThrow(CHANGE_ADDRESS).get();
        }

        Fiscalization fiscalization = fiscalizationRepository.findFirstByOfdApplicationId(ofdApplication.getId());
        if (fiscalization == null) {
            throw validationService.entityNotFoundExceptionThrow(FISCALIZATION).get();
        }

        OfdChangeAddressRequestDTO ofdChangeAddressRequestDTO = new OfdChangeAddressRequestDTO();
        ofdChangeAddressRequestDTO.setApplicationId(ofdApplication.getOfdId());
        ofdChangeAddressRequestDTO.setRegionCode(ofdApplication.getCustomerBranch().getAddress().getRegion().getCode());
        ofdChangeAddressRequestDTO.setDistrictCode(ofdApplication.getCustomerBranch().getAddress().getDistrict().getCode());
        ofdChangeAddressRequestDTO.setNeighborhoodCode(ofdApplication.getCustomerBranch().getAddress().getNeighborhood().getCode());
        ofdChangeAddressRequestDTO.setAddress(ofdApplication.getCustomerBranch().getAddress().getAddress());
        ofdChangeAddressRequestDTO.setLongitude(ofdApplication.getCustomerBranch().getAddress().getLongitude());
        ofdChangeAddressRequestDTO.setLatitude(ofdApplication.getCustomerBranch().getAddress().getLatitude());
        ofdChangeAddressRequestDTO.setOfficeId(ofdApplication.getCustomerBranch().getOfdId());

        OfdChangeAddressResponseDTO result = ofdClient.changeAddress(ofdChangeAddressRequestDTO);
        if (result.isSuccess() && result.getData() != null) {
            OfdApplication newApplication = new OfdApplication();
            newApplication.setOfdId(result.getData());
            newApplication.setOfdStatus(OfdStatus.PENDING);
            newApplication.setCustomerId(fiscalization.getCustomerId());
            newApplication.setCustomerBranchId(fiscalization.getCustomerBranchId());
            newApplication.setDeviceId(fiscalization.getDeviceId());
            newApplication.setFiscalModuleId(fiscalization.getFiscalModuleId());
            newApplication.setApplicationType(ApplicationType.CHANGE_ADDRESS);
            newApplication.setApplicationFileUrl(ofdApplication.getApplicationFileUrl());
            newApplication.setCreatedDate(LocalDateTime.now());
            ofdApplicationRepository.save(newApplication);

            fiscalization.setOfdApplicationId(newApplication.getId());
            fiscalizationRepository.save(fiscalization);
            cacheService.evictFiscalCache(fiscalization.getHashKey());
            return result.getText();
        } else {
            StringBuilder errorBuilder = new StringBuilder();
            if (CollectionUtils.isNotEmpty(result.getErrors())) {
                result.getErrors().forEach(ofdErrorDTO -> errorBuilder.append(ofdErrorDTO.getField()).append(" : ").append(ofdErrorDTO.getMessage()).append("\n"));
            } else {
                if (StringUtils.isNotEmpty(result.getText())) {
                    errorBuilder.append(result.getText());
                } else {
                    errorBuilder.append("Ошибка НИЦ");
                }
            }
            throw validationService.badRequestExceptionThrow(errorBuilder.toString()).get();
        }
    }

    @Override
    @Transactional
    public void merge(OfdApplicationMergeDTO ofdApplicationMergeDTO) {
        Fiscalization fiscalization = fiscalizationRepository.findById(ofdApplicationMergeDTO.getFiscalizationId()).orElseThrow(validationService.entityNotFoundExceptionThrow(FISCALIZATION));
        OfdApplicationListResponseDTO ofdApplicationListResponseDTO = ofdClient.getApplicationById(ofdApplicationMergeDTO.getOfdId());
        if (ofdApplicationListResponseDTO == null || CollectionUtils.isEmpty(ofdApplicationListResponseDTO.getData())) {
            throw validationService.entityNotFoundExceptionThrow(APPLICATION).get();
        }
        OfdApplicationDTO ofdApplicationDTO = ofdApplicationListResponseDTO.getData().getFirst();
        if (ofdApplicationDTO == null) {
            throw validationService.entityNotFoundExceptionThrow(APPLICATION).get();
        }

        if (!fiscalization.getFiscalModule().getSerialNumber().equals(ofdApplicationDTO.getTerminalId())) {
            throw validationService.badRequestExceptionThrow(FISCAL_MODULE_NOT_MATCH, fiscalization.getFiscalModule().getSerialNumber(), ofdApplicationDTO.getTerminalId()).get();
        }
        if (!fiscalization.getDevice().getSerialNumber().equals(ofdApplicationDTO.getSerialNumber())) {
            throw validationService.badRequestExceptionThrow(DEVICE_NOT_MATCH, fiscalization.getDevice().getSerialNumber(), ofdApplicationDTO.getSerialNumber()).get();
        }

        String createdDateString = ofdApplicationDTO.getCreatedDate().substring(0, ofdApplicationDTO.getCreatedDate().lastIndexOf("."));

        OfdApplication ofdApplication = ofdApplicationRepository.findFirstByOfdIdOrderByOfdIdDesc(ofdApplicationMergeDTO.getOfdId()).orElse(new OfdApplication());
        ofdApplication.setCreatedDate(LocalDateTime.parse(createdDateString, DateUtil.LONG_FORMATTER));
        ofdApplication.setOfdId(ofdApplicationDTO.getId());
        if (ofdApplicationDTO.getStatus() != null) {
            ofdApplication.setOfdStatus(OfdStatus.getById(ofdApplicationDTO.getStatus()));
        }
        ofdApplication.setApplicationFileUrl(ofdApplicationDTO.getRegistrationCardUrl());
        ofdApplication.setReason(ofdApplicationDTO.getAnswerDescription());
        ofdApplication.setOfficeId(ofdApplicationDTO.getOfficeId());
        ofdApplication.setOfficeFileId(ofdApplicationDTO.getOfficeFileId());
        ofdApplication.setContractFileId(ofdApplicationDTO.getContractFileId());
        ofdApplication.setApplicationFileId(ofdApplicationDTO.getApplicationFileId());
        if (ofdApplication.getApplicationType() == null) {
            ofdApplication.setApplicationType(ApplicationType.getById(ofdApplicationDTO.getApplicationTypeCode()));
        }
        ofdApplication.setDeviceId(fiscalization.getDeviceId());
        ofdApplication.setFiscalModuleId(fiscalization.getFiscalModuleId());
        ofdApplication.setCustomerBranchId(fiscalization.getCustomerBranchId());
        ofdApplication.setCustomerId(fiscalization.getCustomerId());
        ofdApplicationRepository.save(ofdApplication);

        fiscalization.setOfdApplicationId(ofdApplication.getId());
        fiscalizationRepository.save(fiscalization);
        cacheService.evictFiscalCache(fiscalization.getHashKey());
        rabbitMqSmartPosProducer.updateCustomer(fiscalization.getId());
    }

    @Override
    @Transactional
    public void changeApplicationType(Long id, ApplicationType applicationType) {
        OfdApplication ofdApplication = findById(id);
        ofdApplication.setApplicationType(applicationType);
        ofdApplicationRepository.save(ofdApplication);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, OfdStatus status) {
        OfdApplication ofdApplication = findById(id);
        ofdApplication.setOfdStatus(status);
        ofdApplicationRepository.save(ofdApplication);
    }

    @Override
    @Transactional
    public void rejectWithRecalculation(Long id, MultipartFile file) {
        OfdApplication ofdApplication = reject(file, id);
        Fiscalization fiscalization = fiscalizationRepository.findFirstByOfdApplicationId(ofdApplication.getId());
        log.info("RecalculateHistory start");
        servicePaymentHistoryCronService.recalculateHistory(ofdApplication.getCustomer().getTin(), fiscalization.getDevice().getSerialNumber(), ofdApplication.getCreatedDate().toLocalDate().plusDays(1));
        log.info("RecalculateHistory end");
    }
}
