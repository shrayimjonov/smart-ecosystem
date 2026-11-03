package uz.uzkassa.cts.dto.cases.caseproperty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.domain.cases.Cases;
import uz.uzkassa.cts.dto.cases.request.HumoBankDetailsChangeDto;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.repository.CaseFileRepository;
import uz.uzkassa.cts.repository.CustomerEmployeeRepository;
import uz.uzkassa.cts.repository.DeviceRepository;
import uz.uzkassa.cts.repository.HumoConnectRepository;
import uz.uzkassa.cts.service.BankService;
import uz.uzkassa.cts.service.CaseService;
import uz.uzkassa.cts.service.CustomerOldService;
import uz.uzkassa.cts.service.ThymeleafService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Component
public class HumoBankDetailsChangeProperty implements CaseAction {

    @Autowired
    @JsonIgnore
    private CustomerOldService customerOldService;
    @Autowired
    @JsonIgnore
    private ThymeleafService thymeleafService;
    @Autowired
    @JsonIgnore
    private BankService bankService;
    @Autowired
    @JsonIgnore
    private HumoConnectRepository humoConnectRepository;
    @Autowired
    @JsonIgnore
    private CaseFileRepository caseFileRepository;
    @Autowired
    @JsonIgnore
    private DeviceRepository deviceRepository;
    @Autowired
    @JsonIgnore
    private CustomerEmployeeRepository customerEmployeeRepository;
    @Autowired
    @JsonIgnore
    private CaseService caseService;

    @NotNull
    private Long customerId;
    @NotNull
    private Long bankId;
    @NotNull
    private Long customerBranchId;
    @NotBlank
    private String bankName;
    @NotBlank
    private String mfo;
    @NotBlank
    private String accountNumber;
    @NotBlank
    private String oked;
    @NotBlank
    private String deviceSerialNumber;
    private Long caseId;


    @Override
    public void accept() {
        Optional<HumoConnect> optionalHumoConnect = humoConnectRepository.findByCaseId(caseId);
        if (optionalHumoConnect.isEmpty()) {
            Customer customer = customerOldService.findById(customerId);
            HumoConnect humoConnect = new HumoConnect();
            humoConnect.setCaseId(caseId);
            humoConnect.setCustomerId(customerId);
            humoConnect.setAccountNumber(accountNumber);
            humoConnect.setAddress(customer.getAddress());
            humoConnect.setStatus(HumoConnectStatus.DEFAULT);
            humoConnect.setDeviceSerialNumber(deviceSerialNumber);
            humoConnect.setMfo(customer.getCustomerBank().getBank().getCode());
            humoConnect.setOperatorStatus(HumoConnectOperatorStatus.DEFAULT);
            humoConnect.setCaseCreatorId(caseService.findById(caseId).getCreatorId());
            humoConnect.setBusinessType(customer.getBusinessType());
            humoConnect.setAppType(customer.getAppType());
            humoConnect.setBankType(customer.getBankTariff() != null ? customer.getBankTariff().getBankType() : BankType.HAMKORBANK);
            humoConnect.setDocFlowStatus(humoConnect.getBankType() == BankType.TRASTBANK ? DocFlowStatus.TRANSFERRED_ARCHIVE : DocFlowStatus.NOT_COME);
            humoConnect.setCaseType(CaseType.HUMO_BANK_DETAILS_CHANGE);
            humoConnect.setCustomerBranchId(customerBranchId);
            caseFileRepository
                    .findByCaseIdAndDocumentType(caseId, DocumentType.APPLICATION)
                    .ifPresent(caseFile -> {
                        humoConnect.setFileUrl(caseFile.getFileUrl());
                        humoConnect.setFileName(caseFile.getFileName());
                    });
            humoConnectRepository.save(humoConnect);
            Cases cases = caseService.findById(caseId);
            cases.setStatus(CaseStatus.ACCEPTED);
            caseService.save(cases);
        }
    }

    @Override
    public List<String> generateTemplate() {
        Customer customer = customerOldService.findById(customerId);
        BankTariff bankTariff = customer.getBankTariff();
        if (bankTariff != null) {
            if (BankType.TRASTBANK == bankTariff.getBankType()) {
                return Collections.singletonList(thymeleafService.humoBankDetailsChangeStatement(map()));
            } else {
                return Collections.singletonList(thymeleafService.humoChangeAccount(map()));
            }
        }
        return Collections.singletonList(thymeleafService.humoChangeAccount(map()));
    }

    private HumoBankDetailsChangeDto map() {
        Customer customer = customerOldService.findById(customerId);
        Bank bank = bankService.findById(bankId);
        Device device = deviceRepository.findBySerialNumber(deviceSerialNumber);
        HumoBankDetailsChangeDto dto = new HumoBankDetailsChangeDto();
        dto.setTin(customer.getTin());
        dto.setDate(LocalDate.now());
        dto.setCompanyName(customer.getCompanyName());
        customerEmployeeRepository.findFirstByCustomerIdAndRole(customerId, Role.DIRECTOR).ifPresent(customerEmployee -> dto.setDirector(customerEmployee.getName()));
        dto.setBankName(bank.getNameNotNullable());
        dto.setOked(this.oked);
        dto.setBusinessType(customer.getBusinessType().getName());
        dto.setDeviceSerialNumbers(Collections.singletonList(this.deviceSerialNumber));
        dto.setDeviceModelName(device.getModel().getName());
        dto.setAccountNumber(accountNumber);
        dto.setMfo(bank.getCode());
        return dto;
    }

    @JsonIgnore
    @Override
    public boolean is2Step() {
        return false;
    }
}
