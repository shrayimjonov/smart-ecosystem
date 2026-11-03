package uz.uzkassa.cts.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.Bank;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.domain.Reference;
import uz.uzkassa.cts.domain.address.Region;
import uz.uzkassa.cts.dto.address.base.BaseAddressDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.contact.ContactDTO;
import uz.uzkassa.cts.dto.customer.CustomerDTO;
import uz.uzkassa.cts.dto.customerbank.CustomBankDTO;
import uz.uzkassa.cts.dto.customeremployee.CustomerEmployeeDTO;
import uz.uzkassa.cts.dto.sync.ActivityTypeSyncDTO;
import uz.uzkassa.cts.dto.sync.AddressSyncDTO;
import uz.uzkassa.cts.dto.sync.BankSyncDTO;
import uz.uzkassa.cts.dto.sync.customer.CustomerDirectorSyncDTO;
import uz.uzkassa.cts.dto.sync.customer.CustomerSyncDTO;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.filter.CustomerFilter;
import uz.uzkassa.cts.integrations.ApayClient;
import uz.uzkassa.cts.integrations.HorecaClient;
import uz.uzkassa.cts.integrations.SupplyClient;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.service.*;
import uz.uzkassa.cts.web.rest.errors.BadRequestException;

import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class CustomerSyncServiceImpl extends BaseServiceImpl<Customer, Long> implements CustomerSyncService {

    private final CustomerRepository customerRepository;
    private final ReferenceRepository referenceRepository;
    private final VatRepository vatRepository;
    private final ActivityTypeRepository activityTypeRepository;
    private final RegionRepository regionRepository;
    private final DistrictRepository districtRepository;
    private final NeighborhoodRepository neighborhoodRepository;
    private final CustomerBankService customerBankService;
    private final BankRepository bankRepository;
    private final CustomerService customerService;
    private final HorecaClient horecaClient;
    private final ApayClient apayClient;
    private final SupplyClient supplyClient;
    private final RelationService relationService;
    private final CustomerEmployeeRepository customerEmployeeRepository;
    private final CustomerEmployeeService customerEmployeeService;

    public CustomerSyncServiceImpl(CustomerRepository customerRepository, ReferenceRepository referenceRepository, VatRepository vatRepository, ActivityTypeRepository activityTypeRepository, RegionRepository regionRepository, DistrictRepository districtRepository, NeighborhoodRepository neighborhoodRepository, CustomerBankService customerBankService, BankRepository bankRepository, CustomerService customerService, HorecaClient horecaClient, ApayClient apayClient, SupplyClient supplyClient, RelationService relationService, CustomerEmployeeRepository customerEmployeeRepository, CustomerEmployeeService customerEmployeeService) {
        super(customerRepository);
        this.customerRepository = customerRepository;
        this.referenceRepository = referenceRepository;
        this.vatRepository = vatRepository;
        this.activityTypeRepository = activityTypeRepository;
        this.regionRepository = regionRepository;
        this.districtRepository = districtRepository;
        this.neighborhoodRepository = neighborhoodRepository;
        this.customerBankService = customerBankService;
        this.bankRepository = bankRepository;
        this.customerService = customerService;
        this.horecaClient = horecaClient;
        this.apayClient = apayClient;
        this.supplyClient = supplyClient;
        this.relationService = relationService;
        this.customerEmployeeRepository = customerEmployeeRepository;
        this.customerEmployeeService = customerEmployeeService;
    }

    @Override
    public Page<CustomerSyncDTO> getList(CustomerFilter filter) {
        ResultList<Customer> resultList = customerRepository.getResultList(filter);
        List<CustomerSyncDTO> result = resultList
                .getList()
                .stream()
                .map(this::wrapToSyncDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    public CustomerSyncDTO get(String tin) {
        return this.wrapToSyncDTO(customerRepository.findByTin(tin));
    }

    @Override
    @Transactional
    public Long sync(CustomerSyncDTO customerSyncDTO) {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setFromQuickRegister(true);
        customerDTO.setTin(customerSyncDTO.getTin());
        customerDTO.setPinfl(customerSyncDTO.getPinfl());
        customerDTO.setName(customerSyncDTO.getName());
        customerDTO.setCompanyName(customerSyncDTO.getCompanyName());
        customerDTO.setOked(customerSyncDTO.getOked());
        if (StringUtils.isNotEmpty(customerSyncDTO.getBusinessType())) {
            customerDTO.setBusinessType(BusinessType.valueOf(customerSyncDTO.getBusinessType()));
        }
        if (StringUtils.isNotEmpty(customerSyncDTO.getPhone())) {
            Reference phoneType = referenceRepository.findFirstByTypeAndCodeAndDeletedIsFalse(ReferenceType.PHONE_TYPE, ReferenceTypeValue.WORK.name());
            ContactDTO contactDTO = new ContactDTO();
            contactDTO.setName(customerSyncDTO.getPhone());
            contactDTO.setTypeId(phoneType.getId());
            contactDTO.setPrimary(true);
            customerDTO.setPhones(List.of(contactDTO));
        }
        if (customerSyncDTO.getVatRate() != null) {
            vatRepository.findFirstByPercent(customerSyncDTO.getVatRate().setScale(2, RoundingMode.HALF_UP)).ifPresent(vat -> customerDTO.setVatId(vat.getId()));
        }
        if (customerSyncDTO.getActivityType() != null && customerSyncDTO.getActivityType().getCode() != null && customerSyncDTO.getActivityType().getParentCode() != null) {
            activityTypeRepository.findByCodeAndParent_Code(customerSyncDTO.getActivityType().getCode(), customerSyncDTO.getActivityType().getParentCode()).ifPresent(activityType -> customerDTO.setActivityTypeId(activityType.getId()));
        }
        BaseAddressDTO baseAddressDTO = new BaseAddressDTO();
        Region region = regionRepository.findByCode(customerSyncDTO.getAddress().getRegionCode());
        baseAddressDTO.setRegionId(region.getId());

        if (customerSyncDTO.getAddress().getDistrictCode() != null) {
            districtRepository.findFirstByRegionIdAndCode(region.getId(), customerSyncDTO.getAddress().getDistrictCode()).ifPresent(district -> baseAddressDTO.setDistrictId(district.getId()));
        }
        if (customerSyncDTO.getAddress().getNeighborhoodCode() != null) {
            neighborhoodRepository.findByCode(customerSyncDTO.getAddress().getNeighborhoodCode()).ifPresent(neighborhood -> baseAddressDTO.setNeighborhoodId(neighborhood.getId()));
        }
        baseAddressDTO.setLandmark(customerSyncDTO.getAddress().getLandmark());
        baseAddressDTO.setStreet(customerSyncDTO.getAddress().getStreet());
        baseAddressDTO.setHouse(customerSyncDTO.getAddress().getHouse());
        baseAddressDTO.setApartment(customerSyncDTO.getAddress().getApartment());
        baseAddressDTO.setLongitude(customerSyncDTO.getAddress().getLongitude());
        baseAddressDTO.setLatitude(customerSyncDTO.getAddress().getLatitude());
        customerDTO.setAddress(baseAddressDTO);

        Customer customer = customerRepository.findByTin(customerSyncDTO.getTin());
        if (customer == null) {
            customer = customerService.createEntity(customerDTO);
        } else {
            customer = customerService.updateEntity(customer.getId(), customerDTO);
        }

        if (customerSyncDTO.getDirector() != null) {
            CustomerEmployeeDTO customerEmployeeDTO = new CustomerEmployeeDTO();
            customerEmployeeDTO.setFirstName(customerSyncDTO.getDirector().getFirstName());
            customerEmployeeDTO.setLastName(customerSyncDTO.getDirector().getLastName());
            customerEmployeeDTO.setPatronymic(customerSyncDTO.getDirector().getPatronymic());
            customerEmployeeDTO.setRole(Role.DIRECTOR);
            customerEmployeeDTO.setCustomerId(customer.getId());

            customerEmployeeRepository.findFirstByCustomerIdAndRole(customer.getId(), Role.DIRECTOR).ifPresentOrElse(customerEmployee -> customerEmployeeService.update(customerEmployee.getId(), customerEmployeeDTO), () -> customerEmployeeService.create(customerEmployeeDTO));
        }
        if (customerSyncDTO.getBank() != null && StringUtils.isNotEmpty(customerSyncDTO.getBank().getMfo())) {
            Bank bank = bankRepository.findByCode(customerSyncDTO.getBank().getMfo()).orElseThrow(validationService.entityNotFoundExceptionThrow(BANK));
            CustomBankDTO customBankDTO = new CustomBankDTO();
            customBankDTO.setCustomerId(customer.getId());
            customBankDTO.setDefault(true);
            customBankDTO.setAccountNumber(customerSyncDTO.getBank().getAccountNumber());
            customBankDTO.setBankId(bank.getId());
            if (customer.getCustomerBankId() == null) {
                customer.setCustomerBankId(customerBankService.create(customBankDTO));
            } else {
                customerBankService.update(customer.getCustomerBankId(), customBankDTO);
            }
        }
        customerRepository.save(customer);
        relationService.create(customer.getId(), RelationType.CTS, customerSyncDTO.getRelationId(), RelationType.valueOf(customerSyncDTO.getRelationType()), EntityTypeEnum.CUSTOMER);
        return customer.getId();
    }

    @Override
    @Transactional
    public Long sync(Long id, RelationType relationType) {
        Customer customer = customerRepository.getReferenceById(id);
        CustomerSyncDTO customerSyncDTO = this.wrapToSyncDTO(customer);
        switch (relationType) {
            case HORECA -> {
                Long toId = horecaClient.syncCompany(customerSyncDTO);
                relationService.create(customer.getId(), RelationType.CTS, toId.toString(), relationType, customer.getEntityType());
            }
            case A_PAY -> {
                String toId = apayClient.syncCompany(customerSyncDTO);
                relationService.create(customer.getId(), RelationType.CTS, toId, relationType, customer.getEntityType());
            }
            case SUPPLY -> {
                Long toId = supplyClient.syncCompany(customerSyncDTO);
                relationService.create(customer.getId(), RelationType.CTS, toId.toString(), relationType, customer.getEntityType());
            }
            default -> throw new BadRequestException("Unexpected value: " + relationType);
        }
        return id;
    }

    private CustomerSyncDTO wrapToSyncDTO(Customer customer) {
        CustomerSyncDTO customerSyncDTO = new CustomerSyncDTO();
        customerSyncDTO.setId(customer.getId());
        customerSyncDTO.setName(customer.getName());
        customerSyncDTO.setTin(customer.getTin());
        customerSyncDTO.setCompanyName(customer.getCompanyName());
        customerSyncDTO.setPhone(customer.getPhone());
        customerSyncDTO.setOked(customer.getOked());
        customerSyncDTO.setVatRate(customer.getVatId() != null ? customer.getVat().getPercent() : null);
        customerSyncDTO.setBusinessType(customer.getBusinessType().getCode());
        if (customer.getActivityTypeId() != null) {
            customerSyncDTO.setActivityType(new ActivityTypeSyncDTO(customer.getActivityType().getCode(), customer.getActivityType().getParent().getCode()));
        }

        AddressSyncDTO addressSyncDTO = new AddressSyncDTO();
        addressSyncDTO.setRegionCode(customer.getAddress().getRegion().getCode());
        addressSyncDTO.setDistrictCode(customer.getAddress().getDistrict().getCode());
        if (customer.getAddress().getNeighborhoodId() != null) {
            addressSyncDTO.setNeighborhoodCode(customer.getAddress().getNeighborhood().getCode());
        }
        addressSyncDTO.setLandmark(customer.getAddress().getLandmark());
        addressSyncDTO.setStreet(customer.getAddress().getStreet());
        addressSyncDTO.setHouse(customer.getAddress().getHouse());
        addressSyncDTO.setApartment(customer.getAddress().getApartment());
        addressSyncDTO.setLongitude(customer.getAddress().getLongitude());
        addressSyncDTO.setLatitude(customer.getAddress().getLatitude());
        customerSyncDTO.setAddress(addressSyncDTO);

        customerEmployeeRepository.findFirstByCustomerIdAndRole(customer.getId(), Role.DIRECTOR).ifPresent(customerEmployee -> {
            CustomerDirectorSyncDTO directorDTO = new CustomerDirectorSyncDTO();
            directorDTO.setFirstName(customerEmployee.getFirstName());
            directorDTO.setLastName(customerEmployee.getLastName());
            directorDTO.setPatronymic(customerEmployee.getPatronymic());
            directorDTO.setPhone(customer.getPhone());
            directorDTO.setUsername(customer.getPhone());
            customerSyncDTO.setDirector(directorDTO);
        });

        if (customer.getManager() != null) {
            CustomerDirectorSyncDTO managerDTO = new CustomerDirectorSyncDTO();
            managerDTO.setId(customer.getManager().getId());
            managerDTO.setFirstName(customer.getManager().getFirstName());
            managerDTO.setLastName(customer.getManager().getLastName());
            managerDTO.setPatronymic(customer.getManager().getPatronymic());
            managerDTO.setPhone(customer.getManager().getPhone());
            managerDTO.setUsername(customer.getManager().getLogin());
            customerSyncDTO.setManager(managerDTO);
        }
        if (customer.getCustomerBankId() != null) {
            customerSyncDTO.setBank(new BankSyncDTO(customer.getCustomerBank().getBank().getCode(), customer.getCustomerBank().getAccountNumber()));
        }
        return customerSyncDTO;
    }
}
