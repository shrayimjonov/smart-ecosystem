package uz.uzkassa.cts.dto.cases.caseproperty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.constants.MessageConstants;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.domain.cases.CaseAgreement;
import uz.uzkassa.cts.dto.cases.request.HumoConnectionDeviceDto;
import uz.uzkassa.cts.dto.cases.request.HumoConnectionDto;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.enums.billing.AgreementStatus;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.service.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Component
public class HumoConnectionProperty implements CaseAction {

    @Autowired
    @JsonIgnore
    private CustomerOldService customerOldService;
    @Autowired
    @JsonIgnore
    private ThymeleafService thymeleafService;
    @Autowired
    @JsonIgnore
    private HumoConnectRepository humoConnectRepository;
    @Autowired
    @JsonIgnore
    private DeviceLicenseRepository deviceLicenseRepository;
    @Autowired
    @JsonIgnore
    private CaseService caseService;
    @Autowired
    @JsonIgnore
    private BankService bankService;
    @Autowired
    @JsonIgnore
    private DeviceRepository deviceRepository;
    @Autowired
    @JsonIgnore
    private FiscalizationRepository fiscalizationRepository;
    @Autowired
    @JsonIgnore
    private CaseFileRepository caseFileRepository;
    @Autowired
    @JsonIgnore
    private UserRepository userRepository;
    @Autowired
    @JsonIgnore
    private CustomerEmployeeRepository customerEmployeeRepository;
    @Autowired
    @JsonIgnore
    private CaseAgreementService caseAgreementService;
    @Autowired
    @JsonIgnore
    private ValidationService validationService;


    @NotNull
    private Long bankId;
    @NotNull
    private Long customerId;
    @NotNull
    private Long customerBranchId;
    @NotBlank
    private String deviceSerialNumber;
    @NotBlank
    private String accountNumber;
    @NotBlank
    private String bankName;
    @NotBlank
    private String mfo;
    @NotNull
    @Enumerated(EnumType.STRING)
    private BankType bankType;
    @NotNull
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;
    private String oked;
    private String phone;
    private String secretKey;
    private Long caseId;

    @Override
    public void accept() {
        Customer customer = customerOldService.findById(customerId);
        CaseAgreement caseAgreement = caseAgreementService.checkAgreementStatus(caseId, EntityTypeEnum.CASES);
        if (!customer.isWhiteList() && caseAgreement != null && caseAgreement.getAgreementStatus() == AgreementStatus.INACTIVE) {
            validationService.badRequestExceptionThrow(MessageConstants.CASE_AGREEMENT_INACTIVE);
        }
        Optional<HumoConnect> optionalHumoConnect = humoConnectRepository.findByCaseId(caseId);
        if (optionalHumoConnect.isEmpty()) {
            Bank bank = bankService.findById(bankId);
            HumoConnect humoConnect = new HumoConnect();
            humoConnect.setAddress(customer.getAddress());
            humoConnect.setCustomerId(customerId);
            humoConnect.setDeviceSerialNumber(deviceSerialNumber);
            humoConnect.setPhone(phone);
            humoConnect.setAccountNumber(accountNumber);
            humoConnect.setMfo(bank.getCode());
            humoConnect.setCaseId(caseId);
            humoConnect.setStatus(HumoConnectStatus.PENDING);
            humoConnect.setOperatorStatus(HumoConnectOperatorStatus.NOT_CONNECTED);
            humoConnect.setCaseCreatorId(caseService.findById(caseId).getCreatorId());
            humoConnect.setBusinessType(customer.getBusinessType());
            humoConnect.setAppType(customer.getAppType());
            humoConnect.setBankType(bankType);
            humoConnect.setPaymentType(paymentType);
            humoConnect.setCaseType(CaseType.HUMO_CONNECTION_CHANGE);
            humoConnect.setDocFlowStatus(DocFlowStatus.NOT_COME);
            humoConnect.setCustomerBranchId(customerBranchId);
            caseFileRepository
                    .findByCaseIdAndDocumentType(caseId, DocumentType.APPLICATION)
                    .ifPresent(caseFile -> {
                        humoConnect.setFileUrl(caseFile.getFileUrl());
                        humoConnect.setFileName(caseFile.getFileName());
                    });
            humoConnectRepository.save(humoConnect);
        }
    }

    @Override
    public List<String> generateTemplate() {
        Customer customer = customerOldService.findById(customerId);
        List<String> templates = new ArrayList<>();
        if (BankType.TRASTBANK == this.bankType) {
            templates.add(thymeleafService.humoConnectionTrans(map()));
        } else {
            templates.add(thymeleafService.humoConnectionStatement(map()));
            if (BusinessType.IP == customer.getBusinessType()) {
                templates.add(thymeleafService.humoOpenAccountIP(map()));
            } else {
                templates.add(thymeleafService.humoOpenAccount(map()));
            }
        }
        return templates;
    }

    private HumoConnectionDto map() {
        Bank bank = bankService.findById(bankId);
        Customer customer = customerOldService.findById(customerId);
        BankTariff bankTariff = customer.getBankTariff();
        Device device = deviceRepository.findFirstBySerialNumber(deviceSerialNumber);
        Fiscalization fiscalization = fiscalizationRepository.findFirstByCustomerIdAndDeviceId(customerId, device.getId());
        HumoConnectionDto humoConnectionDTO = new HumoConnectionDto();
        humoConnectionDTO.setTin(customer.getTin());
        humoConnectionDTO.setDate(LocalDate.now());
        humoConnectionDTO.setDeviceSerialNumber(this.deviceSerialNumber);
        humoConnectionDTO.setCompanyName(customer.getCompanyName());
        humoConnectionDTO.setAddress(customer.getAddress().getFullAddressAddress());
        humoConnectionDTO.setCustomerBranchAddress(fiscalization != null ? fiscalization.getCustomerBranch().getAddress().getFullAddressAddress() : "");
        humoConnectionDTO.setPhone(phone);
        HumoConnectionDeviceDto humoConnectionDeviceDto = new HumoConnectionDeviceDto();
        humoConnectionDeviceDto.setDeviceModelName(device.getModel() != null ? device.getModel().getName() : "");
        humoConnectionDeviceDto.setDeviceSerialNumber(device.getSerialNumber());
        humoConnectionDTO.getDevices().add(humoConnectionDeviceDto);
        humoConnectionDTO.setMfo(bank.getCode());
        humoConnectionDTO.setAccountNumber(accountNumber);
        humoConnectionDTO.setBankName(bank.getNameNotNullable());
        humoConnectionDTO.setSecretKey(secretKey);
        customerEmployeeRepository.findFirstByCustomerIdAndRole(customerId, Role.DIRECTOR).ifPresent(customerEmployee -> humoConnectionDTO.setDirector(customerEmployee.getName()));

        if (bankTariff != null) {
            humoConnectionDTO.setUzcardPercentage(bankTariff.getHumoPercentage());
            humoConnectionDTO.setHumoPercentage(bankTariff.getHumoPercentage());
            humoConnectionDTO.setVisaPercentage(bankTariff.getVisaPercentage());
        }
        return humoConnectionDTO;
    }

    @JsonIgnore
    @Override
    public boolean is2Step() {
        return false;
    }
}
