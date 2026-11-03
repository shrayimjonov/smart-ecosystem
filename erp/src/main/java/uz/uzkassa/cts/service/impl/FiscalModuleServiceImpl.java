package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.constants.CacheConstants;
import uz.uzkassa.cts.domain.FiscalModule;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.device.CompanyTerminalListDTO;
import uz.uzkassa.cts.dto.device.RegistryListDTO;
import uz.uzkassa.cts.dto.device.SerialNumberDTO;
import uz.uzkassa.cts.dto.device.admin.*;
import uz.uzkassa.cts.dto.fm.FiscalModuleDTO;
import uz.uzkassa.cts.dto.fm.FiscalModuleForTransferListDTO;
import uz.uzkassa.cts.dto.fm.FiscalModuleListDTO;
import uz.uzkassa.cts.dto.fm.FiscalModuleRegisterDTO;
import uz.uzkassa.cts.dto.fm.admin.FiscalModuleDetailAdminDTO;
import uz.uzkassa.cts.dto.fm.admin.FiscalModuleListAdminDTO;
import uz.uzkassa.cts.enums.Action;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.*;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.CacheService;
import uz.uzkassa.cts.service.FiscalModuleService;
import uz.uzkassa.cts.service.LogHistoryService;
import uz.uzkassa.cts.service.WarehouseHistoryService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@Slf4j
public class FiscalModuleServiceImpl extends BaseServiceImpl<FiscalModule, Long> implements FiscalModuleService {

    private final FiscalModuleRepository fiscalModuleRepository;
    private final WarehouseHistoryService warehouseHistoryService;
    private final UserRepository userRepository;
    private final FiscalizationRepository fiscalizationRepository;
    private final CustomerBranchFiscalModuleRepository customerBranchFiscalModuleRepository;
    private final CustomerFiscalModuleRepository customerFiscalModuleRepository;
    private final BranchFiscalModuleRepository branchFiscalModuleRepository;
    private final CompanyFiscalModuleRepository companyFiscalModuleRepository;
    private final CacheService cacheService;
    private final LogHistoryService logHistoryService;
    private final ValidationContext validationContext;

    public FiscalModuleServiceImpl(FiscalModuleRepository fiscalModuleRepository,
                                   WarehouseHistoryService warehouseHistoryService,
                                   UserRepository userRepository,
                                   FiscalizationRepository fiscalizationRepository,
                                   CustomerBranchFiscalModuleRepository customerBranchFiscalModuleRepository,
                                   CustomerFiscalModuleRepository customerFiscalModuleRepository,
                                   BranchFiscalModuleRepository branchFiscalModuleRepository, CompanyFiscalModuleRepository companyFiscalModuleRepository, CacheService cacheService, LogHistoryService logHistoryService, ValidationContext validationContext) {
        super(fiscalModuleRepository);
        this.fiscalModuleRepository = fiscalModuleRepository;
        this.warehouseHistoryService = warehouseHistoryService;
        this.userRepository = userRepository;
        this.fiscalizationRepository = fiscalizationRepository;
        this.customerBranchFiscalModuleRepository = customerBranchFiscalModuleRepository;
        this.customerFiscalModuleRepository = customerFiscalModuleRepository;
        this.branchFiscalModuleRepository = branchFiscalModuleRepository;
        this.companyFiscalModuleRepository = companyFiscalModuleRepository;
        this.cacheService = cacheService;
        this.logHistoryService = logHistoryService;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Long create(FiscalModuleDTO fiscalModuleDTO) {
        validationContext.validate(fiscalModuleDTO);
        FiscalModule fiscalModule = new FiscalModule();
        fiscalModule.setSerialNumber(fiscalModuleDTO.getSerialNumber().toUpperCase());
        if (getCurrentUserId() != null) {
            userRepository.findById(getCurrentUserId()).ifPresent(user -> fiscalModule.setCompanyId(user.getCompanyId()));
        }
        fiscalModuleRepository.save(fiscalModule);
        cacheService.putCache(CacheConstants.FISCAL_MODULE_BY_SERIAL_NUMBER, fiscalModuleDTO.getSerialNumber().toUpperCase(), fiscalModule);
        logHistoryService.logHistory(fiscalModule.getSerialNumber(), true, Action.TRANSFER, "Склад компании", "Реестр");
        return fiscalModule.getId();
    }

    @Override
    public Page<FiscalModuleListAdminDTO> getListForAdmin(BaseFilter filter) {
        ResultList<FiscalModule> resultList = fiscalModuleRepository.getResultList(filter);
        List<FiscalModuleListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(fiscalModule -> {
                    FiscalModuleListAdminDTO fiscalModuleListDTO = new FiscalModuleListAdminDTO();
                    fiscalModuleListDTO.setId(fiscalModule.getId());
                    fiscalModuleListDTO.setSerialNumber(fiscalModule.getSerialNumber());
                    fiscalModuleListDTO.setStatus(fiscalModule.getStatus());
                    fiscalModuleListDTO.setCreatedDate(fiscalModule.getCreatedDate());
                    fiscalModuleListDTO.setCreator(fiscalModule.getCreator().toCommonDTO());
                    return fiscalModuleListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public FiscalModuleDetailAdminDTO getForAdmin(Long id) {
        return fiscalModuleRepository.findById(id).map(fiscalModule -> {
            FiscalModuleDetailAdminDTO fiscalModuleDetailDTO = new FiscalModuleDetailAdminDTO();
            fiscalModuleDetailDTO.setFiscalModule(fiscalModule.toSerialNumberDTO());

            RegistryDetailAdminDTO registryFiscalModuleDTO = new RegistryDetailAdminDTO();
            registryFiscalModuleDTO.setStatus(fiscalModule.getStatus());
            registryFiscalModuleDTO.setCreator(fiscalModule.getCreator().toCommonDTO());
            registryFiscalModuleDTO.setCreatedDate(fiscalModule.getCreatedDate());
            fiscalModuleDetailDTO.setRegistryFiscalModule(registryFiscalModuleDTO);

            companyFiscalModuleRepository.findFirstByFiscalModuleIdOrderByIdDesc(id).ifPresent(companyFiscalModule -> {
                CompanyDeviceDetailAdminDTO companyFiscalModuleDTO = new CompanyDeviceDetailAdminDTO();
                companyFiscalModuleDTO.setStatus(companyFiscalModule.getStatus());
                companyFiscalModuleDTO.setCreator(companyFiscalModule.getCreator().toCommonDTO());
                companyFiscalModuleDTO.setCreatedDate(companyFiscalModule.getCreatedDate());
                companyFiscalModuleDTO.setCompany(companyFiscalModule.getCompany().toShortDTO());
                fiscalModuleDetailDTO.setCompanyFiscalModule(companyFiscalModuleDTO);
            });

            branchFiscalModuleRepository.findFirstByFiscalModuleIdOrderByIdDesc(id).ifPresent(branchFiscalModule -> {
                BranchDeviceDetailAdminDTO branchFiscalModuleDTO = new BranchDeviceDetailAdminDTO();
                branchFiscalModuleDTO.setStatus(branchFiscalModule.getStatus());
                branchFiscalModuleDTO.setCreator(branchFiscalModule.getCreator().toCommonDTO());
                branchFiscalModuleDTO.setCreatedDate(branchFiscalModule.getCreatedDate());
                branchFiscalModuleDTO.setBranch(branchFiscalModule.getBranch().toCommonDTO());
                fiscalModuleDetailDTO.setBranchFiscalModule(branchFiscalModuleDTO);
            });

            customerFiscalModuleRepository.findFirstByFiscalModuleIdOrderByIdDesc(id).ifPresent(customerFiscalModule -> {
                CustomerDeviceDetailAdminDTO customerFiscalModuleDTO = new CustomerDeviceDetailAdminDTO();
                customerFiscalModuleDTO.setStatus(customerFiscalModule.getStatus());
                customerFiscalModuleDTO.setCreator(customerFiscalModule.getCreator().toCommonDTO());
                customerFiscalModuleDTO.setCreatedDate(customerFiscalModule.getCreatedDate());
                customerFiscalModuleDTO.setCustomer(customerFiscalModule.getCustomer().toShortDTO());
                fiscalModuleDetailDTO.setCustomerFiscalModule(customerFiscalModuleDTO);
            });

            customerBranchFiscalModuleRepository.findFirstByFiscalModuleIdOrderByIdDesc(id).ifPresent(customerBranchFiscalModule -> {
                CustomerBranchDeviceDetailAdminDTO customerBranchFiscalModuleDTO = new CustomerBranchDeviceDetailAdminDTO();
                customerBranchFiscalModuleDTO.setStatus(customerBranchFiscalModule.getStatus());
                customerBranchFiscalModuleDTO.setCreator(customerBranchFiscalModule.getCreator().toCommonDTO());
                customerBranchFiscalModuleDTO.setCreatedDate(customerBranchFiscalModule.getCreatedDate());
                customerBranchFiscalModuleDTO.setCustomerBranch(customerBranchFiscalModule.getCustomerBranch().toCommonDTO());
                fiscalModuleDetailDTO.setCustomerBranchFiscalModule(customerBranchFiscalModuleDTO);
            });

            fiscalizationRepository.findFirstByDeviceIdOrderByIdDesc(id).ifPresent(fiscalization -> {
                FiscalizationDetailAdminDTO fiscalizationDTO = new FiscalizationDetailAdminDTO();
                fiscalizationDTO.setStatus(fiscalization.getStatus());
                fiscalizationDTO.setCreator(fiscalization.getCreator().toCommonDTO());
                fiscalizationDTO.setCreatedDate(fiscalization.getCreatedDate());
                fiscalizationDTO.setCompany(fiscalization.getCustomer().getCompany().toShortDTO());
                fiscalizationDTO.setBranch(fiscalization.getBranch().toCommonDTO());
                fiscalizationDTO.setCustomer(fiscalization.getCustomer().toShortDTO());
                fiscalizationDTO.setCustomerBranch(fiscalization.getCustomerBranch().toCommonDTO());
                fiscalizationDTO.setDevice(fiscalization.getDevice().toSerialNumberDTO());
                fiscalModuleDetailDTO.setFiscalization(fiscalizationDTO);
            });

            return fiscalModuleDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(FISCAL_MODULE));
    }

    @Override
    public Page<SerialNumberDTO> lookUp(FiscalModuleLookUpFilter filter) {
        if (!filter.isFromRegistry()) {
            CompanyDeviceFilter companyDeviceFilter = new CompanyDeviceFilter();
            if (SecurityUtils.getBranchId() == null || SecurityUtils.getBranchId() == 0) {
                User user = userRepository.findById(SecurityUtils.getCurrentUserId()).orElseThrow(validationService.entityNotFoundExceptionThrow(USER));
                companyDeviceFilter.setBranchId(user.getBranchId());
            } else {
                companyDeviceFilter.setBranchId(SecurityUtils.getBranchId());
            }
            companyDeviceFilter.setSearch(filter.getSearch());
            companyDeviceFilter.setOrderBy(filter.getOrderBy());
            companyDeviceFilter.setPage(filter.getPage());
            companyDeviceFilter.setSortOrder(filter.getSortOrder());
            companyDeviceFilter.setSize(filter.getSize());
            ResultList<SerialNumberDTO> resultList = branchFiscalModuleRepository.getListForTransfer2(companyDeviceFilter);
            return new PageImpl<>
                    (resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
        }

        ResultList<SerialNumberDTO> resultList = fiscalModuleRepository.getLookUp(filter);
        return new PageImpl<>
                (resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    @Transactional
    public FiscalModuleRegisterDTO add(FiscalModuleRegisterDTO fiscalModuleRegisterDTO) {
        if (fiscalModuleRepository.findBySerialNumber(fiscalModuleRegisterDTO.getSerialNumber().toUpperCase()) != null) {
            throw validationService.entityAlreadyExistExceptionThrow(FISCAL_MODULE, SERIAL_NUMBER, fiscalModuleRegisterDTO.getSerialNumber().toUpperCase()).get();
        }

        FiscalModule fiscalModule = new FiscalModule();
        fiscalModule.setSerialNumber(fiscalModuleRegisterDTO.getSerialNumber().toUpperCase());
        fiscalModule.setCompanyId(getCompanyId(fiscalModuleRegisterDTO.getCompanyId()));
        fiscalModuleRepository.save(fiscalModule);
        cacheService.putCache(CacheConstants.FISCAL_MODULE_BY_SERIAL_NUMBER, fiscalModuleRegisterDTO.getSerialNumber().toUpperCase(), fiscalModule);
        fiscalModuleRegisterDTO.setId(fiscalModule.getId());
        logHistoryService.logHistory(fiscalModule.getSerialNumber(), true, Action.TRANSFER, "Склад компании", "Реестр");
        return fiscalModuleRegisterDTO;
    }

    @Override
    @Transactional
    public void batchAdd(List<SerialNumberDTO> serialNumbers) {
        List<FiscalModule> result = new ArrayList<>();
        serialNumbers.forEach(fiscalModuleDTO -> {
            if (fiscalModuleRepository.findBySerialNumber(fiscalModuleDTO.getSerialNumber().toUpperCase()) == null) {
                FiscalModule fiscalModule = new FiscalModule();
                fiscalModule.setSerialNumber(fiscalModuleDTO.getSerialNumber().toUpperCase());
                fiscalModule.setCompanyId(getCompanyId());
                result.add(fiscalModule);
                cacheService.putCache(CacheConstants.FISCAL_MODULE_BY_SERIAL_NUMBER, fiscalModuleDTO.getSerialNumber().toUpperCase(), fiscalModule);
                logHistoryService.logHistory(fiscalModule.getSerialNumber(), true, Action.TRANSFER, "Склад компании", "Реестр");
            }
        });
        if (!result.isEmpty()) {
            fiscalModuleRepository.saveAll(result);
        }
    }

    @Override
    public Page<FiscalModuleListDTO> list(BaseFilter filter) {
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            if (DeviceStatus.get(filter.getStatus()) == null) {
                throw validationService.badRequestExceptionThrow(INVALID_INPUT, filter.getStatus()).get();
            }
        }
        ResultList<FiscalModuleListDTO> resultList = fiscalModuleRepository.getList(filter);
        return new PageImpl<>
                (resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public List<DeviceStatisticsAdminDTO> getStatisticsForAdmin(BaseFilter filter) {
        return fiscalModuleRepository.getStatistics(filter);
    }

    @Override
    @Transactional
    public Long importFiscalModule(MultipartFile file) {
        try {
            final DataFormatter formatter = new DataFormatter();
            InputStream inputStream = new ByteArrayInputStream(file.getBytes());
            Workbook wb = WorkbookFactory.create(inputStream);
            Sheet sheet = wb.getSheetAt(0);
            boolean header = true;
            int counter = 0;
            for (Row row : sheet) {
                if (header) {
                    header = false;
                    continue;
                }
                Cell cell0 = row.getCell(0);
                if (cell0 != null) {
                    String serialNumber;
                    try {
                        serialNumber = substring(255, formatter.formatCellValue(cell0)).trim().toUpperCase();
                    } catch (Exception e) {
                        throw validationService.badRequestExceptionThrow(e.getMessage() + " [Row: " + cell0.getAddress().getRow() + " Column:" + cell0.getAddress().getColumn() + "]").get();
                    }
                    if (StringUtils.isNoneEmpty(serialNumber)) {
                        try {
                            if (fiscalModuleRepository.findFirstBySerialNumber(serialNumber) == null) {
                                FiscalModule fiscalModule = new FiscalModule();
                                fiscalModule.setSerialNumber(serialNumber);
                                fiscalModule.setCompanyId(getCompanyId());
                                fiscalModuleRepository.save(fiscalModule);
                                cacheService.putCache(CacheConstants.FISCAL_MODULE_BY_SERIAL_NUMBER, serialNumber, fiscalModule);
                                logHistoryService.logHistory(serialNumber, true, Action.TRANSFER, "Склад компании", "Реестр");
                                counter++;
                            }
                        } catch (Exception e) {
                            log.error("Import fiscal modules error: {}", e.getMessage());
                        }

                    }
                }
            }

            return (long) counter;
        } catch (IOException | EncryptedDocumentException e) {
            log.error(e.getMessage(),e);
            throw validationService.badRequestExceptionThrow(INVALID_ACTION, e.getMessage()).get();
        }
    }

    @Override
    @Transactional
    public void updateStatusAndClear(FiscalModule fiscalModule, DeviceStatus status) {
        DeviceStatus oldStatus = fiscalModule.getStatus();
        fiscalModule.setStatus(status);
        fiscalModule.setCustomerBranchId(null);
        fiscalModule.setCustomerId(null);
        fiscalModule.setBranchId(null);
        fiscalModuleRepository.save(fiscalModule);
        warehouseHistoryService.create(fiscalModule.getSerialNumber(), fiscalModule.getId(), EntityTypeEnum.FISCAL_MODULE, status);
        cacheService.evict(CacheConstants.FISCAL_MODULE_BY_SERIAL_NUMBER, fiscalModule.getSerialNumber());
        logHistoryService.logHistory(fiscalModule.getSerialNumber(), true, Action.UPDATE, null, null, oldStatus.getCode(), status.getCode());
    }

    @Override
    @Transactional
    public void updateStatus(FiscalModule fiscalModule, DeviceStatus status) {
        DeviceStatus oldStatus = fiscalModule.getStatus();
        fiscalModule.setStatus(status);
        fiscalModuleRepository.save(fiscalModule);
        warehouseHistoryService.create(fiscalModule.getSerialNumber(), fiscalModule.getId(), EntityTypeEnum.FISCAL_MODULE, status);
        cacheService.evict(CacheConstants.FISCAL_MODULE_BY_SERIAL_NUMBER, fiscalModule.getSerialNumber());
        logHistoryService.logHistory(fiscalModule.getSerialNumber(), true, Action.UPDATE, null, null, oldStatus.getCode(), status.getCode());
    }

    @Override
    public Page<FiscalModuleForTransferListDTO> getListForTransfer(CompanyDeviceFilter filter) {
        FiscalModule fiscalModule;
        if (StringUtils.isNotEmpty(filter.getFromSerialNumber())) {
            fiscalModule = fiscalModuleRepository.findBySerialNumber(filter.getFromSerialNumber().toUpperCase());
            if (fiscalModule == null) {
                throw validationService.entityNotFoundExceptionThrow(FISCAL_MODULE, SERIAL_NUMBER, filter.getFromSerialNumber().toUpperCase()).get();
            }
            filter.setFiscalModuleId(fiscalModule.getId());
        }
        ResultList<FiscalModuleForTransferListDTO> resultList = fiscalModuleRepository.getListForTransfer(filter);
        return new PageImpl<>
                (resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }

    private String substring(int count, String field) {
        String subString = field;
        if (StringUtils.isNotEmpty(field) && field.length() > count) {
            subString = field.substring(0, count);
        }
        return subString;
    }

    @Override
    public Page<CompanyTerminalListDTO> listAll(CompanyBranchDeviceFilter filter) {
        ResultList<CompanyTerminalListDTO> resultList = fiscalModuleRepository.getListAll(filter);
        return new PageImpl<>
                (resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public Page<RegistryListDTO> listRegister(CompanyDeviceListFilter filter) {
        return fiscalModuleRepository.listRegister(filter);
    }
}
