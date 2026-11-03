package uz.uzkassa.smartpossupply.service;

import uz.uzkassa.smartpossupply.constants.MessageConstants;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.ActivityType;
import uz.uzkassa.smartpossupply.domain.address.Address;
import uz.uzkassa.smartpossupply.domain.address.District;
import uz.uzkassa.smartpossupply.domain.address.Region;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.company.CompanyBank;
import uz.uzkassa.smartpossupply.domain.company.Partners;
import uz.uzkassa.smartpossupply.domain.settings.CompanySettings;
import uz.uzkassa.smartpossupply.domain.telegram.TelegramIntegration;
import uz.uzkassa.smartpossupply.dto.activitytype.ActivityTypeDTO;
import uz.uzkassa.smartpossupply.dto.address.AddressDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.company.CheckTinResultDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.dto.company.TopClientDTO;
import uz.uzkassa.smartpossupply.dto.companybank.CompanyBankDTO;
import uz.uzkassa.smartpossupply.dto.contract.ContractDTO;
import uz.uzkassa.smartpossupply.dto.customer.CustomerDTO;
import uz.uzkassa.smartpossupply.dto.customer.CustomerDetailDTO;
import uz.uzkassa.smartpossupply.dto.customer.CustomerLimitDTO;
import uz.uzkassa.smartpossupply.dto.customer.CustomerListDTO;
import uz.uzkassa.smartpossupply.dto.customer.CustomerStatisticDTO;
import uz.uzkassa.smartpossupply.dto.customer.CustomerTelegramDTO;
import uz.uzkassa.smartpossupply.dto.customer.CustomerWithLimitDTO;
import uz.uzkassa.smartpossupply.dto.customer.StatisticsByCustomerDTO;
import uz.uzkassa.smartpossupply.dto.customer.mobile.CustomerDetailMobileDTO;
import uz.uzkassa.smartpossupply.dto.customer.mobile.CustomerListMobileDTO;
import uz.uzkassa.smartpossupply.dto.customerbranch.CustomerBranchDTO;
import uz.uzkassa.smartpossupply.dto.integration.BillingCustomerDTO;
import uz.uzkassa.smartpossupply.dto.integration.agreement.CreateAgreementDTO;
import uz.uzkassa.smartpossupply.dto.integration.agreement.CreateAgreementRequestDTO;
import uz.uzkassa.smartpossupply.dto.payload.SyncCustomerBranchPayload;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.CompanyStatus;
import uz.uzkassa.smartpossupply.enums.GenericSettingsFieldType;
import uz.uzkassa.smartpossupply.enums.GenericSettingsType;
import uz.uzkassa.smartpossupply.enums.Role;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.filters.CompanyFilter;
import uz.uzkassa.smartpossupply.integration.billing.BillingServiceFeignClient;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqCustomerBranchProducer;
import uz.uzkassa.smartpossupply.repository.ActivityTypeRepository;
import uz.uzkassa.smartpossupply.repository.CompanyBankRepository;
import uz.uzkassa.smartpossupply.repository.CustomerBranchRepository;
import uz.uzkassa.smartpossupply.repository.DistrictRepository;
import uz.uzkassa.smartpossupply.repository.PartnerRepository;
import uz.uzkassa.smartpossupply.repository.RegionRepository;
import uz.uzkassa.smartpossupply.repository.TelegramIntegrationRepository;
import uz.uzkassa.smartpossupply.repository.WarehouseRepository;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.utils.SequenceUtils;
import uz.uzkassa.smartpossupply.validator.ValidationContext;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class CustomerService extends BaseService {

    CompanyBankService companyBankService;
    CompanyBankRepository companyBankRepository;
    PartnerRepository partnerRepository;
    RabbitMqCustomerBranchProducer rabbitMqCustomerBranchProducer;
    ActivityTypeRepository activityTypeRepository;
    CompanyService companyService;
    BillingServiceFeignClient billingServiceFeignClient;
    PlanRouteService planRouteService;
    CustomerBranchRepository customerBranchRepository;
    CustomerBranchService customerBranchService;
    ContractService contractService;
    SequenceUtils sequenceUtils;
    ProductFinancialDiscountService productFinancialDiscountService;
    TelegramIntegrationRepository telegramIntegrationRepository;
    WarehouseRepository warehouseRepository;
    CacheService cacheService;
    RegionRepository regionRepository;
    DistrictRepository districtRepository;
    ValidationContext validationContext;

    @Transactional
    public Company create(CustomerDTO customerDTO) {
        validationContext.validate(customerDTO);
        Long companyId = Optional.ofNullable(customerDTO.getCompanyId()).orElseGet(BaseService::getCurrentCompanyId);
        if (companyId == null) {
            throw validationService.badRequestExceptionThrow("Company is required").get();
        }
        if (customerDTO.getBank() != null) {
            if (customerDTO.getBank().getBankId() == 0) {
                throw validationService.badRequestExceptionThrow("Bank is required", "bankId can not be zero").get();
            }
        }
        Company customer;
        if (StringUtils.isNotEmpty(customerDTO.getTin())) {
            customer = companyRepository.findFirstByTinAndDeletedIsFalse(customerDTO.getTin()).orElseGet(Company::new);
        } else if (StringUtils.isNotEmpty(customerDTO.getName())) {
            customer = companyRepository.findFirstByNameIgnoreCaseAndDeletedIsFalse(customerDTO.getName()).orElseGet(Company::new);
        } else {
            customer = new Company();
        }
        customer.setName(customerDTO.getName());
        customer.setBrand(customerDTO.getBrand());
        if (StringUtils.isEmpty(customer.getTin()) && StringUtils.isNotEmpty(customerDTO.getTin())) {
            customer.setTin(customerDTO.getTin());
        }
        customer.setContactPerson(customerDTO.getContactPerson());
        customer.setPhone(customerDTO.getPhone());
        customer.setMobileId(customerDTO.getMobileId());
        if (customerDTO.getAddress() != null) {
            if (customer.isNew()) {
                customer.setAddress(addressMapper.toEntity(customerDTO.getAddress()));
            } else {
                customer.setAddress(
                    addressMapper.merge(customerDTO.getAddress(), Optional.ofNullable(customer.getAddress()).orElse(new Address()))
                );
            }
        }
        customer.setDirector(customerDTO.getDirector());
        customer.setAccountant(customerDTO.getAccountant());
        companyRepository.saveAndFlush(customer);

        if (customerDTO.getBank() != null) {
            CompanyBank companyBank = Optional.ofNullable(customer.getCompanyBank()).orElse(new CompanyBank());
            companyBank.setBankId(customerDTO.getBank().getBankId());
            companyBank.setCompanyId(customer.getId());
            companyBank.setAccountNumber(customerDTO.getBank().getAccountNumber());
            companyBank.setOked(customerDTO.getBank().getOked());
            companyBank.setMain(Optional.ofNullable(customerDTO.getBank().getMain()).orElse(true));
            companyBankRepository.save(companyBank);
            customer.setCompanyBankId(companyBank.getId());
        }
        if (customerDTO.getActivityTypeId() != null) {
            customer.setActivityTypeId(customerDTO.getActivityTypeId());
        }
        companyRepository.saveAndFlush(customer);
        cacheService.evictCompany(customer.getTin());

        Partners partners = partnerRepository.findByCompanyIdAndCustomerIdAndSupplierId(companyId, customer.getId(), companyId).orElseGet(Partners::new);
        if (partners.getId() == null) {
            partners = new Partners();
            partners.setCompanyId(companyId);
            partners.setCustomerId(customer.getId());
            partners.setSupplierId(companyId);
            partners.setStatus(CompanyStatus.ACTIVE);
        }
        partners.setExternalId(customerDTO.getExternalId());
        partnerRepository.save(partners);

        CustomerBranchDTO customerBranchDTO = new CustomerBranchDTO();
        customerBranchDTO.setCustomerId(customer.getId());
        customerBranchDTO.setName(customer.getName());
        customerBranchDTO.setPhone(customer.getPhone());
        customerBranchDTO.setMain(true);
        customerBranchDTO.setAddress(addressMapper.toDTO(customer.getAddress()));
        try {
            customerBranchService.createOrGet(customerBranchDTO);
        } catch (Exception e) {
            log.error("Customer branch create error: {}", e.getMessage());
        }
        if (customerDTO.isSyncBranches() && StringUtils.isNotEmpty(customer.getTin())) {
            try {
                rabbitMqCustomerBranchProducer.send(new SyncCustomerBranchPayload(customer.getTin(), customer.getId()));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        if (SecurityUtils.isFromMobile()) {
            ContractDTO contractDTO = new ContractDTO();
            contractDTO.setCustomerId(customer.getId());
            contractDTO.setContractDate(LocalDate.now().plusYears(1));
            contractDTO.setContractNumber("За_нал " + sequenceUtils.generateNextNumber("contract_number_seq"));
            contractService.create(contractDTO);
        }
        return customer;

    }

    @Transactional
    public Company createOrGet(CustomerDTO customerDTO) {
        Company customer = null;
        if (StringUtils.isNotEmpty(customerDTO.getTin())) {
            customer = companyRepository.findFirstByTinAndDeletedIsFalse(customerDTO.getTin()).orElse(null);
        }
        if (customer == null && StringUtils.isNotEmpty(customerDTO.getName())) {
            customer = companyRepository.findFirstByNameIgnoreCaseAndDeletedIsFalse(customerDTO.getName()).orElse(null);
        }
        if (customer == null) {
            customer = this.create(customerDTO);
        }
        Partners partners = partnerRepository.findByCompanyIdAndCustomerIdAndSupplierId(customerDTO.getCompanyId(), customer.getId(), customerDTO.getCompanyId()).orElseGet(Partners::new);
        if (partners.getId() == null) {
            partners = new Partners();
            partners.setCompanyId(customerDTO.getCompanyId());
            partners.setCustomerId(customer.getId());
            partners.setSupplierId(customerDTO.getCompanyId());
            partners.setStatus(CompanyStatus.ACTIVE);
        }
        partners.setExternalId(customerDTO.getExternalId());
        partnerRepository.save(partners);
        return customer;
    }

    @Transactional
    public Long update(Long id, CustomerDTO customerDTO) {
        validationContext.validate(customerDTO);
        return companyRepository.findById(id).map(customer -> {
            customer.setName(customerDTO.getName());
            customer.setBrand(customerDTO.getBrand());
            customer.setContactPerson(customerDTO.getContactPerson());
            customer.setPhone(customerDTO.getPhone());
            customer.setDirector(customerDTO.getDirector());
            customer.setAccountant(customerDTO.getAccountant());
            if (customerDTO.getActivityTypeId() != null) {
                ActivityType activityType = activityTypeRepository.findById(customerDTO.getActivityTypeId()).orElseThrow(validationService.entityNotFoundExceptionThrow(ActivityType.class.getSimpleName(), "id", customerDTO.getActivityTypeId()));
                customer.setActivityType(activityType);
                customer.setActivityTypeId(activityType.getId());
            } else {
                customer.setActivityType(null);
                customer.setActivityTypeId(null);
            }
            if (customerDTO.getAddress() != null) {
                Address address = addressMapper.toEntity(customerDTO.getAddress());
                if (customerDTO.getAddress().getRegionId() != null) {
                    Region region = regionRepository.findById(customerDTO.getAddress().getRegionId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Region.class.getSimpleName(), "id", customerDTO.getAddress().getRegionId()));
                    address.setRegion(region);
                }
                if (customerDTO.getAddress().getDistrictId() != null) {
                    District district = districtRepository.findById(customerDTO.getAddress().getDistrictId()).orElseThrow(validationService.entityNotFoundExceptionThrow(District.class.getSimpleName(), "id", customerDTO.getAddress().getDistrictId()));
                    address.setDistrict(district);
                }
                customer.setAddress(addressMapper.merge(customerDTO.getAddress(), address));
            }
            if (customerDTO.getBank() != null && customerDTO.getBank().getBankId() != null) {
                CompanyBank companyBank = Optional.ofNullable(customer.getCompanyBank()).orElse(new CompanyBank());
                companyBank.setBankId(customerDTO.getBank().getBankId());
                companyBank.setCompanyId(customer.getId());
                companyBank.setAccountNumber(customerDTO.getBank().getAccountNumber());
                companyBank.setOked(customerDTO.getBank().getOked());
                companyBank.setMain(Optional.of(customerDTO.getBank().getMain()).orElse(true));
                companyBankRepository.save(companyBank);
                customer.setCompanyBankId(companyBank.getId());
            }
            companyRepository.save(customer);
            partnerRepository.findByCompanyIdAndCustomerIdAndSupplierId(getCurrentCompanyId(), customer.getId(), getCurrentCompanyId()).ifPresent(partners -> {
                partners.setExternalId(customerDTO.getExternalId());
                partnerRepository.save(partners);
            });
            cacheService.evictCompany(customer.getTin());
            return customer.getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", id));
    }

    public CustomerDetailDTO get(Long id) {
        return companyRepository.findById(id).map(customer -> {
            CustomerDetailDTO customerDetailDTO = new CustomerDetailDTO();
            customerDetailDTO.setId(customer.getId());
            customerDetailDTO.setName(customer.getName());
            customerDetailDTO.setBrand(customer.getBrand());
            customerDetailDTO.setTin(customer.getTin());
            customerDetailDTO.setAddress(addressMapper.toDetailDTO(customer.getAddress()));
            customerDetailDTO.setContactPerson(customer.getContactPerson());
            customerDetailDTO.setDirector(customer.getDirector());
            customerDetailDTO.setAccountant(customer.getAccountant());
            customerDetailDTO.setPhone(customer.getPhone());
            customerDetailDTO.setBankDetail(companyBankService.get(customer));
            customerDetailDTO.setHasFinancialDiscount(customer.isHasFinancialDiscount());
            if (customer.isHasFinancialDiscount()) {
                customerDetailDTO.setFinancialDiscountProducts(productFinancialDiscountService.getFinancialDiscountProduct(customer.getProductGroup()));
            }
            if (customer.getActivityTypeId() != null) {
                customerDetailDTO.setActivityType(
                    new ActivityTypeDTO(customer.getActivityType().getId(),
                        customer.getActivityType().getName(),
                        customer.getActivityType().getParent()));
            }
            customerDetailDTO.setCompanyType(customer.getCompanyType());
            partnerRepository.findByCompanyIdAndCustomerIdAndSupplierId(getCurrentCompanyId(), customer.getId(), getCurrentCompanyId()).ifPresent(partners -> {
                customerDetailDTO.setLimitBalance(partners.getLimitBalance());
                customerDetailDTO.setCurrentBalance(partners.getCurrentBalance());
                customerDetailDTO.setHasLimit(partners.isHasLimit());
                customerDetailDTO.setExternalId(partners.getExternalId());
            });
            return customerDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", id));
    }

    public ResultList<Company> getMineResultList(CompanyFilter filter) {
        filter.setCompanyId(Optional.ofNullable(filter.getCompanyId()).orElse(getCurrentCompanyId()));
        filter.setCreatorId(getCurrentUserId());
        ResultList<Partners> resultList = partnerRepository.getResultList(filter, false);
        ResultList<Company> partners = new ResultList<>();
        partners.setList(resultList.getList().stream().map(Partners::getCustomer).toList());
        partners.setCount(resultList.getCount());
        return partners;
    }

    public ResultList<Company> getResultList(CompanyFilter filter) {
        filter.setCompanyId(Optional.ofNullable(filter.getCompanyId()).orElse(getCurrentCompanyId()));
        boolean planRouteCustomerListEnabled = false;
        ResultList<Company> resultList = new ResultList<>();
        CompanySettings companySettings = companySettingsRepository.findFirstByCompanyIdAndSettings_SettingsType(filter.getCompanyId(), GenericSettingsType.WORK_WITH_AGENT)
            .orElse(null);
        if (companySettings != null) {
            planRouteCustomerListEnabled = Optional.ofNullable(companySettings.getValues().get(GenericSettingsFieldType.CUSTOMERS)).orElse(GenericSettingsFieldType.PLAN_ROUTE_CUSTOMER_LIST.getCode()).equals(GenericSettingsFieldType.PLAN_ROUTE_CUSTOMER_LIST.getCode());
        }
        if (SecurityUtils.isAgent()) {
            filter.setAgentId(SecurityUtils.getCurrentUserId());
            if (planRouteCustomerListEnabled) {
                resultList = planRouteService.getCustomerForAgent(filter);
            } else {
                ResultList<Partners> partnersResultList = partnerRepository.getResultList(filter, false);
                resultList.setList(partnersResultList.getList().stream().map(Partners::getCustomer).toList());
                resultList.setCount(partnersResultList.getCount());
            }
        } else if (isExpeditor() && planRouteCustomerListEnabled) {
            resultList = planRouteService.getCustomerForAgent(filter);
        } else {
            ResultList<Partners> partnersResultList = partnerRepository.getResultList(filter, false);
            resultList.setList(partnersResultList.getList().stream().map(Partners::getCustomer).toList());
            resultList.setCount(partnersResultList.getCount());
        }

        return resultList;
    }

    public Page<CustomerDetailMobileDTO> getListForOffline(CompanyFilter filter) {
        ResultList<Company> resultList = this.getResultList(filter);
        Set<Long> companyIds = this.getMineResultList(filter).getList().stream().map(Company::getId).collect(Collectors.toSet());
        List<CustomerDetailMobileDTO> result = resultList
                .getList()
                .stream()
                .map(company -> {
                    CustomerDetailMobileDTO customerDetailMobileDTO = new CustomerDetailMobileDTO();
                    customerDetailMobileDTO.setId(company.getId());
                    customerDetailMobileDTO.setName(company.getName());
                    customerDetailMobileDTO.setBrand(company.getBrand());
                    customerDetailMobileDTO.setTin(company.getTin());
                    customerDetailMobileDTO.setCreatedDate(company.getCreatedDate());
                    customerDetailMobileDTO.setAddress(addressMapper.toDetailDTO(company.getAddress()));
                    customerDetailMobileDTO.setContactPerson(company.getContactPerson());
                    customerDetailMobileDTO.setDirector(company.getDirector());
                    customerDetailMobileDTO.setAccountant(company.getAccountant());
                    customerDetailMobileDTO.setPhone(company.getPhone());
                    customerDetailMobileDTO.setBankDetail(companyBankService.get(company));
                    customerDetailMobileDTO.setHasFinancialDiscount(company.isHasFinancialDiscount());
                    if (company.isHasFinancialDiscount()) {
                        customerDetailMobileDTO.setFinancialDiscountProducts(productFinancialDiscountService.getFinancialDiscountProduct(company.getProductGroup()));
                    }
                    if (company.getActivityTypeId() != null) {
                        customerDetailMobileDTO.setActivityType(
                                new ActivityTypeDTO(company.getActivityType().getId(),
                                        company.getActivityType().getName(),
                                        company.getActivityType().getParent()));
                    }
                    customerDetailMobileDTO.setCompanyType(company.getCompanyType());
                    partnerRepository.findByCompanyIdAndCustomerIdAndSupplierId(getCurrentCompanyId(), company.getId(), getCurrentCompanyId()).ifPresent(partners -> {
                        customerDetailMobileDTO.setLimitBalance(partners.getLimitBalance());
                        customerDetailMobileDTO.setCurrentBalance(partners.getCurrentBalance());
                        customerDetailMobileDTO.setHasLimit(partners.isHasLimit());
                        customerDetailMobileDTO.setMine(companyIds.contains(company.getId()));
                    });
                    return customerDetailMobileDTO;
                }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public Page<CustomerListDTO> getList(CompanyFilter filter) {
        ResultList<Company> resultList = this.getResultList(filter);
        List<CustomerListDTO> result = resultList
            .getList()
            .stream()
            .map(company -> {
                CustomerListDTO customerListDTO = new CustomerListDTO();
                customerListDTO.setId(company.getId());
                customerListDTO.setName(company.getName());
                customerListDTO.setBrand(company.getBrand());
                customerListDTO.setTin(company.getTin());
                customerListDTO.setContactPerson(company.getContactPerson());
                customerListDTO.setPhone(company.getPhone());
                customerListDTO.setAddress(addressMapper.toDetailDTO(company.getAddress()));
                customerListDTO.setCustomerBranchCount(company.getCustomerBranchCount());
                customerListDTO.setEquipmentCount(company.getEquipmentCount());
                if (company.getActivityTypeId() != null) {
                    customerListDTO.setActivityType(company.getActivityType().toCommonDTO());
                }
                partnerRepository.findByCompanyIdAndCustomerIdAndSupplierId(filter.getCompanyId(), company.getId(), filter.getCompanyId())
                    .ifPresent(partners -> {
                        customerListDTO.setLimitBalance(partners.getLimitBalance());
                        customerListDTO.setStatus(partners.getStatus());
                        customerListDTO.setCurrentBalance(partners.getCurrentBalance());
                        customerListDTO.setHasLimit(partners.isHasLimit());
                        customerListDTO.setCreatedDate(partners.getCreatedDate());
                        if (partners.getCreatorId() != null) {
                            customerListDTO.setCreator(partners.getCreator().toUserCommonDTO());
                        }
                    });
                return customerListDTO;
            }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public Page<CustomerListMobileDTO> getListForMobile(CompanyFilter filter) {
        ResultList<Company> resultList;
        if (filter.getMine() != null && filter.getMine()) {
            resultList = this.getMineResultList(filter);
        } else {
            resultList = this.getResultList(filter);
        }
        List<CustomerListMobileDTO> result = resultList.getList().stream().map(partners -> {
            CustomerListMobileDTO customerListDTO = new CustomerListMobileDTO();
            customerListDTO.setId(partners.getId());
            customerListDTO.setName(partners.getName());
            customerListDTO.setBrand(partners.getBrand());
            customerListDTO.setTin(partners.getTin());
            customerListDTO.setPhone(partners.getPhone());
            customerListDTO.setContactPerson(partners.getContactPerson());
            customerListDTO.setCustomerBranchCount(customerBranchRepository.countByCustomerIdAndDeletedIsFalseAndStatus(partners.getId(), CommonStatus.ACTIVE));
            return customerListDTO;
        }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public CustomerStatisticDTO getStatistics(BaseFilter filter) {
        return companyRepository.getCustomerStatistics(filter);
    }

    public StatisticsByCustomerDTO getStatisticsByCustomer(Long id) {
        return companyRepository.getStatisticsByCustomer(id);
    }

    public List<CustomerWithLimitDTO> lookUp(CompanyFilter filter) {
        filter.setStatus(CompanyStatus.ACTIVE.getCode());
        Page<CustomerListDTO> result = this.getList(filter);
        return result
            .stream()
            .map(customer -> {
                CustomerWithLimitDTO customerWithLimitDTO = new CustomerWithLimitDTO();
                customerWithLimitDTO.setId(customer.getId());
                customerWithLimitDTO.setName(customer.getName());
                customerWithLimitDTO.setBrand(customer.getBrand());
                customerWithLimitDTO.setTin(customer.getTin());
                customerWithLimitDTO.setLimitBalance(customer.getLimitBalance());
                customerWithLimitDTO.setCurrentBalance(customer.getCurrentBalance());
                customerWithLimitDTO.setHasLimit(customer.isHasLimit());
                return customerWithLimitDTO;
            })
            .toList();
    }

    public List<CommonDTO> lookUpForTelegram(CompanyFilter filter) {
        filter.setStatus(CompanyStatus.ACTIVE.getCode());
        filter.setCompanyId(userRepository.getCompanyIdByUserId(filter.getUserId()));
        return this.getResultList(filter).getList().stream().map(Company::toCommonDTO).toList();
    }


    public List<CompanyBaseDTO> lookUpForMobile(CompanyFilter filter) {
        filter.setStatus(CompanyStatus.ACTIVE.getCode());
        ResultList<Company> resultList = this.getResultList(filter);
        return resultList.getList()
                .stream()
                .map(customer -> {
                    CompanyBaseDTO companyBaseDTO = new CompanyBaseDTO();
                    companyBaseDTO.setId(customer.getId());
                    companyBaseDTO.setName(customer.getName());
                    companyBaseDTO.setTin(customer.getTin());
                    companyBaseDTO.setBrand(customer.getBrand());
                    return companyBaseDTO;
                })
                .toList();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        partnerRepository.deleteBySupplierIdAndCustomerId(getCurrentCompanyId(), id);
    }

    public List<TopClientDTO> getTopClients(BaseFilter filter) {
        return companyRepository.getTopClients(filter);
    }

    @Transactional
    public void financialDiscount(Long id, boolean hasFinancialDiscount) {
        companyRepository.findById(id).map(customer -> {
            customer.setHasFinancialDiscount(hasFinancialDiscount);
            companyRepository.save(customer);
            cacheService.evictCompany(customer.getTin());
            return customer;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", id));
    }

    @Transactional
    public void updateStatus(Long id, CompanyStatus status) {
        partnerRepository
            .findByCompanyIdAndCustomerIdAndSupplierId(SecurityUtils.getCurrentCompanyId(), id, SecurityUtils.getCurrentCompanyId())
            .map(partner -> {
                partner.setStatus(status);
                partnerRepository.save(partner);
                return partner;
            })
            .orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", id));
    }

    @Transactional
    public Company create(String customerTin) {
        CheckTinResultDTO checkTinResultDTO = companyService.checkTin(customerTin);

        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setTin(customerTin);
        customerDTO.setCompanyId(SecurityUtils.getCurrentCompanyId());

        customerDTO.setName(checkTinResultDTO.getName());
        if (checkTinResultDTO.getAddress() != null) {
            AddressDTO addressDTO = new AddressDTO();
            if (checkTinResultDTO.getAddress().getRegion() != null) {
                addressDTO.setRegionId(checkTinResultDTO.getAddress().getRegion().getId());
            }
            if (checkTinResultDTO.getAddress().getDistrict() != null) {
                addressDTO.setDistrictId(checkTinResultDTO.getAddress().getDistrict().getId());
            }
            addressDTO.setStreet(checkTinResultDTO.getAddress().getStreet());
            addressDTO.setHouse(checkTinResultDTO.getAddress().getHouse());
            addressDTO.setLongitude(checkTinResultDTO.getAddress().getLongitude());
            addressDTO.setLatitude(checkTinResultDTO.getAddress().getLatitude());
            customerDTO.setAddress(addressDTO);
        }
        if (checkTinResultDTO.getBankDetail() != null) {
            CompanyBankDTO companyBankDTO = new CompanyBankDTO();
            companyBankDTO.setId(checkTinResultDTO.getBankDetail().getId());
            if (checkTinResultDTO.getBankDetail().getBank() != null) {
                companyBankDTO.setBankId(checkTinResultDTO.getBankDetail().getBank().getId());
            }
            companyBankDTO.setOked(checkTinResultDTO.getBankDetail().getOked());
            companyBankDTO.setMain(checkTinResultDTO.getBankDetail().getMain());
            companyBankDTO.setAccountNumber(checkTinResultDTO.getBankDetail().getAccountNumber());
            customerDTO.setBank(companyBankDTO);
        }
        return this.create(customerDTO);
    }

    @Transactional
    public void createOrUpdateLimit(CustomerLimitDTO customerLimitDTO) {
        partnerRepository.findByCompanyIdAndCustomerIdAndSupplierId(getCurrentCompanyId(), customerLimitDTO.getCustomerId(), getCurrentCompanyId()).map(partners -> {
            partners.setLimitBalance(customerLimitDTO.getAmount());
            partners.setHasLimit(customerLimitDTO.isHasLimit());
            partnerRepository.save(partners);
            return partners;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", customerLimitDTO.getCustomerId()));
    }

    @Transactional
    public Object saveBillingAgreement(String token, CreateAgreementDTO dto) {
        Company company = null;
        if (getCurrentUser() != null && Role.getAdminRoles().contains(getCurrentUser().getRole())) {
            if (dto.getCustomer() != null && StringUtils.isNotEmpty(dto.getCustomer().getInn())) {
                company = companyRepository.findFirstByTinAndDeletedIsFalse(dto.getCustomer().getInn()).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "tin", dto.getCustomer().getInn()));
            }
        } else {
            company = getCurrentCompany();
        }

        BillingCustomerDTO billingCustomerDTO = dto.getCustomer();
        if (company != null) {
            if (billingCustomerDTO != null) {
                billingCustomerDTO.setInn(company.getTin());
            }
            if (company.getManager() != null) {
                dto.setSmartposCreatorId(company.getManagerId().toString());
                dto.setFirstName(company.getManager().getFirstName());
                dto.setLastName(company.getManager().getLastName());
                dto.setPatronymic(company.getManager().getPatronymic());
                dto.setLogin(company.getManager().getLogin());
            } else {
                throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.MANAGER_NOT_ASSIGNED)).get();
            }
        } else {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.COMPANY_NOT_REGISTERED)).get();
        }
        CreateAgreementRequestDTO requestDTO = new CreateAgreementRequestDTO();
        requestDTO.setInn(dto.getCustomer().getInn());
        requestDTO.setXizmatId(dto.getXizmat().getId());
        requestDTO.setInstances(dto.getInstances());
        requestDTO.setDescription(dto.getDescription());
        requestDTO.setFirstName(dto.getFirstName());
        requestDTO.setLastName(dto.getLastName());
        requestDTO.setPatronymic(dto.getPatronymic());
        requestDTO.setLogin(dto.getLogin());
        requestDTO.setSmartposCreatorId(dto.getSmartposCreatorId());
        requestDTO.setNoteFromExternalSystem(dto.getNoteFromExternalSystem());
        requestDTO.setTinForInstanceCheck(dto.getTinForInstanceCheck());
        requestDTO.setIdForInstanceCheck(dto.getIdForInstanceCheck());
        requestDTO.setNameForInstanceCheck(dto.getNameForInstanceCheck());
        requestDTO.setParentAgreementId(dto.getParentAgreementId());
        return billingServiceFeignClient.saveAgreement(token, requestDTO);
    }

    @Transactional
    public Object addAgreementExtraTerminals(String token, CreateAgreementDTO dto) {
        Company company = null;
        if (getCurrentUser() != null && Role.getAdminRoles().contains(getCurrentUser().getRole())) {
            if (dto.getCustomer() != null && StringUtils.isNotEmpty(dto.getCustomer().getInn())) {
                company = companyRepository.findFirstByTinAndDeletedIsFalse(dto.getCustomer().getInn()).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "tin", dto.getCustomer().getInn()));
            }
        } else {
            company = getCurrentCompany();
        }

        BillingCustomerDTO billingCustomerDTO = dto.getCustomer();
        if (company != null) {
            if (billingCustomerDTO != null) {
                billingCustomerDTO.setInn(company.getTin());
            }
            if (company.getManager() != null) {
                dto.setSmartposCreatorId(company.getManagerId().toString());
                dto.setFirstName(company.getManager().getFirstName());
                dto.setLastName(company.getManager().getLastName());
                dto.setPatronymic(company.getManager().getPatronymic());
                dto.setLogin(company.getManager().getLogin());
            } else {
                throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.MANAGER_NOT_ASSIGNED)).get();
            }
        } else {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.COMPANY_NOT_REGISTERED)).get();
        }
        CreateAgreementRequestDTO requestDTO = new CreateAgreementRequestDTO();
        requestDTO.setInn(dto.getCustomer().getInn());
        requestDTO.setXizmatId(dto.getXizmat().getId());
        requestDTO.setInstances(dto.getInstances());
        requestDTO.setDescription(dto.getDescription());
        requestDTO.setFirstName(dto.getFirstName());
        requestDTO.setLastName(dto.getLastName());
        requestDTO.setPatronymic(dto.getPatronymic());
        requestDTO.setLogin(dto.getLogin());
        requestDTO.setSmartposCreatorId(dto.getSmartposCreatorId());
        requestDTO.setNoteFromExternalSystem(dto.getNoteFromExternalSystem());
        requestDTO.setTinForInstanceCheck(dto.getTinForInstanceCheck());
        requestDTO.setIdForInstanceCheck(dto.getIdForInstanceCheck());
        requestDTO.setNameForInstanceCheck(dto.getNameForInstanceCheck());
        requestDTO.setParentAgreementId(dto.getParentAgreementId());
        requestDTO.setSerialNumbers(dto.getSerialNumbers());
        return billingServiceFeignClient.addExtraTerminalsAgreement(token, requestDTO);
    }

    @Transactional
    public Long create(CustomerTelegramDTO customerTelegramDTO) {
        TelegramIntegration telegramIntegration = telegramIntegrationRepository.findFirstByBotToken(customerTelegramDTO.getToken()).orElseThrow(validationService.entityNotFoundExceptionThrow(TelegramIntegration.class.getSimpleName(), "botToken", customerTelegramDTO.getToken()));
        Company supplier = telegramIntegration.getCompany();

        companyRepository.findCustomerBySupplierIdAndPhone(supplier.getId(), customerTelegramDTO.getPhone()).ifPresent(company -> {
            throw validationService.badRequestExceptionThrow("Клиент уже существует", "Клиент '" + company.getName() + "' уже зарегистрирован с номером " + company.getPhone()).get();
        });

        Company customer = new Company();
        customer.setTelegramUserId(customerTelegramDTO.getUserId());
        customer.setTelegramLanguageCode(customerTelegramDTO.getLanguageCode());
        customer.setName(customerTelegramDTO.getName());
        customer.setPhone(customerTelegramDTO.getPhone());
        companyRepository.save(customer);

        if (!partnerRepository.existsBySupplierIdAndCustomerIdAndCompanyId(supplier.getId(), customer.getId(), supplier.getId())) {
            Partners partners = partnerRepository.findByCompanyIdAndCustomerIdAndSupplierId(supplier.getId(), customer.getId(), supplier.getId()).orElseGet(Partners::new);
            partners.setCustomerId(customer.getId());
            partners.setSupplierId(supplier.getId());
            partners.setCompanyId(supplier.getId());
            partnerRepository.save(partners);
        }

        final Long branchId = branchRepository.getMainBranchId(supplier.getId());
        final Long warehouseId = warehouseRepository.getMainWarehouseId(branchId);
        if (branchId != null && warehouseId != null) {
            CustomerBranchDTO customerBranchDTO = new CustomerBranchDTO();
            customerBranchDTO.setBranchId(branchId);
            customerBranchDTO.setWarehouseId(warehouseId);
            customerBranchDTO.setCustomerId(customer.getId());
            customerBranchDTO.setName(customer.getName());
            customerBranchDTO.setPhone(customer.getPhone());
            customerBranchDTO.setMain(true);
            customerBranchService.create(customerBranchDTO);
        }
        return customer.getId();
    }
}
