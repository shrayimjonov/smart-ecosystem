package uz.uzkassa.cts.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import uz.uzkassa.cts.config.ApplicationProperties;
import uz.uzkassa.cts.constants.FileConstants;
import uz.uzkassa.cts.constants.MessageConstants;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.domain.address.Region;
import uz.uzkassa.cts.dto.customer.CustomerBalanceDTO;
import uz.uzkassa.cts.dto.ofd.*;
import uz.uzkassa.cts.dto.soliq.SoliqCompanyDTO;
import uz.uzkassa.cts.dto.tin.integration.*;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.integrations.OfdClient;
import uz.uzkassa.cts.integrations.SoliqClient;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.CacheService;
import uz.uzkassa.cts.service.OfdService;
import uz.uzkassa.cts.service.SoliqService;
import uz.uzkassa.cts.service.ValidationService;
import uz.uzkassa.cts.utils.DateUtil;
import uz.uzkassa.cts.utils.FileUtils;
import uz.uzkassa.cts.utils.Utils;
import uz.uzkassa.cts.validator.LocalizationMessage;
import uz.uzkassa.cts.web.rest.errors.BadRequestException;
import uz.uzkassa.cts.web.rest.errors.IntegrationException;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class OfdServiceImpl implements OfdService, MessageConstants {

    CustomerRepository customerRepository;
    CustomerBranchRepository customerBranchRepository;
    DeviceRepository deviceRepository;
    FiscalModuleRepository fiscalModuleRepository;
    DeviceModelRepository deviceModelRepository;
    ApplicationProperties applicationProperties;
    FileUploadRepository fileUploadRepository;
    FiscalizationRepository fiscalizationRepository;
    RegionRepository regionRepository;
    DistrictRepository districtRepository;
    OfdApplicationRepository ofdApplicationRepository;
    SoliqService soliqService;
    CacheService cacheService;
    OfdClient ofdClient;
    SoliqClient soliqClient;
    CustomerBranchRentRepository customerBranchRentRepository;
    ValidationService validationService;
    LocalizationMessage localizationMessage;

    @NonFinal
    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Override
    @Transactional(readOnly = true)
    public OfdRegionListResponseDTO getRegions(String language) {
        return ofdClient.getRegions(language);
    }

    @Override
    @Transactional(readOnly = true)
    public OfdRegionListResponseDTO getDistricts(Integer regionCode, String language) {
        return ofdClient.getDistricts(regionCode, language);
    }

    @Override
    @Transactional(readOnly = true)
    public OfdNeighborhoodListResponseDTO getNeighborhoods(Long regionCode, Long districtCode) {
        return ofdClient.getNeighborhoods(regionCode, districtCode);
    }

    @Override
    @Transactional(readOnly = true)
    public OfdActivityGroupListResponseDTO getActivityTypeGroups() {
        return ofdClient.getActivityTypeGroups();
    }

    @Override
    @Transactional(readOnly = true)
    public OfdActivityGroupListResponseDTO getActivityTypes(Integer groupCode) {
        return ofdClient.getActivityTypes(groupCode);
    }

    @Override
    @Transactional(readOnly = true)
    public String getTerminalTypes() {
        return ofdClient.getTerminalTypes();
    }

    @Override
    public OfdCheckTinResponseResultDTO checkCustomer(String tinOrPinfl) {
        SoliqCompanyDTO soliqCompanyDTO = soliqClient.getCustomerByTin(tinOrPinfl);

        if (Utils.isTin(tinOrPinfl) && "ЯТТ".equalsIgnoreCase(soliqCompanyDTO.getName())) {
            throw validationService.badRequestExceptionThrow("you.should.register.by.pinfl", "ЯТТ").get();
        }

        OfdCheckTinResponseResultDTO result = new OfdCheckTinResponseResultDTO();
        customerRepository.findFirstByTin(tinOrPinfl).ifPresent(customer -> {
            if (CustomerStatus.DRAFT.equals(customer.getStatus())) {
                result.setId(customer.getId());
            }
        });

        Region region = regionRepository.findByCode(soliqCompanyDTO.getNs10Code());
        if (region != null) {
            result.setRegionId(region.getId());
            result.setRegionName(region.getNameRu());
            result.setRegionCode(region.getCode());
            districtRepository.findFirstByRegionIdAndCode(region.getId(), soliqCompanyDTO.getNs11Code())
                    .ifPresent(district -> {
                        result.setDistrictId(district.getId());
                        result.setDistrictName(district.getNameRu());
                        result.setDistrictCode(district.getCode());
                    });
        }

        OfdAccountDTO ofdAccountDTO = ofdClient.checkBalance(tinOrPinfl);
        if (ofdAccountDTO != null) {
            result.setBalance(ofdAccountDTO.getOverpaymentSum());
        }
        result.setAddress(soliqCompanyDTO.getAddress());
        result.setTin(tinOrPinfl.length() == 9 ? tinOrPinfl : null);
        result.setPinfl(tinOrPinfl.length() != 9 ? tinOrPinfl : null);
        result.setNameFull(soliqCompanyDTO.getFullName());
        result.setName(soliqCompanyDTO.getName());

        String[] names = StringUtils.split(soliqCompanyDTO.getName());
        result.setBusinessType(BusinessType.get(names[0].trim()));
        return result;
    }

    @Override
    public OfdGenerateApplicationFileDTO generateApplicationFile(OfdGenerateApplicationFileRequestDTO fileGenerateRequestDTO) {
        OfdGenerateApplicationFileResponseDTO result = ofdClient.generateApplicationFile(fileGenerateRequestDTO);
        if (result.isSuccess() && result.getData() != null) {
            return result.getData();
        } else {
            StringBuilder errorBuilder = new StringBuilder();
            if (result.getErrors() != null && !result.getErrors().isEmpty()) {
                result.getErrors().forEach(errorDTO -> errorBuilder.append(errorDTO.getField()).append(" : ").append(errorDTO.getMessage()).append("\n"));
            } else {
                if (StringUtils.isNotEmpty(result.getText())) {
                    errorBuilder.append(result.getText());
                } else {
                    errorBuilder.append("Ошибка проверки");
                }
            }
            throw validationService.badRequestExceptionThrow(INVALID_INPUT, errorBuilder.toString()).get();
        }
    }

    @Override
    public CustomerBalanceDTO checkCustomerBalance(String tin) {
        OfdAccountDTO ofdAccountDTO = ofdClient.checkBalance(tin);
        if (ofdAccountDTO == null) {
            throw new BadRequestException("Empty data come from OFD");
        }
        CustomerBalanceDTO result = new CustomerBalanceDTO();
        result.setTin(tin);
        result.setTerminalCost(ofdAccountDTO.getTerminalCost());
        result.setCustomerBalance(ofdAccountDTO.getOverpaymentSum());
        result.setUnpaidCount(ofdAccountDTO.getUnpaidCount());
        result.setFiscalizationCount(fiscalizationRepository.totalByCustomerTin(tin).intValue());

        String formula = """
                Требуемая сумма: x = Цена терминала * Фискализации;\s
                Разная сумма: y = Баланс - x;\s
                Актуальная Сумма: z = y - Цена терминала;\s
                """;
        result.setFormula(formula);
        result.setBank(soliqService.getBankByTin(tin));
        return result;
    }

    @Override
    public String sendCustomerBranch(CustomerBranchSendDTO customerBranchUpdateDTO) {

        Customer customer = customerRepository.findById(customerBranchUpdateDTO.getCustomerId()).orElse(null);
        if (customer == null) {
            throw validationService.entityNotFoundExceptionThrow(CUSTOMER).get();
        }

        CustomerBranch customerBranch = customerBranchRepository.findById(customerBranchUpdateDTO.getCustomerBranchId()).orElse(null);
        if (customerBranch == null) {
            throw validationService.entityNotFoundExceptionThrow(BRANCH).get();
        }

        if (customerBranch.getOfdId() != null && customerBranchUpdateDTO.getOfdId() != null && !customerBranch.getOfdId().equals(customerBranchUpdateDTO.getOfdId())) {
            throw validationService.badRequestExceptionThrow(CUSTOMER_BRANCH_ALREADY_SENT, customerBranch.getName()).get();
        }

        if (customerBranch.getAddress().getRegionId() == null) {
            throw validationService.entityNotFoundExceptionThrow(REGION).get();
        }

        if (customerBranch.getAddress().getDistrictId() == null) {
            throw validationService.entityNotFoundExceptionThrow(DISTRICT).get();
        }

        if (customerBranch.getAddress().getNeighborhoodId() == null) {
            throw validationService.entityNotFoundExceptionThrow(NEIGHBORHOOD).get();
        }

        if (StringUtils.isEmpty(customerBranch.getAddress().getLongitude())) {
            throw validationService.entityNotFoundExceptionThrow(LONGITUDE).get();
        }
        if (StringUtils.isEmpty(customerBranch.getAddress().getLatitude())) {
            throw validationService.entityNotFoundExceptionThrow(LATITUDE).get();
        }

        List<FileUpload> cadastreFiles = fileUploadRepository.findAllByDocumentTypeAndEntityTypeAndEntityIdAndContentType(DocumentType.CADASTRE, EntityTypeEnum.CUSTOMER_BRANCH, customerBranch.getId(), MediaType.APPLICATION_PDF_VALUE);
        List<FileUpload> leaseContractFiles = fileUploadRepository.findAllByDocumentTypeAndEntityTypeAndEntityIdAndContentType(DocumentType.LEASE_CONTRACT, EntityTypeEnum.CUSTOMER_BRANCH, customerBranch.getId(), MediaType.APPLICATION_PDF_VALUE);

        List<OfdFileDTO> files = new ArrayList<>();

        if (cadastreFiles != null) {
            for (FileUpload cadastreFile : cadastreFiles) {
                try {
                    files.add(new OfdFileDTO(cadastreFile.getFileName(), FileUtils.fileToBase64(cadastreFile.getDownloadUrl())));
                } catch (
                        Exception e) {
                    log.error(e.getMessage());
                    log.error("Cadastre File parse error:{}", e.getMessage());
                    String error = localizationMessage.localize(FILE_PARSE_FAILED, new Object[]{cadastreFile.getFileName()}) +
                            " Customer: " + customer.getTin() +
                            " TT: " + customerBranch.getName() +
                            " File: " + cadastreFile.getFileName();
                    throw new BadRequestException(error);
                }
            }
        }

        if (leaseContractFiles != null) {
            for (FileUpload leaseContractFile : leaseContractFiles) {
                try {
                    files.add(new OfdFileDTO(leaseContractFile.getFileName(), FileUtils.fileToBase64(leaseContractFile.getDownloadUrl())));
                } catch (
                        Exception e) {
                    log.error(e.getMessage());
                    log.error("Lease Contract file parse error:{}", e.getMessage());
                    String error = localizationMessage.localize(FILE_PARSE_FAILED, new Object[]{leaseContractFile.getFileName()}) +
                            " Customer: " + customer.getTin() +
                            " TT: " + customerBranch.getName() +
                            " File: " + leaseContractFile.getFileName();
                    throw new BadRequestException(error);
                }
            }
        }

        if (files.isEmpty()) {
            log.error(localizationMessage.localize(ENTITY_NOT_FOUND, new Object[]{FILE}));
            throw validationService.entityNotFoundExceptionThrow(FILE).get();
        }

        OfdCustomerBranchAddUpdateDTO ofdCustomerBranchAddUpdateDTO = new OfdCustomerBranchAddUpdateDTO();
        ofdCustomerBranchAddUpdateDTO.setId(customerBranch.getOfdId());
        if (Utils.isTin(customer.getTin())) {
            ofdCustomerBranchAddUpdateDTO.setTin(customer.getTin());
        } else {
            ofdCustomerBranchAddUpdateDTO.setPinfl(customer.getTin());
        }
        ofdCustomerBranchAddUpdateDTO.setName(customerBranch.getName());
        ofdCustomerBranchAddUpdateDTO.setAddress(customerBranch.getAddress().getAddress());
        ofdCustomerBranchAddUpdateDTO.setRegionCode(customerBranch.getAddress().getRegion().getCode());
        ofdCustomerBranchAddUpdateDTO.setDistrictCode(customerBranch.getAddress().getDistrict().getCode());
        if (customerBranch.getAddress().getNeighborhood() != null) {
            ofdCustomerBranchAddUpdateDTO.setNeighborhoodCode(customerBranch.getAddress().getNeighborhood().getCode().toString());
        } else {
            ofdCustomerBranchAddUpdateDTO.setNeighborhoodCode(" ");
        }
        if (customerBranch.getActivityType().getParent() != null) {
            ofdCustomerBranchAddUpdateDTO.setActivityGroup(customerBranch.getActivityType().getParent().getCode());
        } else {
            ofdCustomerBranchAddUpdateDTO.setActivityGroup(customerBranch.getActivityType().getCode());
        }
        ofdCustomerBranchAddUpdateDTO.setActivityType(customerBranch.getActivityType().getCode());
        ofdCustomerBranchAddUpdateDTO.setLongitude(customerBranch.getAddress().getLongitude());
        ofdCustomerBranchAddUpdateDTO.setLatitude(customerBranch.getAddress().getLatitude());
        ofdCustomerBranchAddUpdateDTO.setFiles(files);

        customerBranchRentRepository
                .findFirstByCustomerBranchIdAndStatus(customerBranch.getId(), CommonStatus.ACTIVE)
                .ifPresentOrElse(
                        customerBranchRent -> {
                            ofdCustomerBranchAddUpdateDTO.setRentNumber(customerBranchRent.getRentNumber());
                            ofdCustomerBranchAddUpdateDTO.setRentBeginDate(DateUtil.getLocalDateAsString(customerBranchRent.getStartDate()));
                            ofdCustomerBranchAddUpdateDTO.setRentEndDate(DateUtil.getLocalDateAsString(customerBranchRent.getEndDate()));
                        },
                        () -> ofdCustomerBranchAddUpdateDTO.setCadastreNumber(customerBranch.getCadastreNumber()));

        log.info("Request to change address from OFD : {}", ofdCustomerBranchAddUpdateDTO);
        OfdCustomerBranchResponseDTO result = ofdClient.createOrUpdateCustomerBranch(customerBranch.getOfdId(), ofdCustomerBranchAddUpdateDTO);
        if (result.isSuccess() && result.getData() != null && result.getData().getId() != null) {
            log.info("Response from OFD (change address): {}", result.getData());
            customerBranch.setOfdId(result.getData().getId());
            customerBranch.setOfdStatus(result.getData().getStatus() == 1 ? OfdStatus.ACTIVE : OfdStatus.PENDING);
            customerBranch.setResync(false);
            customerBranchRepository.save(customerBranch);
            log.info(">>>>>>>>>> End Send Customer Branch <<<<<<<<<<<<");
            return result.getText();
        } else {
            log.error("Error catch from TAX : {}", result);
            StringBuilder errorBuilder = new StringBuilder();
            if (result.getErrors() != null && !result.getErrors().isEmpty()) {
                result.getErrors().forEach(errorDTO -> errorBuilder.append(errorDTO.getField()).append(" : ").append(errorDTO.getMessage()).append("\n"));
            } else {
                if (StringUtils.isNotEmpty(result.getText())) {
                    errorBuilder.append(result.getText());
                } else {
                    errorBuilder.append("Ошибка проверки");
                }
            }
            throw new IntegrationException(localizationMessage.localize(INVALID_INPUT, new Object[]{errorBuilder.toString()}));
        }
    }

    @Override
    public String makeCustomerFiscal(CustomerFiscalDTO customerFiscalDTO) {
        Customer customer = customerRepository.findById(customerFiscalDTO.getCustomerId()).orElse(null);
        if (customer == null) {
            throw validationService.entityNotFoundExceptionThrow(CUSTOMER).get();
        }

        CustomerBranch customerBranch = customerBranchRepository.findById(customerFiscalDTO.getCustomerBranchId()).orElse(null);
        if (customerBranch == null) {
            throw validationService.entityNotFoundExceptionThrow(BRANCH).get();
        }

        String modelCode = SecurityUtils.getModelCode(customerFiscalDTO.getDeviceSerial());
        DeviceModel deviceModel = deviceModelRepository.findFirstByCode(modelCode);
        if (deviceModel == null) {
            throw validationService.entityNotFoundExceptionThrow(DEVICE_MODEL).get();
        }

        if (deviceModel.getOfdId() == null) {
            throw validationService.entityNotFoundExceptionThrow(DEVICE_MODEL, modelCode, "OFD").get();
            //throw new NotFoundException(localizationMessage.localize(ENTITY_NOT_FOUND_WITH, new Object[]{localizationMessage.localize(DEVICE_MODEL), modelCode, "OFD"}));
        }

        Device device = deviceRepository.findByModelIdAndSerialNumber(deviceModel.getId(), customerFiscalDTO.getDeviceSerial());
        if (device == null) {
            throw validationService.entityNotFoundExceptionThrow(DEVICE_MODEL, SERIAL_NUMBER, customerFiscalDTO.getDeviceSerial()).get();
        }

        FiscalModule fiscalModule = fiscalModuleRepository.findBySerialNumber(customerFiscalDTO.getFiscalModuleSerial());
        if (fiscalModule == null) {
            throw validationService.entityNotFoundExceptionThrow(FISCAL_MODULE, SERIAL_NUMBER, customerFiscalDTO.getFiscalModuleSerial()).get();
        }

        Fiscalization fiscalization = fiscalizationRepository.findFirstByCustomerIdAndCustomerBranchIdAndDeviceIdAndFiscalModuleId(
                customer.getId(),
                customerBranch.getId(),
                device.getId(),
                fiscalModule.getId());

        if (fiscalization == null) {
            throw validationService.entityNotFoundExceptionThrow(FISCALIZATION).get();
        }

        if (fiscalization.getOfdApplicationId() != null) {
            OfdApplication ofdApplication = ofdApplicationRepository.findById(fiscalization.getOfdApplicationId()).orElse(null);
            if (ofdApplication != null) {
                if (!OfdStatus.REJECT.getCode().equals(ofdApplication.getOfdStatus().getCode())) {
                    log.error("Application has already been sent to {}", fiscalization.getCustomer().getTin());
                    log.error("Application status is {}", fiscalization.getOfdApplication().getOfdStatus().getCode());
                    throw validationService.badRequestExceptionThrow(APPLICATION_ALREADY_SENT, device.getSerialNumber(), fiscalModule.getSerialNumber()).get();
                }
            }
        }

        //Application File
        FileUpload applicationFile = fileUploadRepository.findFirstByDocumentTypeAndEntityTypeAndEntityId(DocumentType.APPLICATION, EntityTypeEnum.FISCALIZATION, fiscalization.getId());
        if (applicationFile == null) {
            log.error("Application file is null");
            throw validationService.entityNotFoundExceptionThrow(APPLICATION_FILE, SERIAL_NUMBER, device.getSerialNumber()).get();
        }

        List<FileUpload> cadastreFiles = fileUploadRepository.findAllByDocumentTypeAndEntityTypeAndEntityIdAndContentType(DocumentType.CADASTRE, EntityTypeEnum.CUSTOMER_BRANCH, customerBranch.getId(), MediaType.APPLICATION_PDF_VALUE);
        List<FileUpload> leaseContractFiles = fileUploadRepository.findAllByDocumentTypeAndEntityTypeAndEntityIdAndContentType(DocumentType.LEASE_CONTRACT, EntityTypeEnum.CUSTOMER_BRANCH, customerBranch.getId(), MediaType.APPLICATION_PDF_VALUE);

        if (cadastreFiles.isEmpty() && leaseContractFiles.isEmpty()) {
            log.error(localizationMessage.localize(ENTITY_NOT_FOUND, new Object[]{FILE}));
            throw validationService.entityNotFoundExceptionThrow(FILE).get();
        }

        String file64;
        try {
            Resource resource = new ClassPathResource("/data/contract_offer_base64.txt");
            InputStream inputStream = resource.getInputStream();
            file64 = new String(FileCopyUtils.copyToByteArray(inputStream), StandardCharsets.UTF_8);
        } catch (
                Exception e) {
            String error = localizationMessage.localize(FILE_PARSE_FAILED, new Object[]{FileConstants.CONTRACT_OFFER_FILE_URL}) +
                    " Customer: " + customer.getTin() +
                    " TT: " + customerBranch.getName() +
                    " File: " + FileConstants.CONTRACT_OFFER_FILE_URL;
            throw new BadRequestException(error);
        }

        OfdCustomerFiscalDTO ofdCustomerFiscalDTO = new OfdCustomerFiscalDTO();
        ofdCustomerFiscalDTO.setCcmCategoryId(deviceModel.getOfdId());
        ofdCustomerFiscalDTO.setCcmSerialNumber(device.getSerialNumber());
        ofdCustomerFiscalDTO.setOfficeId(customerBranch.getOfdId());
        ofdCustomerFiscalDTO.setPhone(customer.getPhone());
        ofdCustomerFiscalDTO.setTerminalId(customerFiscalDTO.getFiscalModuleSerial());
        if (Utils.isTin(customer.getTin())) {
            ofdCustomerFiscalDTO.setTin(customer.getTin());
        } else {
            ofdCustomerFiscalDTO.setPinfl(customer.getTin());
        }

        //Contract File
        List<OfdFileDTO> contactFiles = new ArrayList<>();
        contactFiles.add(new OfdFileDTO(FileConstants.CONTRACT_OFFER_FILE_NAME, file64));
        ofdCustomerFiscalDTO.setContractFile(contactFiles);

        List<OfdFileDTO> applicationFiles = new ArrayList<>();
        try {
            applicationFiles.add(new OfdFileDTO(applicationFile.getFileName(), FileUtils.fileToBase64(applicationFile.getDownloadUrl())));
        } catch (
                Exception e) {
            log.error("Application File parse error:{}", e.getMessage());
            log.error("File url:{}", applicationFile.getDownloadUrl());

            String error = localizationMessage.localize(FILE_PARSE_FAILED, new Object[]{applicationFile.getDownloadUrl()}) +
                    " Customer: " + customer.getTin() +
                    " TT: " + customerBranch.getName() +
                    " File: " + applicationFile.getFileName();
            throw new BadRequestException(error);
        }
        ofdCustomerFiscalDTO.setApplicationFile(applicationFiles);
        /*Scanner */
        if (customerBranch.getCustomerBranchScanner() != null) {
            //Scanner File
            FileUpload scannerFile = fileUploadRepository.findFirstByDocumentTypeAndEntityTypeAndEntityId(DocumentType.CUSTOMER_BRANCH_SCANNER, EntityTypeEnum.CUSTOMER_BRANCH_SCANNER, customerBranch.getId());
            /*Scanner object*/
            OfdCustomerBranchScannerDTO ofdCustomerBranchScannerDTO = new OfdCustomerBranchScannerDTO();
            if (scannerFile != null) {
                List<OfdFileDTO> scannerFiles = new ArrayList<>();
                try {
                    scannerFiles.add(new OfdFileDTO(scannerFile.getFileName(), FileUtils.fileToBase64(scannerFile.getDownloadUrl())));
                } catch (
                        Exception e) {
                    log.error("Scanner File parse error:{}", e.getMessage());
                    log.error("File url:{}", scannerFile.getDownloadUrl());

                    String error = localizationMessage.localize(FILE_PARSE_FAILED, new Object[]{scannerFile.getDownloadUrl()}) +
                            " Customer: " + customer.getTin() +
                            " TT: " + customerBranch.getName() +
                            " File: " + scannerFile.getFileName();
                    throw new BadRequestException(error);
                }
                ofdCustomerBranchScannerDTO.setFile(scannerFiles);
            }
            ofdCustomerBranchScannerDTO.setType(Integer.valueOf(customerBranch.getCustomerBranchScanner().getCode()));

            ofdCustomerFiscalDTO.setScanner(ofdCustomerBranchScannerDTO);
        }
        if (ofdCustomerFiscalDTO.getCcmCategoryId() == null
                || ofdCustomerFiscalDTO.getCcmSerialNumber() == null
                || ofdCustomerFiscalDTO.getTerminalId() == null
                || ofdCustomerFiscalDTO.getPhone() == null
                || ofdCustomerFiscalDTO.getOfficeId() == null
                || ofdCustomerFiscalDTO.getApplicationFile().isEmpty()
                || ofdCustomerFiscalDTO.getContractFile().isEmpty()) {
            log.error("One of required field is null");
            throw validationService.badRequestExceptionThrow(INVALID_INPUT, FISCALIZATION).get();
        }
        log.info("Request confirm CUSTOMER: {}", ofdCustomerFiscalDTO);
        OfdApplicationSendResponseDTO result = ofdClient.makeCustomerFiscal(ofdCustomerFiscalDTO);
        if (result.isSuccess() && result.getData() != null && result.getData().getId() != null) {
            log.info("Response ofd : {}", result.getData());
            OfdApplication ofdApplication = new OfdApplication();
            ofdApplication.setOfdId(result.getData().getId());
            ofdApplication.setOfdStatus(OfdStatus.PENDING);
            ofdApplication.setApplicationFileUrl(result.getData().getFileUrl());
            ofdApplication.setCustomerId(customer.getId());
            ofdApplication.setCustomerBranchId(customerBranch.getId());
            ofdApplication.setDeviceId(device.getId());
            ofdApplication.setFiscalModuleId(fiscalModule.getId());
            ofdApplication.setApplicationType(ApplicationType.REGISTER);
            ofdApplication.setCreatedDate(LocalDateTime.now());

            ofdApplicationRepository.save(ofdApplication);

            fiscalization.setOfdApplicationId(ofdApplication.getId());
            fiscalizationRepository.save(fiscalization);
            cacheService.evictFiscalCache(fiscalization.getHashKey());
            return result.getText();
        } else {
            StringBuilder errorBuilder = new StringBuilder();
            if (result.getErrors() != null && !result.getErrors().isEmpty()) {
                result.getErrors().forEach(ofdErrorDTO -> errorBuilder.append(ofdErrorDTO.getField()).append(" : ").append(ofdErrorDTO.getMessage()).append("\n"));
            } else {
                if (StringUtils.isNotEmpty(result.getText())) {
                    errorBuilder.append(result.getText());
                } else {
                    errorBuilder.append("Ошибка проверки");
                }
            }
            throw new IntegrationException(errorBuilder.toString());
        }
    }

    /*@Override
    @Transactional(readOnly = true)
    public TxkmResponseDTO getTxkm(Integer pageNo, Integer pageSize, String serialNumber, boolean type) {
        log.warn("Start send request to TAX to get notification {}", serialNumber);
        Fiscalization fiscalization;
        if (type) {
            fiscalization = fiscalizationRepository.findFirstByDevice_SerialNumberAndStatusIn(serialNumber, List.of(DeviceStatusEnum.FISCAL, DeviceStatusEnum.FISCAL_ACTIVE));
        } else {
            fiscalization = fiscalizationRepository.findFirstByFiscalModule_SerialNumberAndStatusIn(serialNumber, List.of(DeviceStatusEnum.FISCAL, DeviceStatusEnum.FISCAL_ACTIVE));
        }
        if (fiscalization == null) {
            return null;
        }

        String fmSerialNumber = fiscalization.getFiscalModule().getSerialNumber();
        String modelCode = fiscalization.getDevice().getModel().getCode();
        String tin;
        log.warn("Entered  send request to TAX to get notification {}", fmSerialNumber);
        if (modelCode.equalsIgnoreCase(DeviceModelEnum.P100.name()) || modelCode.equalsIgnoreCase(DeviceModelEnum.P10.name())) {
            tin = applicationProperties.getTxkmProperty().getP100p10Tin();
        } else if (modelCode.equalsIgnoreCase(DeviceModelEnum.N5.name())) {
            tin = applicationProperties.getTxkmProperty().getN5Tin();
        } else {
            return null;
        }
        log.warn("Ended send request to TAX to get notification {}", fmSerialNumber);
        return ofdClient.getTxkmByFmSerial(
                TxkmDTO.builder()
                        .pageNo(pageNo)
                        .pageSize(pageSize)
                        .terminalId(fiscalization.getFiscalModule().getSerialNumber())
                        .tin(tin).build());
    }*/
}
