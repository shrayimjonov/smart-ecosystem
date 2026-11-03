package uz.uzkassa.cts.dto.cases.caseproperty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.domain.CustomerBank;
import uz.uzkassa.cts.dto.cases.request.BankDetailsDto;
import uz.uzkassa.cts.enums.Role;
import uz.uzkassa.cts.repository.CustomerEmployeeRepository;
import uz.uzkassa.cts.service.BankService;
import uz.uzkassa.cts.service.CustomerOldService;
import uz.uzkassa.cts.service.ThymeleafService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Component
public class BankDetailsProperty implements CaseAction {
    @Autowired
    @JsonIgnore
    private BankService bankService;
    @Autowired
    @JsonIgnore
    private ThymeleafService thymeleafService;
    @Autowired
    @JsonIgnore
    private CustomerOldService customerOldService;
    @Autowired
    @JsonIgnore
    private CustomerEmployeeRepository customerEmployeeRepository;


    @NotNull
    private Long customerId;
    @NotNull
    private Long bankId;
    @NotBlank
    private String bankName;
    @NotBlank
    private String mfo;
    private String accountNumber;
    private String oked;

    @Override
    public void accept() {
        Customer customer = customerOldService.findById(customerId);
        CustomerBank customerBank = customer.getCustomerBank();
        customerBank.setBankId(bankId);
        if (StringUtils.isNotEmpty(accountNumber)) {
            customerBank.setAccountNumber(StringUtils.replace(accountNumber, " ", ""));
        }
        customer.setOked(oked);
        customerOldService.save(customer);
    }

    @Override
    public List<String> generateTemplate() {
        return Collections.singletonList(thymeleafService.bankDetailsChangeStatement(map()));
    }

    private BankDetailsDto map() {
        Customer customer = customerOldService.findById(customerId);
        BankDetailsDto dto = new BankDetailsDto();
        dto.setTin(customer.getTin());
        dto.setDate(LocalDate.now());
        dto.setCompanyName(customer.getCompanyName());
        customerEmployeeRepository.findFirstByCustomerIdAndRole(customerId, Role.DIRECTOR).ifPresent(customerEmployee -> dto.setDirector(customerEmployee.getName()));
        dto.setMfo(mfo);
        dto.setBankName(bankName);
        dto.setAccountNumber(accountNumber);
        dto.setOked(this.oked);
        dto.setBusinessType(customer.getBusinessType().getName());
        return dto;
    }

    @JsonIgnore
    @Override
    public boolean is2Step() {
        return false;
    }
}
