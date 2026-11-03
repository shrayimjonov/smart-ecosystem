package uz.uzkassa.cts.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.domain.Fiscalization;
import uz.uzkassa.cts.domain.OfdApplication;
import uz.uzkassa.cts.domain.Reference;
import uz.uzkassa.cts.domain.address.BaseAddress;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.dto.auth.CustomerActivationDTO;
import uz.uzkassa.cts.dto.auth.UsernameDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.company.CompanyCommonDTO;
import uz.uzkassa.cts.dto.contact.ContactDTO;
import uz.uzkassa.cts.dto.customer.CustomerDTO;
import uz.uzkassa.cts.dto.customer.CustomerQuickDTO;
import uz.uzkassa.cts.dto.customer.admin.CustomerDetailAdminDTO;
import uz.uzkassa.cts.dto.customer.admin.CustomerListAdminDTO;
import uz.uzkassa.cts.dto.customer.admin.CustomerStatisticsAdminDTO;
import uz.uzkassa.cts.dto.customer.cabinet.CustomerDetailCabinetDTO;
import uz.uzkassa.cts.dto.customer.cabinet.CustomerListCabinetDTO;
import uz.uzkassa.cts.dto.customerRequest.CustomerRequestDTO;
import uz.uzkassa.cts.dto.customeremployee.CustomerEmployeeDTO;
import uz.uzkassa.cts.dto.user.PhoneDTO;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.filter.CustomerFilter;
import uz.uzkassa.cts.integrations.SoliqClient;
import uz.uzkassa.cts.mappings.AddressMapper;
import uz.uzkassa.cts.mappings.BankTariffMapper;
import uz.uzkassa.cts.mappings.VatMapper;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqSmartPosProducer;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.*;
import uz.uzkassa.cts.validator.ValidationContext;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class CustomerServiceImpl extends BaseServiceImpl<Customer, Long> implements CustomerService {

    private final CustomerRepository customerRepository;
    private final ContactService contactService;
    private final CustomerEmployeeService customerEmployeeService;
    private final ReferenceRepository referenceRepository;
    private final SoliqClient soliqClient;
    private final BranchRepository branchRepository;
    private final ActivityTypeRepository activityTypeRepository;
    private final VatRepository vatRepository;
    private final UserRepository userRepository;
    private final RegionRepository regionRepository;
    private final DistrictRepository districtRepository;
    private final NeighborhoodRepository neighborhoodRepository;
    private final FiscalizationRepository fiscalizationRepository;
    private final OfdApplicationRepository ofdApplicationRepository;
    private final RabbitMqSmartPosProducer rabbitMqSmartPosProducer;
    private final AddressMapper addressMapper;
    private final VatMapper vatMapper;
    private final BankTariffMapper bankTariffMapper;
    private final CustomerBankRepository customerBankRepository;
    private final CustomerRequestService customerRequestService;
    private final UserAuthService userAuthService;
    private final UserAuthRepository userAuthRepository;
    private final BillingClientService billingClientService;
    private final CustomerBranchRepository customerBranchRepository;
    private final CustomerBranchDeviceRepository customerBranchDeviceRepository;
    private final CustomerBranchFiscalModuleRepository customerBranchFiscalModuleRepository;
    private final ValidationContext validationContext;

    public CustomerServiceImpl(CustomerRepository customerRepository, ContactService contactService, CustomerEmployeeService customerEmployeeService,
                               ReferenceRepository referenceRepository, SoliqClient soliqClient, BranchRepository branchRepository,
                               ActivityTypeRepository activityTypeRepository, VatRepository vatRepository, UserRepository userRepository,
                               RegionRepository regionRepository, DistrictRepository districtRepository,
                               NeighborhoodRepository neighborhoodRepository, FiscalizationRepository fiscalizationRepository,
                               OfdApplicationRepository ofdApplicationRepository, RabbitMqSmartPosProducer rabbitMqSmartPosProducer,
                               AddressMapper addressMapper, VatMapper vatMapper, BankTariffMapper bankTariffMapper, CustomerBankRepository customerBankRepository,
                               CustomerRequestService customerRequestService, UserAuthService userAuthService, UserAuthRepository userAuthRepository,
                               BillingClientService billingClientService, CustomerBranchRepository customerBranchRepository, CustomerBranchDeviceRepository customerBranchDeviceRepository, CustomerBranchFiscalModuleRepository customerBranchFiscalModuleRepository, ValidationContext validationContext) {
        super(customerRepository);
        this.customerRepository = customerRepository;
        this.contactService = contactService;
        this.customerEmployeeService = customerEmployeeService;
        this.referenceRepository = referenceRepository;
        this.soliqClient = soliqClient;
        this.branchRepository = branchRepository;
        this.activityTypeRepository = activityTypeRepository;
        this.vatRepository = vatRepository;
        this.userRepository = userRepository;
        this.regionRepository = regionRepository;
        this.districtRepository = districtRepository;
        this.neighborhoodRepository = neighborhoodRepository;
        this.fiscalizationRepository = fiscalizationRepository;
        this.ofdApplicationRepository = ofdApplicationRepository;
        this.rabbitMqSmartPosProducer = rabbitMqSmartPosProducer;
        this.addressMapper = addressMapper;
        this.vatMapper = vatMapper;
        this.bankTariffMapper = bankTariffMapper;
        this.customerBankRepository = customerBankRepository;
        this.customerRequestService = customerRequestService;
        this.userAuthService = userAuthService;
        this.userAuthRepository = userAuthRepository;
        this.billingClientService = billingClientService;
        this.customerBranchRepository = customerBranchRepository;
        this.customerBranchDeviceRepository = customerBranchDeviceRepository;
        this.customerBranchFiscalModuleRepository = customerBranchFiscalModuleRepository;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Customer createEntity(CustomerDTO customerDTO) {
        validationContext.validate(customerDTO);

        String phone = customerDTO.getPhones().stream().filter(ContactDTO::isPrimary).toList().getFirst().getName();
        Customer customer = new Customer();
        customer.setTin(customerDTO.getTin());
        customer.setOldTin(customerDTO.getTin());
        customer.setName(customerDTO.getName());
        customer.setCompanyName(customerDTO.getCompanyName());
        customer.setOked(customerDTO.getOked());
        customer.setPhone(phone);
        customer.setActivityTypeId(customerDTO.getActivityTypeId());
        customer.setBusinessType(customerDTO.getBusinessType());
        if (customerDTO.getAddress() != null) {
            customer.setBaseAddress(addressMapper.toEntity(customerDTO.getAddress()));
        }
        customer.setStatus(customerDTO.isFromQuickRegister() ? CustomerStatus.DRAFT : CustomerStatus.PENDING);
        if (customerDTO.getVatId() != null && customerDTO.getVatId() > 0) {
            customer.setVatId(customerDTO.getVatId());
        }
        if (SecurityUtils.isCurrentUserInRole(Role.OPERATOR.getRoleCode())) {
            customer.setManagerId(Optional.ofNullable(customerDTO.getManagerId()).orElseGet(SecurityUtils::getCurrentUserId));
        }
        customer.setAppType(Optional.ofNullable(customerDTO.getAppType()).orElse(CustomerAppType.CTS));
        if (CollectionUtils.isNotEmpty(customerDTO.getBranches())) {
            customer.setBranchId(customerDTO.getBranches().getFirst());
            customer.setBranches(new HashSet<>(branchRepository.findAllByIdIn(customerDTO.getBranches())));
        }
        customer.setCompanyId(getCompanyId());
        customerRepository.save(customer);
        contactService.createBatch(customer.getEntityType(), customer.getEntityId(), customerDTO.getPhones());
        if (customerDTO.getDirector() != null) {
            CustomerEmployeeDTO customerEmployeeDTO = new CustomerEmployeeDTO();
            customerEmployeeDTO.setCustomerId(customer.getId());
            customerEmployeeDTO.setRole(Role.DIRECTOR);
            customerEmployeeDTO.setFirstName(customerDTO.getDirector().getFirstName());
            customerEmployeeDTO.setLastName(customerDTO.getDirector().getLastName());
            customerEmployeeDTO.setPatronymic(customerDTO.getDirector().getPatronymic());
            customerEmployeeService.create(customerEmployeeDTO);
        }
        if (customerDTO.getAccountant() != null) {
            CustomerEmployeeDTO customerEmployeeDTO = new CustomerEmployeeDTO();
            customerEmployeeDTO.setCustomerId(customer.getId());
            customerEmployeeDTO.setRole(Role.ACCOUNTANT);
            customerEmployeeDTO.setFirstName(customerDTO.getAccountant().getFirstName());
            customerEmployeeDTO.setLastName(customerDTO.getAccountant().getLastName());
            customerEmployeeDTO.setPatronymic(customerDTO.getAccountant().getPatronymic());
            customerEmployeeService.create(customerEmployeeDTO);
        }

        CustomerRequestDTO customerRequestDTO = new CustomerRequestDTO();
        customerRequestDTO.setCustomerId(customer.getId());
        customerRequestDTO.setRequestType(CustomerRequestType.NEW_CUSTOMER);
        customerRequestService.create(customerRequestDTO);

        return customer;
    }

    @Override
    @Transactional
    public Long create(CustomerDTO customerDTO) {
        return this.createEntity(customerDTO).getId();
    }

    @Override
    @Transactional
    public Long quickRegister(CustomerQuickDTO customerQuickDTO) {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setFromQuickRegister(true);
        customerDTO.setTin(customerQuickDTO.getTin());
        customerDTO.setPinfl(customerQuickDTO.getPinfl());
        customerDTO.setName(Optional.ofNullable(customerQuickDTO.getName()).orElse(customerQuickDTO.getCompanyName()));
        customerDTO.setCompanyName(customerQuickDTO.getCompanyName());
        customerDTO.setAddress(customerQuickDTO.getAddress());
        customerDTO.setDirector(customerQuickDTO.getDirector());
        Reference phoneType = referenceRepository.findFirstByTypeAndCodeAndDeletedIsFalse(ReferenceType.PHONE_TYPE, ReferenceTypeValue.WORK.name());
        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setPrimary(true);
        contactDTO.setTypeId(phoneType.getId());
        contactDTO.setName(customerQuickDTO.getPhone());
        customerDTO.setPhones(Collections.singletonList(contactDTO));
        customerDTO.setBranches(Collections.singletonList(getBranchId()));
        return this.createEntity(customerDTO).getId();
    }

    @Override
    @Transactional
    public Long update(Long id, CustomerDTO customerDTO) {
        return this.updateEntity(id, customerDTO).getId();
    }

    @Override
    @Transactional
    public Customer updateEntity(Long id, CustomerDTO customerDTO) {
        customerDTO.setId(id);
        validationContext.validate(customerDTO);
        String phone = CollectionUtils.isEmpty(customerDTO.getPhones()) ? null : customerDTO.getPhones().stream().filter(ContactDTO::isPrimary).toList().getFirst().getName();
        return customerRepository.findById(id).map(customer -> {
            if (CustomerStatus.DRAFT.equals(customer.getStatus())) {
                customer.setStatus(CustomerStatus.PENDING);
            }
            customer.setName(customerDTO.getName());
            customer.setCompanyName(customerDTO.getCompanyName());
            customer.setOked(customerDTO.getOked());
            if (customerDTO.getActivityTypeId() != null) {
                customer.setActivityTypeId(customerDTO.getActivityTypeId());
                customer.setActivityType(activityTypeRepository.findById(customerDTO.getActivityTypeId()).orElseThrow(validationService.entityNotFoundExceptionThrow(TYPE)));
            }
            customer.setBusinessType(customerDTO.getBusinessType());
            BaseAddress baseAddress = addressMapper.merge(customerDTO.getAddress(), customer.getAddress());
            if (baseAddress.getRegionId() != null) {
                regionRepository.findById(baseAddress.getRegionId()).ifPresent(baseAddress::setRegion);
            }
            if (baseAddress.getDistrictId() != null) {
                districtRepository.findById(baseAddress.getDistrictId()).ifPresent(baseAddress::setDistrict);
            }
            if (baseAddress.getNeighborhoodId() != null) {
                neighborhoodRepository.findById(baseAddress.getNeighborhoodId()).ifPresent(baseAddress::setNeighborhood);
            }
            customer.setBaseAddress(baseAddress);
            if (customerDTO.getVatId() != null) {
                if (customerDTO.getVatId() > 0) {
                    customer.setVatId(customerDTO.getVatId());
                    customer.setVat(vatRepository.findById(customerDTO.getVatId()).orElseThrow(validationService.entityNotFoundExceptionThrow(VAT)));
                } else if (customerDTO.getVatId() == -1L) {
                    customer.setVatId(null);
                }
            }
            if (customerDTO.getManagerId() != null) {
                userRepository.findById(customerDTO.getManagerId()).ifPresent(user -> {
                    customer.setManagerId(user.getId());
                    customer.setManager(user);
                });
            } else {
                customer.setManagerId(null);
                customer.setManager(null);
            }
            if (customerDTO.getAppType() != null) {
                customer.setAppType(customerDTO.getAppType());
            }
            if (CollectionUtils.isNotEmpty(customerDTO.getBranches())) {
                customer.setBranchId(customerDTO.getBranches().getFirst());
                customer.getBranches().clear();
                customer.setBranches(new HashSet<>(branchRepository.findAllByIdIn(customerDTO.getBranches())));
            }
            customer.setPhone(phone);
            customerRepository.save(customer);
            contactService.updateBatch(customer.getEntityType(), customer.getEntityId(), customerDTO.getPhones());

            if (!CollectionUtils.isEmpty(customer.getCustomerBranches())) {
                Fiscalization fiscalization = fiscalizationRepository.findFirstByCustomerIdAndStatus(customer.getId(), DeviceStatus.FISCAL_ACTIVE);
                if (fiscalization != null && fiscalization.getOfdApplicationId() != null) {
                    OfdApplication ofdApplication = ofdApplicationRepository.findById(fiscalization.getOfdApplicationId()).orElse(null);
                    if (ofdApplication != null) {
                        if (OfdStatus.ACCEPT.equals(ofdApplication.getOfdStatus()) ||
                                (OfdStatus.REJECT.equals(ofdApplication.getOfdStatus()) && ApplicationType.DEREGISTER.equals(ofdApplication.getApplicationType()))
                            //Bu degani ro'yxatdan chiqarishga ariza yuborilgan, lekin NIC rad etgan. Ro'yxatdan chiqarmagan. Kassa hali faol.
                        ) {
                            rabbitMqSmartPosProducer.updateCustomer(fiscalization.getId());
                        }
                    }
                }
            }

            return customer;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(CUSTOMER));
    }

    @Override
    public CustomerDetailAdminDTO getForAdmin(Long id) {
        return customerRepository.findById(id).map(customer -> {
            CustomerDetailAdminDTO customerDetailDTO = new CustomerDetailAdminDTO();
            customerDetailDTO.setId(customer.getId());
            customerDetailDTO.setTin(customer.getTin());
            customerDetailDTO.setOldTin(customer.getOldTin());
            customerDetailDTO.setName(customer.getName());
            customerDetailDTO.setCompanyName(customer.getCompanyName());
            customerDetailDTO.setOked(customer.getOked());
            customerDetailDTO.setAppType(customer.getAppType());
            if (customer.getActivityTypeId() != null) {
                customerDetailDTO.setActivityType(customer.getActivityType().toCommonDTO());
                if (customer.getActivityType().getParentId() != null) {
                    customerDetailDTO.setParentActivityType(customer.getActivityType().getParent().toCommonDTO());
                }
            }
            customerDetailDTO.setBusinessType(customer.getBusinessType());
            customerDetailDTO.setAddress(addressMapper.toDetailDTO(customer.getAddress()));
            customerDetailDTO.setVat(vatMapper.toItemDTO(customer.getVat()));
            customerDetailDTO.setCreatedDate(customer.getCreatedDate());
            if (customer.getManagerId() != null) {
                customerDetailDTO.setManager(customer.getManager().toCommonDTO());
            }
            customerDetailDTO.setStatus(customer.getStatus());
            customerDetailDTO.setLegalStatus(customer.getLegalStatus());
            if (customer.getBankTariff() != null) {
                customerDetailDTO.setBankTariff(bankTariffMapper.toDetailDTO(customer.getBankTariff()));
            }
            customerDetailDTO.setApayTariffId(customer.getApayTariffId());
            customerBankRepository.findFirstByCustomerIdAndDefaultIsTrue(id).ifPresent(customerBank -> {
                customerDetailDTO.setBank(customerBank.getBank().toCommonCodeDTO());
                customerDetailDTO.setAccountNumber(customerBank.getAccountNumber());
            });

            List<Long> phoneTypeIds = referenceRepository.findAllIdsByTypeAndDeletedIsFalse(ReferenceType.PHONE_TYPE);
            customerDetailDTO.setPhones(contactService.getList(customer.getEntityType(), customer.getEntityId(), phoneTypeIds));
            customerDetailDTO.setBranches(customer.getBranches().stream().map(SimpleEntity::toCommonDTO).collect(Collectors.toList()));
            if (CustomerStatus.ACTIVE != customer.getStatus()) {
                userAuthRepository.findFirstByUsername(customer.getPhone()).ifPresent(userAuth -> customerDetailDTO.setActivationCode(userAuth.getActivationCode()));
            }
            return customerDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(CUSTOMER));
    }

    @Override
    public CustomerDetailCabinetDTO getForCabinet(Long id) {
        Customer customer = null;
        if (isSuperAdminAndAdmin()) {
            customer = customerRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(CUSTOMER));
        } else if (isOperator()) {
            customer = customerRepository.getByIdAndBranchId(id, getBranchId());
        }
        if (customer == null) {
            throw validationService.forbiddenExceptionThrow(ACCESS_TO_EMPLOYEES).get();
        }

        CustomerDetailCabinetDTO customerDetailDTO = new CustomerDetailCabinetDTO();
        customerDetailDTO.setId(customer.getId());
        customerDetailDTO.setTin(customer.getTin());
        customerDetailDTO.setName(customer.getName());
        customerDetailDTO.setCompanyName(customer.getCompanyName());
        customerDetailDTO.setOked(customer.getOked());
        customerDetailDTO.setAppType(customer.getAppType());
        customerDetailDTO.setCreatedDate(customer.getCreatedDate());
        if (customer.getActivityTypeId() != null) {
            customerDetailDTO.setActivityType(customer.getActivityType().toCommonDTO());
            if (customer.getActivityType().getParentId() != null) {
                customerDetailDTO.setParentActivityType(customer.getActivityType().getParent().toCommonDTO());
            }
        }
        customerDetailDTO.setBusinessType(customer.getBusinessType());
        customerDetailDTO.setAddress(addressMapper.toDetailDTO(customer.getAddress()));
        customerDetailDTO.setVat(vatMapper.toItemDTO(customer.getVat()));
        if (customer.getManagerId() != null) {
            customerDetailDTO.setManager(customer.getManager().toCommonDTO());
        }
        customerDetailDTO.setStatus(customer.getStatus());
        customerDetailDTO.setLegalStatus(customer.getLegalStatus());
        if (customer.getBankTariff() != null) {
            customerDetailDTO.setBankTariff(bankTariffMapper.toDetailDTO(customer.getBankTariff()));
        }
        customerDetailDTO.setApayTariffId(customer.getApayTariffId());
        customerBankRepository.findFirstByCustomerIdAndDefaultIsTrue(id).ifPresent(customerBank -> {
            customerDetailDTO.setBank(customerBank.getBank().toCommonCodeDTO());
            customerDetailDTO.setAccountNumber(customerBank.getAccountNumber());
        });

        List<Long> phoneTypeIds = referenceRepository.findAllIdsByTypeAndDeletedIsFalse(ReferenceType.PHONE_TYPE);
        customerDetailDTO.setPhones(contactService.getList(customer.getEntityType(), customer.getEntityId(), phoneTypeIds));
        customerDetailDTO.setBranches(customer.getBranches().stream().map(SimpleEntity::toCommonDTO).collect(Collectors.toList()));
        return customerDetailDTO;
    }

    @Override
    public Page<CustomerListAdminDTO> getListForAdmin(CustomerFilter filter) {
        ResultList<Customer> resultList = customerRepository.getResultList(filter);
        List<CustomerListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(customer -> {
                    CustomerListAdminDTO customerListDTO = new CustomerListAdminDTO();
                    customerListDTO.setId(customer.getId());
                    customerListDTO.setName(customer.getName());
                    customerListDTO.setCompanyName(customer.getCompanyName());
                    customerListDTO.setTin(customer.getTin());
                    if (customer.getActivityType() != null) {
                        customerListDTO.setActivityType(customer.getActivityType().toCommonDTO());
                    }
                    customerListDTO.setBusinessType(customer.getBusinessType());
                    customerListDTO.setPhone(customer.getPhone());
                    if (customer.getBranch() != null) {
                        customerListDTO.setBranch(customer.getBranch().toCommonDTO());
                    }
                    customerListDTO.setAddress(addressMapper.toShortDTO(customer.getAddress()));
                    customerListDTO.setCreator(customer.getCreator().toCommonDTO());
                    customerListDTO.setCreatedDate(customer.getCreatedDate());
                    customerListDTO.setStatus(customer.getStatus());
                    customerListDTO.setWhiteList(customer.isWhiteList());
                    if (customer.getManagerId() != null) {
                        customerListDTO.setManager(customer.getManager().toCommonDTO());
                    }
                    if (customer.getPaymentDate() != null) {
                        customerListDTO.setDaysFromPayment(ChronoUnit.DAYS.between(LocalDate.now(), customer.getPaymentDate()));
                    }
                    return customerListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public Page<CustomerListCabinetDTO> getListForCabinet(CustomerFilter filter) {
        if (isOperator()) {
            filter.setBranchId(Optional.ofNullable(filter.getBranchId()).orElseGet(BaseServiceImpl::getBranchId));
        }
        ResultList<Customer> resultList = customerRepository.getResultList(filter);
        List<CustomerListCabinetDTO> result = resultList
                .getList()
                .stream()
                .map(customer -> {
                    CustomerListCabinetDTO customerListDTO = new CustomerListCabinetDTO();
                    customerListDTO.setId(customer.getId());
                    customerListDTO.setName(customer.getName());
                    customerListDTO.setCompanyName(customer.getCompanyName());
                    customerListDTO.setTin(customer.getTin());
                    if (customer.getActivityType() != null) {
                        customerListDTO.setActivityType(customer.getActivityType().toCommonDTO());
                    }
                    customerListDTO.setBusinessType(customer.getBusinessType());
                    customerListDTO.setPhone(customer.getPhone());
                    customerListDTO.setAddress(addressMapper.toShortDTO(customer.getAddress()));
                    customerListDTO.setStatus(customer.getStatus());
                    return customerListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public List<CompanyCommonDTO> lookUp(CustomerFilter filter) {
        return customerRepository.getResultList(filter).getList().stream().map(Customer::toShortDTO).collect(Collectors.toList());
    }

    @Override
    public void updateStatus(Long id, CustomerStatus status) {
        customerRepository
                .findById(id)
                .ifPresentOrElse(customer -> {
                    customer.setStatus(status);
                    customerRepository.save(customer);
                }, () -> validationService.entityNotFoundExceptionThrow(CUSTOMER).get());
    }

    @Override
    public String getCustomerByTinFromNic(String tin) {
        return soliqClient.getCustomerByTinString(tin);
    }

    @Override
    @Transactional
    public void attachManager(String tin, Long managerId) {
        Customer customer = customerRepository.findFirstByTinOrOldTin(tin, tin);
        if (managerId == null && customer.getManagerId() == null) {
            customer.setManagerId(SecurityUtils.getCurrentUserId());
            userRepository.findById(SecurityUtils.getCurrentUserId()).ifPresent(customer::setManager);
            customerRepository.save(customer);
        } else if (managerId != null) {
            customer.setManagerId(managerId);
            userRepository.findById(managerId).ifPresent(customer::setManager);
            customerRepository.save(customer);
        }
    }

    @Override
    public Customer getByTin(String tin) {
        return customerRepository.findByTin(tin);
    }

    @Override
    @Transactional
    public void updatePhone(Long id, PhoneDTO phoneDTO) {
        Customer customer = customerRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(CUSTOMER));
        if (customerRepository.existsByPhone(phoneDTO.getPhone())) {
            throw validationService.entityAlreadyExistExceptionThrow(CUSTOMER, PHONE, phoneDTO.getPhone()).get();
        }
        customer.setPhone(phoneDTO.getPhone());
        customerRepository.save(customer);
        if (!CollectionUtils.isEmpty(customer.getCustomerBranches())) {
            Fiscalization fiscalization = fiscalizationRepository.findFirstByCustomerIdAndStatus(customer.getId(), DeviceStatus.FISCAL_ACTIVE);
            if (fiscalization != null && fiscalization.getOfdApplicationId() != null) {
                OfdApplication ofdApplication = ofdApplicationRepository.getReferenceById(fiscalization.getOfdApplicationId());
                if (OfdStatus.ACCEPT.equals(ofdApplication.getOfdStatus()) ||
                        (OfdStatus.REJECT.equals(ofdApplication.getOfdStatus()) && ApplicationType.DEREGISTER.equals(ofdApplication.getApplicationType()))
                    //Bu degani ro'yxatdan chiqarishga ariza yuborilgan, lekin NIC rad etgan. Ro'yxatdan chiqarmagan. Kassa hali faol.
                ) {
                    rabbitMqSmartPosProducer.updateCustomer(fiscalization.getId());
                }
            }
        }
    }

    @Override
    @Transactional
    public void whiteList(Long id, boolean whiteList) {
        customerRepository.findById(id).ifPresentOrElse(customer -> {
            customer.setWhiteList(whiteList);
            customerRepository.save(customer);
        }, () -> validationService.entityNotFoundExceptionThrow(CUSTOMER).get());
    }

    @Override
    @Transactional
    public void sendActivationCode(Long id) {
        customerRepository.findById(id).ifPresentOrElse(customer -> {
            if (CustomerAppType.CTS == customer.getAppType()) {
                userAuthService.sendActivationCode(new UsernameDTO(customer.getPhone()), TemplateType.CUSTOMER_ACTIVATION_CTS);
            } else if (CustomerAppType.SMARTPOS == customer.getAppType()) {
                userAuthService.sendActivationCode(new UsernameDTO(customer.getPhone()), TemplateType.CUSTOMER_ACTIVATION_SMARTPOS);
            }
        }, () -> validationService.entityNotFoundExceptionThrow(CUSTOMER).get());
    }

    @Override
    @Transactional
    public void activate(CustomerActivationDTO customerActivationDTO) {
        customerRepository.findById(customerActivationDTO.getId()).ifPresentOrElse(customer -> {
            if (userAuthRepository.existsByUsernameAndActivationCode(customer.getPhone(), customerActivationDTO.getActivationCode())) {
                customer.setStatus(CustomerStatus.ACTIVE);
                customerRepository.save(customer);
                Thread.startVirtualThread(() -> billingClientService.createCustomer(customer));
            } else {
                throw validationService.badRequestExceptionThrow(INCORRECT_ACTIVATION_CODE).get();
            }
        }, () -> validationService.entityNotFoundExceptionThrow(CUSTOMER).get());
    }

    @Override
    public CustomerStatisticsAdminDTO getStatistics(Long id) {
        CustomerStatisticsAdminDTO customerStatisticsAdminDTO = new CustomerStatisticsAdminDTO();
        customerStatisticsAdminDTO.setCustomerBranchCount(customerBranchRepository.countAllByCustomerIdAndDeletedIsFalse(id));
        customerStatisticsAdminDTO.setDeviceCount(customerBranchDeviceRepository.countAllByCustomerId(id));
        customerStatisticsAdminDTO.setFiscalModuleCount(customerBranchFiscalModuleRepository.countAllByCustomerId(id));
        return customerStatisticsAdminDTO;
    }
}
