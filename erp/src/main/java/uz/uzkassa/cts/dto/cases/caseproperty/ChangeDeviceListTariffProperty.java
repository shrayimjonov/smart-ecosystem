package uz.uzkassa.cts.dto.cases.caseproperty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.domain.Tariff;
import uz.uzkassa.cts.domain.cases.Cases;
import uz.uzkassa.cts.dto.cases.request.ChangeTariffStatementDto;
import uz.uzkassa.cts.enums.CaseStatus;
import uz.uzkassa.cts.enums.Role;
import uz.uzkassa.cts.repository.CustomerEmployeeRepository;
import uz.uzkassa.cts.service.CaseService;
import uz.uzkassa.cts.service.CustomerOldService;
import uz.uzkassa.cts.service.TariffService;
import uz.uzkassa.cts.service.ThymeleafService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Component
public class ChangeDeviceListTariffProperty implements CaseAction {

    @Autowired
    @JsonIgnore
    private CustomerOldService customerOldService;
    @Autowired
    @JsonIgnore
    private ThymeleafService thymeleafService;
    @Autowired
    @JsonIgnore
    private TariffService tariffService;
    @Autowired
    @JsonIgnore
    private CaseService caseService;
    @Autowired
    @JsonIgnore
    private CustomerEmployeeRepository customerEmployeeRepository;

    public ChangeDeviceListTariffProperty(CustomerOldService customerOldService, ThymeleafService thymeleafService,
                                          TariffService tariffService, CaseService caseService, CustomerEmployeeRepository customerEmployeeRepository) {
        this.customerOldService = customerOldService;
        this.thymeleafService = thymeleafService;
        this.tariffService = tariffService;
        this.caseService = caseService;
        this.customerEmployeeRepository = customerEmployeeRepository;
    }

    @NotNull
    private Long tariffId;
    @NotNull
    private Long newTariffId;
    @NotNull
    private Long customerId;
    private String tariffName;
    private String newTariffName;
    private Long caseId;
    private List<String> deviceSerialNumbers;

    @Override
    public void accept() {
        Cases cases = caseService.findById(caseId);
        cases.setStatus(CaseStatus.ACCEPTED);
        caseService.save(cases);
    }

    @Override
    public List<String> generateTemplate() {
        return Collections.singletonList(thymeleafService.changeDeviceListTariffStatement(map()));
    }

    private ChangeTariffStatementDto map() {
        Customer customer = customerOldService.findById(customerId);
        Tariff tariff = tariffService.findById(tariffId);
        Tariff newTariff = tariffService.findById(newTariffId);
        ChangeTariffStatementDto dto = new ChangeTariffStatementDto();
        dto.setTin(customer.getTin());
        dto.setDate(LocalDate.now());
        dto.setCompanyName(customer.getCompanyName());
        customerEmployeeRepository.findFirstByCustomerIdAndRole(customerId, Role.DIRECTOR).ifPresent(customerEmployee -> dto.setDirector(customerEmployee.getName()));
        dto.setTariffName(tariff.getName());
        dto.setNewTariffName(newTariff.getName());
        dto.setBusinessType(customer.getBusinessType().getName());
        dto.setDeviceSerialNumbers(String.join(", ", deviceSerialNumbers));
        return dto;
    }

    @Override
    public boolean is2Step() {
        return false;
    }
}
