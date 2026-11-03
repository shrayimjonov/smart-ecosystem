package uz.uzkassa.cts.dto.cases.caseproperty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.dto.address.base.BaseAddressDTO;
import uz.uzkassa.cts.dto.cases.request.LegalDataChangeDto;
import uz.uzkassa.cts.dto.contact.ContactDTO;
import uz.uzkassa.cts.dto.customer.CustomerDTO;
import uz.uzkassa.cts.enums.BusinessType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.ReferenceType;
import uz.uzkassa.cts.enums.Role;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.service.CustomerService;
import uz.uzkassa.cts.service.ThymeleafService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Setter
@Component
public class LegalDataProperty implements CaseAction {

    @Autowired
    @JsonIgnore
    private CustomerService customerService;
    @Autowired
    @JsonIgnore
    private ThymeleafService thymeleafService;
    @Autowired
    @JsonIgnore
    private VatRepository vatRepository;
    @Autowired
    @JsonIgnore
    private ActivityTypeRepository activityTypeRepository;
    @Autowired
    @JsonIgnore
    private RegionRepository regionRepository;
    @Autowired
    @JsonIgnore
    private DistrictRepository districtRepository;
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
    @NotNull
    private Long vatId;
    private Long activityTypeId;
    private String name;
    private String companyName;
    private String phone;
    private String businessType;
    private BaseAddressDTO address;
    private List<Long> branches;
    private boolean allowDeviceRegister;


    @Override
    public void accept() {
        customerService.update(customerId, mapToCustomerUpdateDTO());
    }

    private CustomerDTO mapToCustomerUpdateDTO() {
        Customer customer = customerService.findById(customerId);
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(customerId);
        customerDTO.setName(Optional.ofNullable(name).orElseGet(customer::getName));
        customerDTO.setCompanyName(Optional.ofNullable(companyName).orElseGet(customer::getCompanyName));
        customerDTO.setVatId(Optional.ofNullable(vatId).orElseGet(customer::getVatId));
        customerDTO.setBusinessType(StringUtils.isNotEmpty(businessType) ? BusinessType.get(businessType) : customer.getBusinessType());
        customerDTO.setActivityTypeId(Optional.ofNullable(activityTypeId).orElseGet(customer::getActivityTypeId));

        BaseAddressDTO baseAddressDTO = getAddress() != null ? getAddress() : new BaseAddressDTO();
        baseAddressDTO.setRegionId(Optional.ofNullable(baseAddressDTO.getRegionId()).orElseGet(() -> customer.getAddress().getRegionId()));
        baseAddressDTO.setDistrictId(Optional.ofNullable(baseAddressDTO.getDistrictId()).orElseGet(() -> customer.getAddress().getDistrictId()));
        baseAddressDTO.setNeighborhoodId(Optional.ofNullable(baseAddressDTO.getNeighborhoodId()).orElseGet(() -> customer.getAddress().getNeighborhoodId()));
        baseAddressDTO.setLandmark(Optional.ofNullable(baseAddressDTO.getLandmark()).orElseGet(() -> customer.getAddress().getLandmark()));
        baseAddressDTO.setStreet(Optional.ofNullable(baseAddressDTO.getStreet()).orElseGet(() -> customer.getAddress().getStreet()));
        baseAddressDTO.setHouse(Optional.ofNullable(baseAddressDTO.getHouse()).orElseGet(() -> customer.getAddress().getHouse()));
        baseAddressDTO.setApartment(Optional.ofNullable(baseAddressDTO.getApartment()).orElseGet(() -> customer.getAddress().getApartment()));
        baseAddressDTO.setLongitude(Optional.ofNullable(baseAddressDTO.getLongitude()).orElseGet(() -> customer.getAddress().getLongitude()));
        baseAddressDTO.setLatitude(Optional.ofNullable(baseAddressDTO.getLatitude()).orElseGet(() -> customer.getAddress().getLatitude()));
        customerDTO.setAddress(baseAddressDTO);

        List<Long> branches = !CollectionUtils.isEmpty(getBranches()) ? getBranches() : customer.getBranches().stream().map(SimpleEntity::getId).collect(Collectors.toList());
        customerDTO.setBranches(branches);

        List<Long> phoneTypeIds = referenceRepository.findAllIdsByTypeAndDeletedIsFalse(ReferenceType.PHONE_TYPE);
        List<ContactDTO> phones = contactRepository.findAllByEntityTypeAndEntityIdAndTypeIdIn(EntityTypeEnum.CUSTOMER, customerId, phoneTypeIds).stream().map(contact -> {
            ContactDTO contactDTO = new ContactDTO();
            contactDTO.setId(contact.getId());
            contactDTO.setName(contact.getName());
            contactDTO.setTypeId(contact.getTypeId());
            contactDTO.setPrimary(contact.isPrimary());
            if (StringUtils.isNotEmpty(phone) && contact.isPrimary()) {
                contactDTO.setName(phone);
            }
            return contactDTO;

        }).collect(Collectors.toList());
        customerDTO.setPhones(phones);
        return customerDTO;
    }

    @Override
    public List<String> generateTemplate() {
        return Collections.singletonList(thymeleafService.legalDataChangeStatement(map()));
    }

    private LegalDataChangeDto map() {
        Customer customer = customerService.findById(customerId);
        LegalDataChangeDto legalDataChangeDto = new LegalDataChangeDto();
        legalDataChangeDto.setTin(customer.getTin());
        legalDataChangeDto.setDate(LocalDate.now());
        customerEmployeeRepository.findFirstByCustomerIdAndRole(customerId, Role.DIRECTOR).ifPresent(customerEmployee -> legalDataChangeDto.setDirector(customerEmployee.getName()));
        legalDataChangeDto.setName(customer.getName());
        if (!customer.getName().equals(this.getName())) {
            legalDataChangeDto.setNewName(this.getName());
        }
        legalDataChangeDto.setCompanyName(customer.getCompanyName());
        if (!customer.getCompanyName().equals(this.getCompanyName())) {
            legalDataChangeDto.setNewCompanyName(this.getCompanyName());
        }
        legalDataChangeDto.setBusinessType(customer.getBusinessType().getName());
        if (!customer.getBusinessType().getCode().equals(this.getBusinessType())) {
            legalDataChangeDto.setNewBusinessType(this.getBusinessType());
        }
        legalDataChangeDto.setVat(customer.getVat() != null ? customer.getVat().getPercent().toString() : "Без НДС");
        legalDataChangeDto.setAddress(customer.getAddress().getFullAddressAddress());
        if (!customer.getAddress().getFullAddressAddress().equals(getFullAddress())) {
            legalDataChangeDto.setNewAddress(getFullAddress());
        }
        if (Objects.nonNull(customer.getAddress().getNeighborhood())) {
            legalDataChangeDto.setNeighborhoodName(customer.getAddress().getNeighborhood().getName());
        }
        if (!this.getVatId().equals(-1L)) {
            vatRepository
                    .findById(this.vatId)
                    .ifPresent(vat -> legalDataChangeDto.setNewVAT(vat.getPercent().toString()));
        } else {
            legalDataChangeDto.setNewVAT("Без НДС");
        }
        legalDataChangeDto.setActivityType(customer.getActivityType().getNameRu());
        if (this.activityTypeId != null) {
            activityTypeRepository.findById(this.activityTypeId)
                    .ifPresent(activityType -> {
                        if (!Objects.equals(activityType.getId(), customer.getActivityTypeId())) {
                            legalDataChangeDto.setNewActivityType(activityType.getNameRu());
                        }
                    });
        }
        return legalDataChangeDto;
    }


    private String getFullAddress() {
        if (this.address != null && this.address.getRegionId() != null) {
            StringBuilder address = new StringBuilder();
            regionRepository.findById(this.address.getRegionId()).ifPresent(r -> address.append(r.getNameUzLatin()).append(" "));
            districtRepository.findById(this.address.getDistrictId()).ifPresent(d -> address.append(d.getNameUzLatin()).append(" "));
            if (StringUtils.isNotEmpty(this.getAddress().getStreet())) {
                address.append(this.getAddress().getStreet()).append(" ");
            }
            if (StringUtils.isNotEmpty(this.getAddress().getHouse())) {
                address.append(this.getAddress().getHouse()).append(" ");
            }
            if (StringUtils.isNotEmpty(this.getAddress().getApartment())) {
                address.append(this.getAddress().getApartment()).append(" ");
            }
            return address.toString().trim();
        }
        return null;
    }

    @Override
    @JsonIgnore
    public boolean is2Step() {
        return false;
    }
}
