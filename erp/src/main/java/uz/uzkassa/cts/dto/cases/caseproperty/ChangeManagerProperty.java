package uz.uzkassa.cts.dto.cases.caseproperty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.domain.cases.Cases;
import uz.uzkassa.cts.dto.cases.request.ManagerChangeDto;
import uz.uzkassa.cts.enums.Role;
import uz.uzkassa.cts.repository.CustomerEmployeeRepository;
import uz.uzkassa.cts.repository.CustomerRepository;
import uz.uzkassa.cts.service.CaseService;
import uz.uzkassa.cts.service.SmartposService;
import uz.uzkassa.cts.service.ThymeleafService;
import uz.uzkassa.cts.web.rest.errors.NotFoundException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Component
public class ChangeManagerProperty implements CaseAction {
    @Autowired
    @JsonIgnore
    private CaseService caseService;
    @Autowired
    @JsonIgnore
    private CustomerRepository customerRepository;
    @Autowired
    @JsonIgnore
    private CustomerEmployeeRepository customerEmployeeRepository;
    @Autowired
    @JsonIgnore
    private ThymeleafService thymeleafService;
    @Autowired
    @JsonIgnore
    private SmartposService smartposService;


    @NotNull
    private Long customerId;
    @NotNull
    private Long referenceId;
    @NotBlank
    private String phoneNumber;
    private String reason;
    private Long caseId;
    private String oldManagerName;
    private String newManagerName;

    @Override
    @Transactional
    public void accept() {
        Cases cases = caseService.findById(caseId);
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new NotFoundException(CUSTOMER));
        customer.setManagerId(cases.getCreatorId());
        customer.setManager(cases.getCreator());
        customerRepository.save(customer);
        if (cases.getCreator() != null) {
            smartposService.attachManager(List.of(customer.getTin()), cases.getCreator());
        }
    }

    @Override
    public List<String> generateTemplate() {
        return Collections.singletonList(thymeleafService.managerChangeStatement(map()));
    }

    private ManagerChangeDto map() {
        Cases cases = caseService.findById(caseId);
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new NotFoundException(CUSTOMER));
        ManagerChangeDto dto = new ManagerChangeDto();
        customerEmployeeRepository.findFirstByCustomerIdAndRole(customerId, Role.DIRECTOR).ifPresent(customerEmployee -> dto.setDirector(customerEmployee.getName()));
        dto.setCompanyName(customer.getCompanyName());
        dto.setTin(customer.getTin());
        dto.setBusinessType(customer.getBusinessType().getName());
        dto.setManagerName(cases.getCreator().getName());
        dto.setReason(reason);
        dto.setPhoneNumber(phoneNumber);
        dto.setDate(LocalDate.now());
        return dto;
    }

    @Override
    @JsonIgnore
    public boolean is2Step() {
        return false;
    }
}
