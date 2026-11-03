package uz.uzkassa.cts.dto.cases.caseproperty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.uzkassa.cts.domain.Contact;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.domain.Reference;
import uz.uzkassa.cts.dto.cases.CaseSmartposLoginChangeDto;
import uz.uzkassa.cts.dto.cases.request.PhoneNumberChangeDto;
import uz.uzkassa.cts.enums.ReferenceType;
import uz.uzkassa.cts.enums.ReferenceTypeValue;
import uz.uzkassa.cts.enums.Role;
import uz.uzkassa.cts.integrations.SmartposClient;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.service.CacheService;
import uz.uzkassa.cts.service.ThymeleafService;
import uz.uzkassa.cts.web.rest.errors.BadRequestException;
import uz.uzkassa.cts.web.rest.errors.NotFoundException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Service
public class PhoneNumberProperty implements CaseAction {

    @Autowired
    @JsonIgnore
    private CustomerRepository customerRepository;
    @Autowired
    @JsonIgnore
    private UserRepository userRepository;
    @Autowired
    @JsonIgnore
    private CacheService cacheService;
    @Autowired
    @JsonIgnore
    private ThymeleafService thymeleafService;
    @Autowired
    @JsonIgnore
    private SmartposClient smartposClient;
    @Autowired
    @JsonIgnore
    private CustomerEmployeeRepository customerEmployeeRepository;
    @Autowired
    @JsonIgnore
    private ContactRepository contactRepository;
    @Autowired
    @JsonIgnore
    private ReferenceRepository referenceRepository;


    @NotNull
    private Long customerId;
    @NotBlank
    private String phoneNumber;
    @NotBlank
    private String currentPhoneNumber;
    private Boolean changeInSmartpos = true;

    @Override
    public void accept() {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException(CUSTOMER));

        if (!customer.getPhone().equals(currentPhoneNumber)) {
            throw new BadRequestException("Текущий номер телефона не принадлежит этому пользователю: " + currentPhoneNumber);
        }
        if (customerRepository.existsByPhone(phoneNumber)) {
            throw new BadRequestException("Пользователь с таким номером телефона уже существует: " + phoneNumber);
        }
        customer.setPhone(phoneNumber);
        customerRepository.save(customer);
        Reference phoneType = referenceRepository.findFirstByTypeAndCodeAndDeletedIsFalse(ReferenceType.PHONE_TYPE, ReferenceTypeValue.WORK.name());
        Contact contact = contactRepository.findFirstByEntityTypeAndEntityIdAndTypeIdAndPrimary(customer.getEntityType(), customer.getEntityId(), phoneType.getId(), true);
        if (contact == null) {
            contact = new Contact();
        }
        contact.setName(phoneNumber);
        contact.setTypeId(phoneType.getId());
        contact.setPrimary(true);
        contact.setEntityId(customer.getEntityId());
        contact.setEntityType(customer.getEntityType());
        contactRepository.save(contact);

        if (changeInSmartpos) {
            smartposClient.changeLogin(new CaseSmartposLoginChangeDto(currentPhoneNumber, phoneNumber));
        }
    }

    @Override
    public List<String> generateTemplate() {
        return Collections.singletonList(thymeleafService.phoneChangeStatement(map()));
    }

    private PhoneNumberChangeDto map() {
        Customer customer = customerRepository.findById(customerId).orElseThrow();
        PhoneNumberChangeDto dto = new PhoneNumberChangeDto();
        dto.setPhoneNumber(currentPhoneNumber);
        dto.setNewPhoneNumber(phoneNumber);
        dto.setTin(customer.getTin());
        dto.setDate(LocalDate.now());
        dto.setCompanyName(customer.getCompanyName());
        customerEmployeeRepository.findFirstByCustomerIdAndRole(customerId, Role.DIRECTOR).ifPresent(customerEmployee -> dto.setDirector(customerEmployee.getName()));
        dto.setBusinessType(customer.getBusinessType().getName());
        dto.setChangeInSmartpos(changeInSmartpos);
        return dto;
    }

    @Override
    @JsonIgnore
    public boolean is2Step() {
        return false;
    }
}
