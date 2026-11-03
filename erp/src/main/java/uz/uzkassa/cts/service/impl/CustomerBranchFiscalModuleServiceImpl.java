package uz.uzkassa.cts.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.device.BaseDeviceFiscalModuleDTO;
import uz.uzkassa.cts.dto.device.CustomerBranchDeviceListDTO;
import uz.uzkassa.cts.dto.device.SerialNumberDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.dto.fm.FiscalModuleAddDTO;
import uz.uzkassa.cts.dto.fm.admin.CustomerBranchFiscalModuleListAdminDTO;
import uz.uzkassa.cts.dto.fm.admin.FiscalModuleDetailAdminDTO;
import uz.uzkassa.cts.enums.Action;
import uz.uzkassa.cts.enums.ApplicationType;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
public class CustomerBranchFiscalModuleServiceImpl extends BaseServiceImpl<CustomerBranchFiscalModule, Long> implements CustomerBranchFiscalModuleService {

    private final CustomerBranchFiscalModuleRepository customerBranchFiscalModuleRepository;
    private final CustomerBranchRepository customerBranchRepository;
    private final FiscalModuleRepository fiscalModuleRepository;
    private final FiscalizationRepository fiscalizationRepository;
    private final CustomerRepository customerRepository;
    private final CustomerFiscalModuleRepository customerFiscalModuleRepository;
    private final WarehouseHistoryService warehouseHistoryService;
    private final FiscalModuleService fiscalModuleService;
    private final CacheService cacheService;
    private final LogHistoryService logHistoryService;

    public CustomerBranchFiscalModuleServiceImpl(CustomerBranchFiscalModuleRepository customerBranchFiscalModuleRepository,
                                                 CustomerBranchRepository customerBranchRepository,
                                                 FiscalModuleRepository fiscalModuleRepository,
                                                 FiscalizationRepository fiscalizationRepository,
                                                 CustomerRepository customerRepository,
                                                 CustomerFiscalModuleRepository customerFiscalModuleRepository,
                                                 WarehouseHistoryService warehouseHistoryService, FiscalModuleService fiscalModuleService, CacheService cacheService, LogHistoryService logHistoryService) {
        super(customerBranchFiscalModuleRepository);
        this.customerBranchFiscalModuleRepository = customerBranchFiscalModuleRepository;
        this.customerBranchRepository = customerBranchRepository;
        this.fiscalModuleRepository = fiscalModuleRepository;
        this.fiscalizationRepository = fiscalizationRepository;
        this.customerRepository = customerRepository;
        this.customerFiscalModuleRepository = customerFiscalModuleRepository;
        this.warehouseHistoryService = warehouseHistoryService;
        this.fiscalModuleService = fiscalModuleService;
        this.cacheService = cacheService;
        this.logHistoryService = logHistoryService;
    }

    @Override
    public Page<CustomerBranchFiscalModuleListAdminDTO> getListForAdmin(BaseFilter filter) {
        ResultList<CustomerBranchFiscalModule> resultList = customerBranchFiscalModuleRepository.getResultList(filter);
        List<CustomerBranchFiscalModuleListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(customerBranchFiscalModule -> {
                    CustomerBranchFiscalModuleListAdminDTO customerBranchFiscalModuleListDTO = new CustomerBranchFiscalModuleListAdminDTO();
                    customerBranchFiscalModuleListDTO.setId(customerBranchFiscalModule.getId());
                    customerBranchFiscalModuleListDTO.setFiscalModule(customerBranchFiscalModule.getFiscalModule().toSerialNumberDTO());
                    customerBranchFiscalModuleListDTO.setStatus(customerBranchFiscalModule.getStatus());
                    customerBranchFiscalModuleListDTO.setCreatedDate(customerBranchFiscalModule.getCreatedDate());
                    customerBranchFiscalModuleListDTO.setCreator(customerBranchFiscalModule.getCreator().toCommonDTO());
                    customerBranchFiscalModuleListDTO.setBranch(customerBranchFiscalModule.getCustomerBranch().getBranch().toCommonDTO());
                    customerBranchFiscalModuleListDTO.setCompany(customerBranchFiscalModule.getCustomerBranch().getBranch().getCompany().toShortDTO());
                    customerBranchFiscalModuleListDTO.setCustomer(customerBranchFiscalModule.getCustomerBranch().getCustomer().toShortDTO());
                    customerBranchFiscalModuleListDTO.setCustomerBranch(customerBranchFiscalModule.getCustomerBranch().toCommonDTO());
                    return customerBranchFiscalModuleListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public FiscalModuleDetailAdminDTO getForAdmin(Long id) {
        return customerBranchFiscalModuleRepository.findById(id).map(customerBranchFiscalModule -> fiscalModuleService.getForAdmin(customerBranchFiscalModule.getFiscalModuleId())).orElseThrow(validationService.entityNotFoundExceptionThrow(FISCAL_MODULE));
    }

    @Override
    public List<DeviceStatisticsAdminDTO> getStatisticsForAdmin(BaseFilter filter) {
        return customerBranchFiscalModuleRepository.getStatistics(filter);
    }

    @Deprecated
    @Override
    public void add(FiscalModuleAddDTO fiscalModuleDTO) {

        FiscalModule fiscalModule = fiscalModuleRepository.findBySerialNumber(fiscalModuleDTO.getSerialNumber());
        if (fiscalModule == null) {
            throw validationService.entityNotFoundExceptionThrow(FISCAL_MODULE).get();
        }

        Customer customer = customerRepository.findById(fiscalModuleDTO.getCustomerId()).orElseThrow(validationService.entityNotFoundExceptionThrow(CUSTOMER));
        CustomerBranch customerBranch = customerBranchRepository.findById(fiscalModuleDTO.getCustomerBranchId()).orElseThrow(validationService.entityNotFoundExceptionThrow(BRANCH));

        if (customerFiscalModuleRepository.findByCustomerIdAndFiscalModuleId(customer.getId(), fiscalModule.getId()) != null) {
            throw validationService.entityAlreadyExistExceptionThrow(FISCAL_MODULE, SERIAL_NUMBER, fiscalModule.getSerialNumber()).get();
        }

        if (customerBranchFiscalModuleRepository.findFirstByCustomerBranchIdAndFiscalModuleId(customerBranch.getId(), fiscalModule.getId()) != null) {
            throw validationService.entityAlreadyExistExceptionThrow(FISCAL_MODULE, SERIAL_NUMBER, fiscalModule.getSerialNumber()).get();
        }

        //Update fiscal module status
        fiscalModule.setStatus(DeviceStatus.REGISTERED);
        fiscalModule.setBranchId(customer.getBranchId());
        fiscalModule.setBranch(customer.getBranch());
        fiscalModule.setCustomerId(customer.getId());
        fiscalModule.setCustomer(customer);
        fiscalModule.setCustomerBranchId(customerBranch.getId());
        fiscalModule.setCustomerBranch(customerBranch);
        fiscalModule.setCompanyId(Optional.ofNullable(fiscalModule.getCompanyId()).orElse(getCompanyId()));
        fiscalModuleRepository.save(fiscalModule);
        warehouseHistoryService.create(fiscalModule.getSerialNumber(), fiscalModule.getId(), EntityTypeEnum.FISCAL_MODULE, DeviceStatus.REGISTERED);
        logHistoryService.logHistory(fiscalModule.getSerialNumber(), true, Action.TRANSFER, "Склад компании", "Реестр");
        logHistoryService.logHistory(fiscalModule.getSerialNumber(), true, Action.TRANSFER, "Реестр", customer.getCompany().getName());
        logHistoryService.logHistory(fiscalModule.getSerialNumber(), true, Action.TRANSFER, customer.getCompany().getName(), customerBranch.getCustomer().getName());

        //Transfer fiscal module to customer
        CustomerFiscalModule customerFiscalModule = new CustomerFiscalModule();
        customerFiscalModule.setCustomerId(customer.getId());
        customerFiscalModule.setCustomer(customer);
        customerFiscalModule.setFiscalModuleId(fiscalModule.getId());
        customerFiscalModule.setFiscalModule(fiscalModule);
        customerFiscalModule.setStatus(DeviceStatus.TRANSFERRED);
        customerFiscalModuleRepository.save(customerFiscalModule);
        warehouseHistoryService.create(fiscalModule.getSerialNumber(), customerFiscalModule.getId(), EntityTypeEnum.CUSTOMER_FISCAL_MODULE, DeviceStatus.TRANSFERRED);
        logHistoryService.logHistory(fiscalModule.getSerialNumber(), true, Action.TRANSFER, customerBranch.getCustomer().getName(), customerBranch.getName());

        //Register to Customer Branch
        CustomerBranchFiscalModule customerBranchFiscalModule = new CustomerBranchFiscalModule();
        customerBranchFiscalModule.setCustomerBranchId(customerBranch.getId());
        customerBranchFiscalModule.setCustomerBranch(customerBranch);
        customerBranchFiscalModule.setFiscalModuleId(fiscalModule.getId());
        customerBranchFiscalModule.setFiscalModule(fiscalModule);
        save(customerBranchFiscalModule);
        warehouseHistoryService.create(fiscalModule.getSerialNumber(), customerBranchFiscalModule.getId(), EntityTypeEnum.CUSTOMER_BRANCH_FISCAL_MODULE, DeviceStatus.REGISTERED);
    }

    @Override
    public void addNew(FiscalModuleAddDTO fiscalModuleDTO) {

        FiscalModule fiscalModule = fiscalModuleRepository.findBySerialNumber(fiscalModuleDTO.getSerialNumber());
        if (fiscalModule == null) {
            throw validationService.entityNotFoundExceptionThrow(FISCAL_MODULE).get();
        }

        Customer customer = customerRepository.findById(fiscalModuleDTO.getCustomerId()).orElseThrow(validationService.entityNotFoundExceptionThrow(CUSTOMER));
        CustomerBranch customerBranch = customerBranchRepository.findById(fiscalModuleDTO.getCustomerBranchId()).orElseThrow(validationService.entityNotFoundExceptionThrow(BRANCH));

        if (customerFiscalModuleRepository.findByCustomerIdAndFiscalModuleId(customer.getId(), fiscalModule.getId()) != null) {
            throw validationService.entityAlreadyExistExceptionThrow(FISCAL_MODULE, SERIAL_NUMBER, fiscalModule.getSerialNumber()).get();
        }
        if (customerBranchFiscalModuleRepository.findFirstByCustomerBranchIdAndFiscalModuleId(customerBranch.getId(), fiscalModule.getId()) != null) {
            throw validationService.entityAlreadyExistExceptionThrow(FISCAL_MODULE, SERIAL_NUMBER, fiscalModule.getSerialNumber()).get();
        }

        CustomerFiscalModule customerFiscalModule = customerFiscalModuleRepository.findByCustomerIdAndFiscalModuleId(customer.getId(), fiscalModule.getId());
        if (customerFiscalModule == null) {
            throw validationService.entityNotFoundExceptionThrow(FISCAL_MODULE).get();
        }
        customerFiscalModule.setStatus(DeviceStatus.TRANSFERRED);
        customerFiscalModuleRepository.save(customerFiscalModule);
        warehouseHistoryService.create(fiscalModule.getSerialNumber(), customerFiscalModule.getId(), EntityTypeEnum.CUSTOMER_FISCAL_MODULE, DeviceStatus.TRANSFERRED);

        //Register to Customer Branch
        CustomerBranchFiscalModule customerBranchFiscalModule = new CustomerBranchFiscalModule();
        customerBranchFiscalModule.setCustomerBranchId(customerBranch.getId());
        customerBranchFiscalModule.setCustomerBranch(customerBranch);
        customerBranchFiscalModule.setFiscalModuleId(fiscalModule.getId());
        customerBranchFiscalModule.setFiscalModule(fiscalModule);
        customerBranchFiscalModule.setStatus(DeviceStatus.REGISTERED);
        customerBranchFiscalModuleRepository.save(customerBranchFiscalModule);
        warehouseHistoryService.create(fiscalModule.getSerialNumber(), customerBranchFiscalModule.getId(), EntityTypeEnum.CUSTOMER_BRANCH_FISCAL_MODULE, DeviceStatus.REGISTERED);

        logHistoryService.logHistory(fiscalModule.getSerialNumber(), true, Action.TRANSFER, customer.getName(), customerBranch.getName());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SerialNumberDTO> lookUp(BaseFilter filter) {
        ResultList<SerialNumberDTO> resultList = customerBranchFiscalModuleRepository.getLookUp(filter);
        return new PageImpl<>
                (resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SerialNumberDTO> getItems(BaseFilter filter) {
        return customerBranchFiscalModuleRepository.getItems(filter);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerBranchDeviceListDTO> list(BaseFilter filter) {
        List<CustomerBranchDeviceListDTO> result = new ArrayList<>();
        ResultList<CustomerBranchFiscalModule> resultList = customerBranchFiscalModuleRepository.getList(filter);
        resultList.getList().forEach(customerBranchFiscalModule -> {
            CustomerBranchDeviceListDTO deviceListDTO = new CustomerBranchDeviceListDTO();
            deviceListDTO.setFiscalModuleSerial(customerBranchFiscalModule.getFiscalModule().getSerialNumber());
            deviceListDTO.setCustomerBranch(customerBranchFiscalModule.getCustomerBranch().getDTO());

            Fiscalization fiscalization = fiscalizationRepository.findFirstByCustomerIdAndCustomerBranchIdAndFiscalModuleId(filter.getCustomerId(), filter.getCustomerBranchId(), customerBranchFiscalModule.getFiscalModuleId());
            if (fiscalization != null) {
                deviceListDTO.setDeviceSerial(fiscalization.getDevice().getSerialNumber());
            }
            result.add(deviceListDTO);
        });
        return new PageImpl<>
                (result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public void deleteByFiscalModuleId(Long fiscalModuleId) {
        CustomerBranchFiscalModule customerBranchFiscalModule = customerBranchFiscalModuleRepository.findFirstByFiscalModuleId(fiscalModuleId);
        if (customerBranchFiscalModule == null) {
            throw validationService.entityNotFoundExceptionThrow(FISCAL_MODULE).get();
        }
        customerBranchFiscalModule.setStatus(DeviceStatus.DELETED);
        customerBranchFiscalModule.setDeleted(true);
        customerBranchFiscalModuleRepository.save(customerBranchFiscalModule);
        warehouseHistoryService.create(customerBranchFiscalModule.getFiscalModule().getSerialNumber(), customerBranchFiscalModule.getId(), EntityTypeEnum.CUSTOMER_BRANCH_FISCAL_MODULE, DeviceStatus.DELETED);
        logHistoryService.logHistory(customerBranchFiscalModule.getFiscalModule().getSerialNumber(), true, Action.DELETE, customerBranchFiscalModule.getCustomerBranch().getName(), null);
    }

    @Override
    public void delete(Long id) {
        CustomerBranchFiscalModule customerBranchFiscalModule = customerBranchFiscalModuleRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(FISCAL_MODULE));

        Fiscalization fiscalization = fiscalizationRepository.findFirstByCustomerIdAndCustomerBranchIdAndFiscalModuleId(customerBranchFiscalModule.getCustomerBranch().getCustomerId(), customerBranchFiscalModule.getCustomerBranchId(), customerBranchFiscalModule.getFiscalModuleId());
        if (fiscalization != null) {
            if (fiscalization.getOfdApplication() != null && !ApplicationType.DEREGISTER.equals(fiscalization.getOfdApplication().getApplicationType())) {
                throw validationService.badRequestExceptionThrow(FISCALIZATION_DELETE_VALIDATION_MESSAGE, customerBranchFiscalModule.getFiscalModule().getSerialNumber()).get();
            }

            //Update Fiscalization
            fiscalization.setDeleted(true);
            fiscalization.setStatus(DeviceStatus.DELETED);
            fiscalizationRepository.save(fiscalization);
            cacheService.evictFiscalCache(fiscalization.getHashKey());
        }

        //Delete from customer branch fiscal module
        customerBranchFiscalModule.setDeleted(true);
        customerBranchFiscalModule.setStatus(DeviceStatus.DELETED);
        customerBranchFiscalModuleRepository.save(customerBranchFiscalModule);
        warehouseHistoryService.create(customerBranchFiscalModule.getFiscalModule().getSerialNumber(), id, EntityTypeEnum.CUSTOMER_BRANCH_FISCAL_MODULE, DeviceStatus.DELETED);
        logHistoryService.logHistory(customerBranchFiscalModule.getFiscalModule().getSerialNumber(), true, Action.DELETE, customerBranchFiscalModule.getCustomerBranch().getName(), null);

        //Delete from customer fiscal module
        CustomerFiscalModule customerFiscalModule = customerFiscalModuleRepository.findByCustomerIdAndFiscalModuleId(customerBranchFiscalModule.getCustomerBranch().getCustomerId(), customerBranchFiscalModule.getFiscalModuleId());
        customerFiscalModule.setDeleted(true);
        customerFiscalModule.setStatus(DeviceStatus.DELETED);
        customerFiscalModuleRepository.save(customerFiscalModule);
        warehouseHistoryService.create(customerFiscalModule.getFiscalModule().getSerialNumber(), customerFiscalModule.getId(), EntityTypeEnum.CUSTOMER_FISCAL_MODULE, DeviceStatus.DELETED);
        logHistoryService.logHistory(customerFiscalModule.getFiscalModule().getSerialNumber(), true, Action.DELETE, customerFiscalModule.getCustomer().getName(), null);

        fiscalModuleService.updateStatusAndClear(customerBranchFiscalModule.getFiscalModule(), DeviceStatus.UNREGISTERED);
    }

    @Override
    public Page<BaseDeviceFiscalModuleDTO> listByCustomerBranch(BaseFilter filter) {
        ResultList<BaseDeviceFiscalModuleDTO> resultList = customerBranchFiscalModuleRepository.getListByCustomerBranch(filter);
        return new PageImpl<>
                (resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }
}
