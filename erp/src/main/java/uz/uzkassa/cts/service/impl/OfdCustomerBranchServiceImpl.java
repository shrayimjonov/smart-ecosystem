package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.domain.CustomerBranch;
import uz.uzkassa.cts.domain.FileUpload;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.ofd.ConfirmCustomerBranchDTO;
import uz.uzkassa.cts.dto.ofd.OfdCustomerBranchAddUpdateDTO;
import uz.uzkassa.cts.dto.ofd.OfdFileDTO;
import uz.uzkassa.cts.dto.tin.integration.OfdCustomerBranchResponseDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.DocumentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.OfdStatus;
import uz.uzkassa.cts.filter.CustomerBranchFilter;
import uz.uzkassa.cts.integrations.OfdClient;
import uz.uzkassa.cts.repository.CustomerBranchRentRepository;
import uz.uzkassa.cts.repository.CustomerBranchRepository;
import uz.uzkassa.cts.repository.CustomerRepository;
import uz.uzkassa.cts.repository.FileUploadRepository;
import uz.uzkassa.cts.service.OfdCustomerBranchService;
import uz.uzkassa.cts.utils.DateUtil;
import uz.uzkassa.cts.utils.FileUtils;
import uz.uzkassa.cts.utils.Utils;
import uz.uzkassa.cts.validator.LocalizationMessage;
import uz.uzkassa.cts.web.rest.errors.BadRequestException;

import java.util.ArrayList;
import java.util.List;

@Transactional(readOnly = true)
@Service
@Slf4j
public class OfdCustomerBranchServiceImpl extends BaseServiceImpl<CustomerBranch, Long> implements OfdCustomerBranchService {

    private final CustomerBranchRepository customerBranchRepository;
    private final CustomerRepository customerRepository;
    private final FileUploadRepository fileUploadRepository;
    private final OfdClient ofdClient;
    private final CustomerBranchRentRepository customerBranchRentRepository;
    private final LocalizationMessage localizationMessage;

    public OfdCustomerBranchServiceImpl(CustomerBranchRepository customerBranchRepository, CustomerRepository customerRepository, FileUploadRepository fileUploadRepository, OfdClient ofdClient, CustomerBranchRentRepository customerBranchRentRepository, LocalizationMessage localizationMessage) {
        super(customerBranchRepository);
        this.customerBranchRepository = customerBranchRepository;
        this.customerRepository = customerRepository;
        this.fileUploadRepository = fileUploadRepository;
        this.ofdClient = ofdClient;
        this.customerBranchRentRepository = customerBranchRentRepository;
        this.localizationMessage = localizationMessage;
    }

    @Override
    @Transactional
    public String confirm(ConfirmCustomerBranchDTO confirmCustomerBranchDTO) {

        Customer customer = customerRepository.findById(confirmCustomerBranchDTO.getCustomerId()).orElseThrow(validationService.entityNotFoundExceptionThrow(CUSTOMER));
        CustomerBranch customerBranch = customerBranchRepository.findById(confirmCustomerBranchDTO.getCustomerBranchId()).orElseThrow(validationService.entityNotFoundExceptionThrow(BRANCH));

        if (customerBranch.getOfdId() != null && confirmCustomerBranchDTO.getOfdId() != null && !customerBranch.getOfdId().equals(confirmCustomerBranchDTO.getOfdId())) {
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
                } catch (Exception e) {
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
                } catch (Exception e) {
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
        log.info("Response from OFD before check : {}", result);
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
            throw validationService.badRequestExceptionThrow(INVALID_INPUT, errorBuilder.toString()).get();
        }
    }

    @Override
    public List<CommonDTO> lookUp(CustomerBranchFilter filter) {
        return customerBranchRepository.getListForOfd(filter);
    }

}
