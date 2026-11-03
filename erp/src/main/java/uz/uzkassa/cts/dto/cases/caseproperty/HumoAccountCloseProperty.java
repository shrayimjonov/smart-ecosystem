package uz.uzkassa.cts.dto.cases.caseproperty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.cases.request.HumoConnectionDeviceDto;
import uz.uzkassa.cts.dto.cases.request.HumoConnectionDto;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.service.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
@Component
public class HumoAccountCloseProperty implements CaseAction {

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
    private CaseFileRepository caseFileRepository;
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
    private CustomerEmployeeRepository customerEmployeeRepository;
    @Autowired
    @JsonIgnore
    private ValidationService validationService;
    @Autowired
    @JsonIgnore
    private CustomerBranchDevicePaymentTypeRepository customerBranchDevicePaymentTypeRepository;


    @NotNull
    private Long customerId;
    private Long customerBranchId;
    @NotNull
    @NotEmpty
    private List<String> deviceSerialNumbers;
    private Long caseId;
    @NotNull
    private List<Long> customerBranchDeviceIds;

    @Override
    public void accept() {
        Optional<HumoConnect> optionalHumoConnect = humoConnectRepository.findByCaseId(caseId);
        if (optionalHumoConnect.isEmpty()) {
            Customer customer = customerOldService.findById(customerId);
            HumoConnect humoConnect = new HumoConnect();
            humoConnect.setAddress(customer.getAddress());
            humoConnect.setCustomerId(customerId);
            humoConnect.setDeviceSerialNumber(StringUtils.join(deviceSerialNumbers, ','));
            humoConnect.setAccountNumber(customer.getCustomerBank().getAccountNumber());
            humoConnect.setMfo(customer.getCustomerBank().getBank().getCode());
            humoConnect.setCaseId(caseId);
            humoConnect.setStatus(HumoConnectStatus.DEFAULT);
            humoConnect.setOperatorStatus(HumoConnectOperatorStatus.DEFAULT);
            humoConnect.setCaseCreatorId(caseService.findById(caseId).getCreatorId());
            humoConnect.setBusinessType(customer.getBusinessType());
            humoConnect.setAppType(customer.getAppType());
            humoConnect.setBankType(customer.getBankTariff() != null ? customer.getBankTariff().getBankType() : BankType.HAMKORBANK);
            humoConnect.setDocFlowStatus(humoConnect.getBankType() == BankType.TRASTBANK ? DocFlowStatus.TRANSFERRED_ARCHIVE : DocFlowStatus.NOT_COME);
            humoConnect.setCaseType(CaseType.HUMO_ACCOUNT_CLOSE);
            humoConnect.setCustomerBranchId(customerBranchId);
            caseFileRepository
                    .findByCaseIdAndDocumentType(caseId, DocumentType.APPLICATION)
                    .ifPresent(caseFile -> {
                        humoConnect.setFileUrl(caseFile.getFileUrl());
                        humoConnect.setFileName(caseFile.getFileName());
                    });
            humoConnectRepository.save(humoConnect);
            if (!CollectionUtils.isEmpty(customerBranchDeviceIds)) {
                customerBranchDevicePaymentTypeRepository.findByCustomerBranchDeviceIdInAndPaymentType(customerBranchDeviceIds, PaymentType.HUMO).forEach(customerBranchDevicePaymentType -> {
                    customerBranchDevicePaymentType.setPaymentType(PaymentType.NOT_CONNECTED);
                    customerBranchDevicePaymentTypeRepository.save(customerBranchDevicePaymentType);
                });
            }
        }
    }

    @Override
    public List<String> generateTemplate() {
        Customer customer = customerOldService.findById(customerId);
        BankTariff bankTariff = customer.getBankTariff();
        if (bankTariff != null) {
            if (bankTariff.getBankType() == BankType.TRASTBANK) {
                return Collections.singletonList(thymeleafService.humoCloseAccount(closeMap()));
            } else {
                return Collections.singletonList(thymeleafService.humoCloseAccountHamkor(closeMap()));
            }
        }
        return Collections.singletonList(thymeleafService.humoCloseAccount(closeMap()));
    }

    private HumoConnectionDto closeMap() {
        Customer customer = customerOldService.findById(customerId);
        if (customer.getCustomerBank() == null) {
            throw validationService.entityNotFoundExceptionThrow(BANK, TIN, customer.getTin()).get();
        }
        Bank bank = customer.getCustomerBank().getBank();
        Set<Device> devices = deviceRepository.findBySerialNumberIn(deviceSerialNumbers);
        HumoConnectionDto dto = new HumoConnectionDto();
        dto.setTin(customer.getTin());
        dto.setDate(LocalDate.now());
        dto.setCompanyName(customer.getCompanyName());
        dto.setAddress(customer.getAddress().getFullAddressAddress());
        customerEmployeeRepository.findFirstByCustomerIdAndRole(customerId, Role.DIRECTOR).ifPresent(customerEmployee -> dto.setDirector(customerEmployee.getName()));
        for (Device device : devices) {
            HumoConnectionDeviceDto humoConnectionDeviceDto = new HumoConnectionDeviceDto();
            humoConnectionDeviceDto.setDeviceModelName(device.getModel() != null ? device.getModel().getName() : "");
            humoConnectionDeviceDto.setDeviceSerialNumber(device.getSerialNumber());
            dto.getDevices().add(humoConnectionDeviceDto);
        }
        dto.setMfo(bank.getCode());
        dto.setAccountNumber(customer.getCustomerBank().getAccountNumber());
        dto.setBankName(bank.getNameNotNullable());
        return dto;
    }

    @Override
    public boolean is2Step() {
        return false;
    }
}
