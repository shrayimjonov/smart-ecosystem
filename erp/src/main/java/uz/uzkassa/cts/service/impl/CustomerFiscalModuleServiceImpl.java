package uz.uzkassa.cts.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.customer.CustomerFiscalModuleTemplateDTO;
import uz.uzkassa.cts.dto.device.CustomerBranchDeviceListDTO;
import uz.uzkassa.cts.dto.device.SerialNumberDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.dto.fm.CustomerFiscalModuleDTO;
import uz.uzkassa.cts.dto.fm.CustomerFiscalModuleRegisterDTO;
import uz.uzkassa.cts.dto.fm.admin.CustomerFiscalModuleListAdminDTO;
import uz.uzkassa.cts.dto.fm.admin.FiscalModuleDetailAdminDTO;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class CustomerFiscalModuleServiceImpl extends BaseServiceImpl<CustomerFiscalModule, Long> implements CustomerFiscalModuleService {

    private final FiscalizationRepository fiscalizationRepository;
    private final FiscalModuleRepository fiscalModuleRepository;
    private final CustomerBranchFiscalModuleRepository customerBranchFiscalModuleRepository;
    private final CustomerFiscalModuleRepository customerFiscalModuleRepository;
    private final WarehouseHistoryService warehouseHistoryService;
    private final FileUploadRepository fileUploadRepository;
    private final CustomerRepository customerRepository;
    private final CustomerBranchRepository customerBranchRepository;
    private final UserRepository userRepository;
    private final CompanyFiscalModuleRepository companyFiscalModuleRepository;
    private final BranchFiscalModuleRepository branchFiscalModuleRepository;
    private final FiscalModuleService fiscalModuleService;
    private final BranchRepository branchRepository;
    private final OfdApplicationRepository ofdApplicationRepository;
    private final ThymeleafService thymeleafService;
    private final CustomerEmployeeRepository customerEmployeeRepository;
    private final CacheService cacheService;
    private final LogHistoryService logHistoryService;


    public CustomerFiscalModuleServiceImpl(CustomerFiscalModuleRepository customerFiscalModuleRepository,
                                           FiscalizationRepository fiscalizationRepository,
                                           CustomerBranchFiscalModuleRepository customerBranchFiscalModuleRepository,
                                           FiscalModuleRepository fiscalModuleRepository,
                                           WarehouseHistoryService warehouseHistoryService,
                                           FileUploadRepository fileUploadRepository,
                                           CustomerRepository customerRepository,
                                           CustomerBranchRepository customerBranchRepository,
                                           UserRepository userRepository,
                                           CompanyFiscalModuleRepository companyFiscalModuleRepository,
                                           BranchFiscalModuleRepository branchFiscalModuleRepository, FiscalModuleService fiscalModuleService,
                                           BranchRepository branchRepository, OfdApplicationRepository ofdApplicationRepository,
                                           ThymeleafService thymeleafService, CustomerEmployeeRepository customerEmployeeRepository, CacheService cacheService, LogHistoryService logHistoryService) {
        super(customerFiscalModuleRepository);
        this.customerFiscalModuleRepository = customerFiscalModuleRepository;
        this.fiscalizationRepository = fiscalizationRepository;
        this.customerBranchFiscalModuleRepository = customerBranchFiscalModuleRepository;
        this.fiscalModuleRepository = fiscalModuleRepository;
        this.warehouseHistoryService = warehouseHistoryService;
        this.fileUploadRepository = fileUploadRepository;
        this.customerRepository = customerRepository;
        this.customerBranchRepository = customerBranchRepository;
        this.userRepository = userRepository;
        this.companyFiscalModuleRepository = companyFiscalModuleRepository;
        this.branchFiscalModuleRepository = branchFiscalModuleRepository;
        this.fiscalModuleService = fiscalModuleService;
        this.branchRepository = branchRepository;
        this.ofdApplicationRepository = ofdApplicationRepository;
        this.thymeleafService = thymeleafService;
        this.customerEmployeeRepository = customerEmployeeRepository;
        this.cacheService = cacheService;
        this.logHistoryService = logHistoryService;
    }

    @Override
    public Page<CustomerFiscalModuleListAdminDTO> getListForAdmin(BaseFilter filter) {
        ResultList<CustomerFiscalModule> resultList = customerFiscalModuleRepository.getResultList(filter);
        List<CustomerFiscalModuleListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(customerFiscalModule -> {
                    CustomerFiscalModuleListAdminDTO customerFiscalModuleListDTO = new CustomerFiscalModuleListAdminDTO();
                    customerFiscalModuleListDTO.setId(customerFiscalModule.getId());
                    customerFiscalModuleListDTO.setFiscalModule(customerFiscalModule.getFiscalModule().toSerialNumberDTO());
                    customerFiscalModuleListDTO.setStatus(customerFiscalModule.getStatus());
                    customerFiscalModuleListDTO.setCreatedDate(customerFiscalModule.getCreatedDate());
                    customerFiscalModuleListDTO.setCreator(customerFiscalModule.getCreator().toCommonDTO());
                    customerFiscalModuleListDTO.setBranch(customerFiscalModule.getCustomer().getBranch().toCommonDTO());
                    customerFiscalModuleListDTO.setCompany(customerFiscalModule.getCustomer().getCompany().toShortDTO());
                    customerFiscalModuleListDTO.setCustomer(customerFiscalModule.getCustomer().toShortDTO());
                    return customerFiscalModuleListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public FiscalModuleDetailAdminDTO getForAdmin(Long id) {
        return customerFiscalModuleRepository.findById(id).map(customerFiscalModule -> fiscalModuleService.getForAdmin(customerFiscalModule.getFiscalModuleId())).orElseThrow(validationService.entityNotFoundExceptionThrow(FISCAL_MODULE));
    }

    @Override
    public List<DeviceStatisticsAdminDTO> getStatisticsForAdmin(BaseFilter filter) {
        return customerFiscalModuleRepository.getStatistics(filter);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerBranchDeviceListDTO> list(BaseFilter filter) {
        List<CustomerBranchDeviceListDTO> result = new ArrayList<>();
        ResultList<CustomerFiscalModule> resultList = customerFiscalModuleRepository.getList(filter);
        resultList.getList().forEach(customerFiscalModule -> {
            CustomerBranchDeviceListDTO customerBranchDeviceListDTO = new CustomerBranchDeviceListDTO();
            customerBranchDeviceListDTO.setId(customerFiscalModule.getId());
            customerBranchDeviceListDTO.setFiscalModuleSerial(customerFiscalModule.getFiscalModule().getSerialNumber());

            Fiscalization fiscalization = fiscalizationRepository.findFirstByCustomerIdAndFiscalModuleId(customerFiscalModule.getCustomerId(), customerFiscalModule.getFiscalModuleId());
            if (fiscalization != null) {
                customerBranchDeviceListDTO.setFiscalizationDate(fiscalization.getCreatedDate());
                customerBranchDeviceListDTO.setDeviceSerial(fiscalization.getDevice().getSerialNumber());
                customerBranchDeviceListDTO.setCustomerBranch(fiscalization.getCustomerBranch().getDTO());
                customerBranchDeviceListDTO.setFiscalizationId(fiscalization.getId());
                customerBranchDeviceListDTO.setPaid(fiscalization.isPaid());
                if (fiscalization.getOfdApplicationId() != null) {
                    OfdApplication ofdApplication = ofdApplicationRepository.findById(fiscalization.getOfdApplicationId()).orElse(null);
                    if (ofdApplication != null) {
                        customerBranchDeviceListDTO.setOfdId(ofdApplication.getOfdId());
                        customerBranchDeviceListDTO.setOfdStatus(ofdApplication.getOfdStatus());
                        customerBranchDeviceListDTO.setOfdFileUrl(ofdApplication.getApplicationFileUrl());
                        customerBranchDeviceListDTO.setOfdApplicationType(ofdApplication.getApplicationType());
                    }
                }
                FileUpload applicationFile = fileUploadRepository.findFirstByDocumentTypeAndEntityTypeAndEntityId(DocumentType.APPLICATION, EntityTypeEnum.FISCALIZATION, fiscalization.getId());
                FileUpload fiscalModuleFile = fileUploadRepository.findFirstByDocumentTypeAndEntityTypeAndEntityId(DocumentType.FISCAL_MODULE_DOC, EntityTypeEnum.CUSTOMER_FISCAL_MODULE, customerFiscalModule.getId());
                if (applicationFile != null) {
                    customerBranchDeviceListDTO.setApplicationFileUrl(applicationFile.getDownloadUrl());
                    customerBranchDeviceListDTO.setApplicationFileId(applicationFile.getId());
                }
                if (fiscalModuleFile != null) {
                    customerBranchDeviceListDTO.setFiscalModuleFileUrl(fiscalModuleFile.getDownloadUrl());
                    customerBranchDeviceListDTO.setFiscalModuleFileId(fiscalModuleFile.getId());
                }
            } else {
                CustomerBranchFiscalModule customerBranchFiscalModule = customerBranchFiscalModuleRepository.findFirstByCustomerBranch_CustomerIdAndFiscalModuleId(customerFiscalModule.getCustomerId(), customerFiscalModule.getFiscalModuleId());
                if (customerBranchFiscalModule != null) {
                    customerBranchDeviceListDTO.setCustomerBranch(customerBranchFiscalModule.getCustomerBranch().getDTO());
                }
            }

            result.add(customerBranchDeviceListDTO);
        });

        return new PageImpl<>
                (result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SerialNumberDTO> lookUp(BaseFilter filter) {

        ResultList<SerialNumberDTO> resultList = customerFiscalModuleRepository.getLookUp(filter);
        return new PageImpl<>
                (resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public void delete(Long id) {
        CustomerFiscalModule customerFiscalModule = customerFiscalModuleRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(FISCAL_MODULE));

        Fiscalization fiscalization = fiscalizationRepository.findFirstByCustomerIdAndFiscalModuleId(customerFiscalModule.getCustomerId(), customerFiscalModule.getFiscalModuleId());
        if (fiscalization != null) {
            if (fiscalization.getOfdApplication() != null && !ApplicationType.DEREGISTER.equals(fiscalization.getOfdApplication().getApplicationType())) {
                throw validationService.badRequestExceptionThrow(FISCALIZATION_DELETE_VALIDATION_MESSAGE, customerFiscalModule.getFiscalModule().getSerialNumber()).get();
            }
            //Update Fiscalization
            fiscalization.setDeleted(true);
            fiscalization.setStatus(DeviceStatus.DELETED);
            fiscalizationRepository.save(fiscalization);
            cacheService.evictFiscalCache(fiscalization.getHashKey());
        }

        FiscalModule fiscalModule = fiscalModuleRepository.findById(customerFiscalModule.getFiscalModuleId()).orElseThrow(validationService.entityNotFoundExceptionThrow(FISCAL_MODULE));
        CustomerBranchFiscalModule customerBranchFiscalModule = customerBranchFiscalModuleRepository.findFirstByCustomerBranch_CustomerIdAndFiscalModuleId(customerFiscalModule.getCustomerId(), fiscalModule.getId());

        if (customerBranchFiscalModule != null) {
            customerBranchFiscalModule.setDeleted(true);
            customerBranchFiscalModule.setStatus(DeviceStatus.DELETED);
            customerBranchFiscalModuleRepository.save(customerBranchFiscalModule);
            warehouseHistoryService.create(fiscalModule.getSerialNumber(), customerBranchFiscalModule.getId(), EntityTypeEnum.CUSTOMER_BRANCH_FISCAL_MODULE, DeviceStatus.DELETED);
            logHistoryService.logHistory(fiscalModule.getSerialNumber(), true, Action.DELETE, customerBranchFiscalModule.getCustomerBranch().getName(), null);
        }

        customerFiscalModule.setDeleted(true);
        customerFiscalModule.setStatus(DeviceStatus.DELETED);
        customerFiscalModuleRepository.save(customerFiscalModule);
        warehouseHistoryService.create(fiscalModule.getSerialNumber(), customerFiscalModule.getId(), EntityTypeEnum.CUSTOMER_FISCAL_MODULE, DeviceStatus.DELETED);
        logHistoryService.logHistory(fiscalModule.getSerialNumber(), true, Action.DELETE, customerFiscalModule.getCustomer().getName(), null);
        fiscalModuleService.updateStatusAndClear(fiscalModule, DeviceStatus.UNREGISTERED);
    }

    @Override
    public void deleteByFiscalModuleId(Long fiscalModuleId) {
        CustomerFiscalModule customerFiscalModule = customerFiscalModuleRepository.findByFiscalModuleId(fiscalModuleId);
        if (customerFiscalModule == null) {
            throw validationService.entityNotFoundExceptionThrow(FISCAL_MODULE).get();
        }
        customerFiscalModule.setStatus(DeviceStatus.DELETED);
        customerFiscalModule.setDeleted(true);
        customerFiscalModuleRepository.save(customerFiscalModule);
        warehouseHistoryService.create(customerFiscalModule.getFiscalModule().getSerialNumber(), customerFiscalModule.getId(), EntityTypeEnum.CUSTOMER_FISCAL_MODULE, DeviceStatus.DELETED);
        logHistoryService.logHistory(customerFiscalModule.getFiscalModule().getSerialNumber(), true, Action.DELETE, customerFiscalModule.getCustomer().getName(), null);
    }

    @Override
    public void add(CustomerFiscalModuleDTO customerFiscalModuleDTO) {
        Customer customer = customerRepository.findById(customerFiscalModuleDTO.getCustomerId()).orElseThrow(validationService.entityNotFoundExceptionThrow(CUSTOMER));
        CustomerBranch customerBranch = customerBranchRepository.findById(customerFiscalModuleDTO.getCustomerBranchId()).orElseThrow(validationService.entityNotFoundExceptionThrow(BRANCH));

        FiscalModule fiscalModule = fiscalModuleRepository.findBySerialNumber(customerFiscalModuleDTO.getSerialNumber());
        if (fiscalModule == null) {
            throw validationService.entityNotFoundExceptionThrow(FISCAL_MODULE).get();
        }

        if (customerFiscalModuleRepository.totalByFiscalModuleId(fiscalModule.getId()).intValue() > 0) {
            throw validationService.entityAlreadyExistExceptionThrow(FISCAL_MODULE, SERIAL_NUMBER, fiscalModule.getSerialNumber()).get();
        }

        BranchFiscalModule branchFiscalModule = branchFiscalModuleRepository.findFirstByBranchIdAndFiscalModuleId(SecurityUtils.getBranchId(), fiscalModule.getId());
        if (branchFiscalModule == null) {
            throw validationService.entityNotFoundExceptionThrow(FISCAL_MODULE).get();
        }

        branchFiscalModule.setStatus(DeviceStatus.TRANSFERRED);
        branchFiscalModuleRepository.save(branchFiscalModule);
        warehouseHistoryService.create(fiscalModule.getSerialNumber(), branchFiscalModule.getId(), EntityTypeEnum.BRANCH_FISCAL_MODULE, DeviceStatus.TRANSFERRED);

        CustomerFiscalModule customerFiscalModule = new CustomerFiscalModule();
        customerFiscalModule.setCustomerId(customer.getId());
        customerFiscalModule.setFiscalModuleId(fiscalModule.getId());
        customerFiscalModule.setStatus(DeviceStatus.REGISTERED);
        customerFiscalModuleRepository.save(customerFiscalModule);
        warehouseHistoryService.create(fiscalModule.getSerialNumber(), customerFiscalModule.getId(), EntityTypeEnum.CUSTOMER_FISCAL_MODULE, DeviceStatus.REGISTERED);
        logHistoryService.logHistory(fiscalModule.getSerialNumber(), true, Action.TRANSFER, branchFiscalModule.getBranch().getName(), customer.getName());

        //Transfer from customer
        customerFiscalModule.setStatus(DeviceStatus.TRANSFERRED);
        customerFiscalModuleRepository.save(customerFiscalModule);
        warehouseHistoryService.create(fiscalModule.getSerialNumber(), customerFiscalModule.getId(), EntityTypeEnum.CUSTOMER_FISCAL_MODULE, DeviceStatus.TRANSFERRED);

        //Register to customer branch
        CustomerBranchFiscalModule customerBranchFiscalModule = new CustomerBranchFiscalModule();
        customerBranchFiscalModule.setCustomerBranchId(customerBranch.getId());
        customerBranchFiscalModule.setFiscalModuleId(fiscalModule.getId());
        customerBranchFiscalModule.setStatus(DeviceStatus.REGISTERED);
        customerBranchFiscalModuleRepository.save(customerBranchFiscalModule);
        warehouseHistoryService.create(fiscalModule.getSerialNumber(), customerBranchFiscalModule.getId(), EntityTypeEnum.CUSTOMER_BRANCH_FISCAL_MODULE, DeviceStatus.REGISTERED);
        logHistoryService.logHistory(fiscalModule.getSerialNumber(), true, Action.TRANSFER, customerFiscalModule.getCustomer().getName(), customerBranch.getName());

        //Update fiscal module status
        fiscalModuleService.updateStatus(fiscalModule, DeviceStatus.REGISTERED);
    }

    @Override
    public void register(CustomerFiscalModuleRegisterDTO customerFiscalModuleRegisterDTO) {
        Customer customer = customerRepository.findById(customerFiscalModuleRegisterDTO.getCustomerId()).orElseThrow(validationService.entityNotFoundExceptionThrow(CUSTOMER));
        CustomerBranch customerBranch = customerBranchRepository.findById(customerFiscalModuleRegisterDTO.getCustomerBranchId()).orElseThrow(validationService.entityNotFoundExceptionThrow(BRANCH));

        Long branchId;
        if (SecurityUtils.getBranchId() == null || SecurityUtils.getBranchId() == 0) {
            User user = userRepository.findById(SecurityUtils.getCurrentUserId()).orElseThrow(validationService.entityNotFoundExceptionThrow(USER));
            branchId = user.getBranchId();
        } else {
            branchId = SecurityUtils.getBranchId();
        }

        Branch branch = branchRepository.findById(branchId).orElseThrow(validationService.entityNotFoundExceptionThrow(BRANCH));

        FiscalModule fiscalModule = fiscalModuleRepository.findBySerialNumber(customerFiscalModuleRegisterDTO.getSerialNumber());
        if (fiscalModule == null) {
            throw validationService.entityNotFoundExceptionThrow(FISCAL_MODULE).get();
        }

        if (customerFiscalModuleRegisterDTO.isFromRegistry()) {

            if (companyFiscalModuleRepository.totalByFiscalModuleId(customerFiscalModuleRegisterDTO.getCustomerId(), fiscalModule.getId()).intValue() > 0) {
                throw validationService.entityAlreadyExistExceptionThrow(FISCAL_MODULE, SERIAL_NUMBER, fiscalModule.getSerialNumber()).get();
            }

            CompanyFiscalModule companyFiscalModule = companyFiscalModuleRepository.findFirstByCompanyIdAndFiscalModuleId(customer.getCompanyId(), fiscalModule.getId());
            if (companyFiscalModule == null) {
                companyFiscalModule = new CompanyFiscalModule();
                companyFiscalModule.setFiscalModuleId(fiscalModule.getId());
                //companyFiscalModule.setFiscalModule(fiscalModule);
                companyFiscalModule.setCompanyId(customer.getCompanyId());
                // companyFiscalModule.setCompany(customer.getCompany());
                companyFiscalModule.setStatus(DeviceStatus.REGISTERED);
                companyFiscalModuleRepository.save(companyFiscalModule);
                warehouseHistoryService.create(fiscalModule.getSerialNumber(), companyFiscalModule.getId(), EntityTypeEnum.COMPANY_FISCAL_MODULE, DeviceStatus.REGISTERED);
                logHistoryService.logHistory(fiscalModule.getSerialNumber(), true, Action.TRANSFER, "Реестр", customer.getCompany().getName());
            }

            //Transfer
            companyFiscalModule.setFiscalModuleId(fiscalModule.getId());
            //companyFiscalModule.setFiscalModule(fiscalModule);
            companyFiscalModule.setCompanyId(customer.getCompanyId());
            //companyFiscalModule.setCompany(customer.getCompany());
            companyFiscalModule.setStatus(DeviceStatus.TRANSFERRED);
            companyFiscalModuleRepository.save(companyFiscalModule);
            warehouseHistoryService.create(fiscalModule.getSerialNumber(), companyFiscalModule.getId(), EntityTypeEnum.COMPANY_FISCAL_MODULE, DeviceStatus.TRANSFERRED);

            //Register to branch
            BranchFiscalModule branchFiscalModule = branchFiscalModuleRepository.findFirstByBranchIdAndFiscalModuleId(branchId, fiscalModule.getId());
            if (branchFiscalModule == null) {
                branchFiscalModule = new BranchFiscalModule();
                branchFiscalModule.setBranchId(branchId);
                //branchFiscalModule.setBranch(branch);
                branchFiscalModule.setFiscalModuleId(fiscalModule.getId());
                //branchFiscalModule.setFiscalModule(fiscalModule);
                branchFiscalModule.setStatus(DeviceStatus.REGISTERED);
                branchFiscalModuleRepository.save(branchFiscalModule);
                warehouseHistoryService.create(fiscalModule.getSerialNumber(), branchFiscalModule.getId(), EntityTypeEnum.BRANCH_FISCAL_MODULE, DeviceStatus.REGISTERED);
            }
            logHistoryService.logHistory(fiscalModule.getSerialNumber(), true, Action.TRANSFER, customer.getName(), branch.getName());

            //Transfer
            branchFiscalModule.setBranchId(branchId);
            //branchFiscalModule.setBranch(branch);
            branchFiscalModule.setFiscalModuleId(fiscalModule.getId());
            //branchFiscalModule.setFiscalModule(fiscalModule);
            branchFiscalModule.setStatus(DeviceStatus.TRANSFERRED);
            branchFiscalModuleRepository.save(branchFiscalModule);
            warehouseHistoryService.create(fiscalModule.getSerialNumber(), branchFiscalModule.getId(), EntityTypeEnum.BRANCH_FISCAL_MODULE, DeviceStatus.TRANSFERRED);
        } else {
            BranchFiscalModule branchFiscalModule = branchFiscalModuleRepository.findFirstByBranchIdAndFiscalModuleIdAndStatus(branchId, fiscalModule.getId(), DeviceStatus.REGISTERED);
            branchFiscalModule.setBranchId(branchId);
            branchFiscalModule.setBranch(branch);
            branchFiscalModule.setFiscalModuleId(fiscalModule.getId());
            branchFiscalModule.setFiscalModule(fiscalModule);
            branchFiscalModule.setStatus(DeviceStatus.TRANSFERRED);
            branchFiscalModuleRepository.save(branchFiscalModule);
            warehouseHistoryService.create(fiscalModule.getSerialNumber(), branchFiscalModule.getId(), EntityTypeEnum.BRANCH_FISCAL_MODULE, DeviceStatus.TRANSFERRED);
        }

        //Register to customer
        CustomerFiscalModule customerFiscalModule = new CustomerFiscalModule();
        customerFiscalModule.setCustomerId(customer.getId());
        //customerFiscalModule.setCustomer(customer);
        customerFiscalModule.setFiscalModuleId(fiscalModule.getId());
        //customerFiscalModule.setFiscalModule(fiscalModule);
        customerFiscalModuleRepository.save(customerFiscalModule);
        warehouseHistoryService.create(fiscalModule.getSerialNumber(), customerFiscalModule.getId(), EntityTypeEnum.CUSTOMER_FISCAL_MODULE, DeviceStatus.REGISTERED);

        logHistoryService.logHistory(fiscalModule.getSerialNumber(), true, Action.TRANSFER, branch.getName(), customer.getName());

        //Transfer from customer
        customerFiscalModule.setStatus(DeviceStatus.TRANSFERRED);
        customerFiscalModuleRepository.save(customerFiscalModule);
        warehouseHistoryService.create(fiscalModule.getSerialNumber(), customerFiscalModule.getId(), EntityTypeEnum.CUSTOMER_FISCAL_MODULE, DeviceStatus.TRANSFERRED);

        //Register to customer branch
        CustomerBranchFiscalModule customerBranchFiscalModule = new CustomerBranchFiscalModule();
        customerBranchFiscalModule.setCustomerBranchId(customerBranch.getId());
        //customerBranchFiscalModule.setCustomerBranch(customerBranch);
        customerBranchFiscalModule.setFiscalModuleId(fiscalModule.getId());
        //customerBranchFiscalModule.setFiscalModule(fiscalModule);
        customerBranchFiscalModule.setStatus(DeviceStatus.REGISTERED);
        customerBranchFiscalModuleRepository.save(customerBranchFiscalModule);
        warehouseHistoryService.create(fiscalModule.getSerialNumber(), customerBranchFiscalModule.getId(), EntityTypeEnum.CUSTOMER_BRANCH_FISCAL_MODULE, DeviceStatus.REGISTERED);

        logHistoryService.logHistory(fiscalModule.getSerialNumber(), true, Action.TRANSFER, customer.getName(), customerBranch.getName());

        //Update device status
        fiscalModuleService.updateStatus(fiscalModule, DeviceStatus.REGISTERED);
    }

    @Override
    public String getHtmlTemplate(Long id) {
        CustomerFiscalModule customerFiscalModule = customerFiscalModuleRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(FISCAL_MODULE));
        CustomerFiscalModuleTemplateDTO dto = new CustomerFiscalModuleTemplateDTO();
        Customer customer = customerFiscalModule.getCustomer();

        dto.setCompanyName(customer.getCompanyName());
        dto.setCustomerTin(customer.getTin());
        dto.setFiscalModuleSerial(customerFiscalModule.getFiscalModule().getSerialNumber());
        dto.setBusinessType(customer.getBusinessType().getName());
        dto.setBankName(customer.getCustomerBank().getBank().getNameNotNullable());
        dto.setMfo(customer.getCustomerBank().getBank().getCode());
        dto.setAccountNumber(customer.getCustomerBank().getAccountNumber());
        dto.setDate(LocalDateTime.now());
        customerEmployeeRepository.findFirstByCustomerIdAndRole(customer.getId(), Role.DIRECTOR).ifPresent(customerEmployee -> dto.setOwner(customerEmployee.getName()));
        return thymeleafService.generateFiscalModuleDoc(dto);
    }
}
