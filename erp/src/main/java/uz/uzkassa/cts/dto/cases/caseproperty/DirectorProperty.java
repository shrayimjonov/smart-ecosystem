package uz.uzkassa.cts.dto.cases.caseproperty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.domain.CustomerEmployee;
import uz.uzkassa.cts.dto.cases.CaseSmartposOwnerChangeDto;
import uz.uzkassa.cts.dto.cases.request.DirectorChangeDto;
import uz.uzkassa.cts.enums.Role;
import uz.uzkassa.cts.integrations.SmartposClient;
import uz.uzkassa.cts.repository.CustomerEmployeeRepository;
import uz.uzkassa.cts.service.CustomerOldService;
import uz.uzkassa.cts.service.ThymeleafService;
import uz.uzkassa.cts.web.rest.errors.BadRequestException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Component
public class DirectorProperty implements CaseAction {

    @Autowired
    @JsonIgnore
    private CustomerOldService customerOldService;
    @Autowired
    @JsonIgnore
    private ThymeleafService thymeleafService;
    @Autowired
    @JsonIgnore
    private SmartposClient smartposClient;
    @Autowired
    @JsonIgnore
    private CustomerEmployeeRepository customerEmployeeRepository;


    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotBlank
    private String patronymic;
    @NotNull
    private Long customerId;

    @Override
    @Transactional
    public void accept() {
        Customer customer = customerOldService.findById(customerId);
        CustomerEmployee customerEmployee = customerEmployeeRepository.findFirstByCustomerIdAndRole(customerId, Role.DIRECTOR).orElseThrow(() -> new BadRequestException("Director not found with this company"));

        customerEmployee.setFirstName(firstName);
        customerEmployee.setLastName(lastName);
        customerEmployee.setPatronymic(patronymic);
        customerEmployeeRepository.save(customerEmployee);

        //change DIRECTOR in SMARTPOS
        smartposClient.changeOwner(CaseSmartposOwnerChangeDto.builder()
                .phone(customer.getPhone())
                .firstName(firstName)
                .lastName(lastName)
                .patronymic(patronymic)
                .build());
    }

    @Override
    public List<String> generateTemplate() {
        return Collections.singletonList(thymeleafService.directorNameChangeStatement(map()));
    }

    private DirectorChangeDto map() {
        Customer customer = customerOldService.findById(customerId);
        DirectorChangeDto dto = new DirectorChangeDto();
        dto.setTin(customer.getTin());
        dto.setDate(LocalDate.now());
        dto.setCompanyName(customer.getCompanyName());
        customerEmployeeRepository.findFirstByCustomerIdAndRole(customerId, Role.DIRECTOR).ifPresent(customerEmployee -> dto.setDirector(customerEmployee.getName()));
        dto.setBusinessType(customer.getBusinessType().getName());
        dto.setNewDirector(getFullName());
        return dto;
    }

    public String getFullName() {
        StringBuilder fullName = new StringBuilder();
        if (StringUtils.isNotEmpty(getFirstName())) {
            fullName.append(getFirstName().trim()).append(" ");
        }
        if (StringUtils.isNotEmpty(getLastName())) {
            fullName.append(getLastName().trim()).append(" ");
        }
        if (StringUtils.isNotEmpty(getPatronymic())) {
            fullName.append(getPatronymic().trim());
        }
        return fullName.toString().trim();
    }

    @Override
    @JsonIgnore
    public boolean is2Step() {
        return false;
    }
}
