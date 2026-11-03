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
import uz.uzkassa.cts.domain.Device;
import uz.uzkassa.cts.domain.DeviceModel;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.device.*;
import uz.uzkassa.cts.dto.device.admin.*;
import uz.uzkassa.cts.dto.payload.ContractReportPayload;
import uz.uzkassa.cts.dto.sync.SyncDeviceListDTO;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.filter.*;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqContractReportProducer;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.*;
import uz.uzkassa.cts.validator.ValidationContext;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@Slf4j
public class DeviceServiceImpl extends BaseServiceImpl<Device, Long> implements DeviceService {

    private final DeviceRepository deviceRepository;
    private final FiscalizationRepository fiscalizationRepository;
    private final CustomerBranchDeviceRepository customerBranchDeviceRepository;
    private final CustomerDeviceRepository customerDeviceRepository;
    private final BranchDeviceRepository branchDeviceRepository;
    private final CompanyDeviceRepository companyDeviceRepository;
    private final DeviceModelRepository deviceModelRepository;
    private final WarehouseHistoryService warehouseHistoryService;
    private final BranchDeviceService branchDeviceService;
    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;
    private final DeviceSuspendHistoryOldService deviceSuspendHistoryOldService;
    private final RabbitMqContractReportProducer rabbitMqContractReportProducer;
    private final CacheService cacheService;
    private final LogHistoryService logHistoryService;
    private final ValidationContext validationContext;

    public DeviceServiceImpl(DeviceRepository deviceRepository,
                             FiscalizationRepository fiscalizationRepository, CustomerBranchDeviceRepository customerBranchDeviceRepository,
                             CustomerDeviceRepository customerDeviceRepository, BranchDeviceRepository branchDeviceRepository,
                             CompanyDeviceRepository companyDeviceRepository, DeviceModelRepository deviceModelRepository,
                             WarehouseHistoryService warehouseHistoryService,
                             BranchDeviceService branchDeviceService,
                             UserRepository userRepository,
                             FileUploadService fileUploadService,
                             DeviceSuspendHistoryOldService deviceSuspendHistoryOldService,
                             RabbitMqContractReportProducer rabbitMqContractReportProducer,
                             CacheService cacheService, LogHistoryService logHistoryService, ValidationContext validationContext) {
        super(deviceRepository);
        this.deviceRepository = deviceRepository;
        this.fiscalizationRepository = fiscalizationRepository;
        this.customerBranchDeviceRepository = customerBranchDeviceRepository;
        this.customerDeviceRepository = customerDeviceRepository;
        this.branchDeviceRepository = branchDeviceRepository;
        this.companyDeviceRepository = companyDeviceRepository;
        this.deviceModelRepository = deviceModelRepository;
        this.warehouseHistoryService = warehouseHistoryService;
        this.branchDeviceService = branchDeviceService;
        this.userRepository = userRepository;
        this.fileUploadService = fileUploadService;
        this.deviceSuspendHistoryOldService = deviceSuspendHistoryOldService;
        this.rabbitMqContractReportProducer = rabbitMqContractReportProducer;
        this.cacheService = cacheService;
        this.logHistoryService = logHistoryService;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Long create(DeviceDTO deviceDTO) {
        validationContext.validate(deviceDTO);
        Device device = new Device();
        device.setSerialNumber(deviceDTO.getSerialNumber());
        device.setModelId(deviceDTO.getModelId());
        if (deviceDTO.getType() != null) {
            device.setType(deviceDTO.getType());
        }
        deviceRepository.save(device);
        if (DeviceType.VIRTUAL != deviceDTO.getType()) {
            /*Create device in Contract report*/
            rabbitMqContractReportProducer.createContractReport(new ContractReportPayload(device.getId(), EntityTypeEnum.DEVICE));
        }
        cacheService.putCache(CacheConstants.DEVICE_BY_SERIAL_NUMBER, deviceDTO.getSerialNumber(), device);
        logHistoryService.logHistory(deviceDTO.getSerialNumber(), false, Action.TRANSFER, "Склад компании", "Реестр");
        return device.getId();
    }

    @Override
    public Page<DeviceListAdminDTO> getListForAdmin(BaseFilter filter) {
        ResultList<Device> resultList = deviceRepository.getResultList(filter);
        List<DeviceListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(device -> {
                    DeviceListAdminDTO deviceListDTO = new DeviceListAdminDTO();
                    deviceListDTO.setId(device.getId());
                    deviceListDTO.setSerialNumber(device.getSerialNumber());
                    deviceListDTO.setStatus(device.getStatus());
                    deviceListDTO.setCreatedDate(device.getCreatedDate());
                    deviceListDTO.setCreator(device.getCreator().toCommonDTO());
                    return deviceListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public DeviceDetailAdminDTO getForAdmin(Long id) {
        return deviceRepository.findById(id).map(device -> {
            DeviceDetailAdminDTO deviceDetailDTO = new DeviceDetailAdminDTO();
            deviceDetailDTO.setDevice(device.toSerialNumberDTO());

            RegistryDetailAdminDTO registryDeviceDTO = new RegistryDetailAdminDTO();
            registryDeviceDTO.setStatus(device.getStatus());
            registryDeviceDTO.setCreator(device.getCreator().toCommonDTO());
            registryDeviceDTO.setCreatedDate(device.getCreatedDate());
            deviceDetailDTO.setRegistryDevice(registryDeviceDTO);

            companyDeviceRepository.findFirstByDeviceIdOrderByIdDesc(id).ifPresent(companyDevice -> {
                CompanyDeviceDetailAdminDTO companyDeviceDTO = new CompanyDeviceDetailAdminDTO();
                companyDeviceDTO.setStatus(companyDevice.getStatus());
                companyDeviceDTO.setCreator(companyDevice.getCreator().toCommonDTO());
                companyDeviceDTO.setCreatedDate(companyDevice.getCreatedDate());
                companyDeviceDTO.setCompany(companyDevice.getCompany().toShortDTO());
                deviceDetailDTO.setCompanyDevice(companyDeviceDTO);

            });

            branchDeviceRepository.findFirstByDeviceIdOrderByIdDesc(id).ifPresent(branchDevice -> {
                BranchDeviceDetailAdminDTO branchDeviceDTO = new BranchDeviceDetailAdminDTO();
                branchDeviceDTO.setStatus(branchDevice.getStatus());
                branchDeviceDTO.setCreator(branchDevice.getCreator().toCommonDTO());
                branchDeviceDTO.setCreatedDate(branchDevice.getCreatedDate());
                branchDeviceDTO.setBranch(branchDevice.getBranch().toCommonDTO());
                deviceDetailDTO.setBranchDevice(branchDeviceDTO);
            });

            customerDeviceRepository.findFirstByDeviceIdOrderByIdDesc(id).ifPresent(customerDevice -> {
                CustomerDeviceDetailAdminDTO customerDeviceDTO = new CustomerDeviceDetailAdminDTO();
                customerDeviceDTO.setStatus(customerDevice.getStatus());
                customerDeviceDTO.setCreator(customerDevice.getCreator().toCommonDTO());
                customerDeviceDTO.setCreatedDate(customerDevice.getCreatedDate());
                customerDeviceDTO.setCustomer(customerDevice.getCustomer().toShortDTO());
                deviceDetailDTO.setCustomerDevice(customerDeviceDTO);
            });

            customerBranchDeviceRepository.findFirstByDeviceIdOrderByIdDesc(id).ifPresent(customerBranchDevice -> {
                CustomerBranchDeviceDetailAdminDTO customerBranchDeviceDTO = new CustomerBranchDeviceDetailAdminDTO();
                customerBranchDeviceDTO.setStatus(customerBranchDevice.getStatus());
                customerBranchDeviceDTO.setCreator(customerBranchDevice.getCreator().toCommonDTO());
                customerBranchDeviceDTO.setCreatedDate(customerBranchDevice.getCreatedDate());
                customerBranchDeviceDTO.setCustomerBranch(customerBranchDevice.getCustomerBranch().toCommonDTO());
                deviceDetailDTO.setCustomerBranchDevice(customerBranchDeviceDTO);
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
                fiscalizationDTO.setFiscalModule(fiscalization.getFiscalModule().toSerialNumberDTO());
                deviceDetailDTO.setFiscalization(fiscalizationDTO);
            });

            return deviceDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(DEVICE));
    }

    @Override
    public List<DeviceStatisticsAdminDTO> getStatisticsForAdmin(BaseFilter filter) {
        return deviceRepository.getStatistics(filter);
    }

    @Override
    @Transactional
    public void batchAdd(List<DeviceDTO> deviceDTOList) {
        List<Device> devices = new ArrayList<>();
        deviceDTOList.forEach(deviceDTO -> {
            if (deviceRepository.findByModelIdAndSerialNumber(deviceDTO.getModelId(), deviceDTO.getSerialNumber()) == null) {
                DeviceModel deviceModel = deviceModelRepository.findById(deviceDTO.getModelId()).orElseThrow(validationService.entityNotFoundExceptionThrow(DEVICE));
                Device device = new Device();
                device.setSerialNumber(deviceDTO.getSerialNumber());
                device.setModelId(deviceModel.getId());
                //device.setCompanyId(deviceModel.getCompanyId());
                devices.add(device);
                cacheService.putCache(CacheConstants.DEVICE_BY_SERIAL_NUMBER, deviceDTO.getSerialNumber(), device);
                logHistoryService.logHistory(deviceDTO.getSerialNumber(), false, Action.TRANSFER, "Склад компании", "Реестр");
            }
        });
        if (!devices.isEmpty()) {
            deviceRepository.saveAll(devices);
        }
    }

    /*@Override
    public DeviceDetailDTO update(DeviceDTO deviceDTO) {
        Device device = deviceRepository.findById(deviceDTO.getId()).orElseThrow(validationService.entityNotFoundExceptionThrow(DEVICE));

        DeviceModel deviceModel = deviceModelRepository.findById(deviceDTO.getModelId()).orElseThrow(validationService.entityNotFoundExceptionThrow(DEVICE));

        device.setSerialNumber(deviceDTO.getSerialNumber());
        device.setModelId(deviceDTO.getModelId());
        device.setModel(deviceModel);
        device.setCompany(deviceModel.getCompany());
        device.setCompanyId(getCompanyId());
        save(device);

        evictCache(CacheConstants.DEVICE_BY_SERIAL_NUMBER, deviceDTO.getSerialNumber());
        return DeviceMapper.INSTANCE.toDetailDto(device);
    }*/


    /*@Override
    @Transactional(readOnly = true)
    public DeviceDetailDTO get(Long id) {
        Device device = deviceRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(DEVICE));
        return DeviceMapper.INSTANCE.toDetailDto(device);
    }*/

    /*@Override
    public void deleteById(Long id) {
        Device device = deviceRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(DEVICE));
        device.setDeleted(true);
        deviceRepository.save(device);
        evictCache(CacheConstants.DEVICE_BY_SERIAL_NUMBER, device.getSerialNumber());
        logHistory(device.getSerialNumber(), false, ActionEnum.DELETE, "Реестр", null);
    }*/

    @Override
    public Page<DeviceForTransferListDTO> getListForTransfer(CompanyDeviceFilter filter) {
        if (StringUtils.isNotEmpty(filter.getFromSerialNumber())) {
            Device device = deviceRepository.findBySerialNumber(filter.getFromSerialNumber().toUpperCase());
            if (device == null) {
                throw validationService.entityNotFoundExceptionThrow(DEVICE, SERIAL_NUMBER, filter.getFromSerialNumber().toUpperCase()).get();
            }
            filter.setDeviceId(device.getId());
        }
        return deviceRepository.getListForTransfer(filter);
    }

    @Override
    public Page<CompanyTerminalListDTO> listAll(CompanyBranchDeviceFilter filter) {
        return deviceRepository.listAll(filter);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RegistryListDTO> listRegister(CompanyDeviceListFilter filter) {
        return deviceRepository.listRegister(filter);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DeviceDetailDTO> list(BaseFilter filter) {
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            if (DeviceStatus.get(filter.getStatus()) == null) {
                throw validationService.badRequestExceptionThrow(INVALID_INPUT, filter.getStatus()).get();
            }
        }
        return deviceRepository.list(filter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Device> getSyncItems(SyncDeviceFilter syncDeviceFilter) {
        return deviceRepository.getSyncItems(syncDeviceFilter);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getDeviceTotalByModel(Long modelId) {
        return deviceRepository.getDeviceTotalByModel(modelId);
    }

    @Override
    @Transactional
    public void updateStatus(Device device, DeviceStatus status) {
        DeviceStatus oldStatus = device.getStatus();
        device.setStatus(status);
        deviceRepository.save(device);
        warehouseHistoryService.create(device.getSerialNumber(), device.getId(), EntityTypeEnum.DEVICE, status);
        cacheService.evict(CacheConstants.DEVICE_BY_SERIAL_NUMBER, device.getSerialNumber());
        logHistoryService.logHistory(device.getSerialNumber(), false, Action.UPDATE, null, null, oldStatus.getCode(), status.getCode());
    }

    @Override
    @Transactional
    public void updateStatus(Long id, String status, MultipartFile file) {
        Device device = deviceRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(DEVICE));
        String oldStatus = device.getStatus().getName();
        DeviceStatus statusEnum = DeviceStatus.get(status);
        if (statusEnum == null) {
            throw validationService.badRequestExceptionThrow(INVALID_INPUT, status).get();
        }
        fileUploadService.upload(file, id, EntityTypeEnum.DEVICE.name(), DocumentType.DEVICE.getCode());
        device.setStatus(statusEnum);
        deviceRepository.save(device);

        deviceSuspendHistoryOldService.suspend(id);
        cacheService.evict(CacheConstants.DEVICE_BY_SERIAL_NUMBER, device.getSerialNumber());
        logHistoryService.logHistory(device.getSerialNumber(), false, Action.UPDATE, null, null, oldStatus, statusEnum.getName());
    }


    @Override
    @Transactional(readOnly = true)
    public Page<SerialNumberDTO> lookUp(DeviceLookUpFilter filter) {
        if (!filter.isFromRegistry()) {
            CompanyDeviceFilter companyDeviceFilter = new CompanyDeviceFilter();
            if (SecurityUtils.getBranchId() == null || SecurityUtils.getBranchId() == 0) {
                User user = userRepository.findById(SecurityUtils.getCurrentUserId()).orElseThrow(validationService.entityNotFoundExceptionThrow(USER));
                companyDeviceFilter.setBranchId(user.getBranchId());
            } else {
                companyDeviceFilter.setBranchId(SecurityUtils.getBranchId());
            }
            companyDeviceFilter.setSearch(filter.getSearch());
            companyDeviceFilter.setModelId(filter.getModelId());
            companyDeviceFilter.setOrderBy(filter.getOrderBy());
            companyDeviceFilter.setPage(filter.getPage());
            companyDeviceFilter.setSortOrder(filter.getSortOrder());
            companyDeviceFilter.setSize(filter.getSize());
            return branchDeviceService.getListForTransfer2(companyDeviceFilter);
        }
        return deviceRepository.resultLookUp(filter);
    }

    @Override
    @Transactional
    public Long importDevices(MultipartFile file) {
        try {
            HashMap<String, DeviceModel> deviceModelMap = deviceModelRepository.findAll().stream().collect(Collectors.toMap(deviceModel -> deviceModel.getCode().toLowerCase(), deviceModel -> deviceModel, (a, b) -> b, HashMap::new));
            final DataFormatter formatter = new DataFormatter();
            InputStream inputStream = new ByteArrayInputStream(file.getBytes());
            Workbook wb = WorkbookFactory.create(inputStream);
            Sheet sheet = wb.getSheetAt(0);
            int counter = 0;
            boolean header = true;
            for (Row row : sheet) {
                if (header) {
                    header = false;
                    continue;
                }
                Cell cell0 = row.getCell(0);
                Cell cell1 = row.getCell(1);
                if (cell0 != null && cell1 != null) {
                    String serialNumber;
                    String model;
                    try {
                        serialNumber = substring(255, formatter.formatCellValue(cell0)).trim().toUpperCase();
                    } catch (Exception e) {
                        throw validationService.badRequestExceptionThrow(e.getMessage() + " [Row: " + cell0.getAddress().getRow() + " Column:" + cell0.getAddress().getColumn() + "]").get();
                    }
                    try {
                        model = substring(255, formatter.formatCellValue(cell1)).trim();
                    } catch (Exception e) {
                        throw validationService.badRequestExceptionThrow(e.getMessage() + " [Row: " + cell1.getAddress().getRow() + " Column:" + cell1.getAddress().getColumn() + "]").get();
                    }
                    if (StringUtils.isNoneEmpty(serialNumber, model)) {
                        DeviceModel deviceModel = deviceModelMap.get(model.toLowerCase());
                        if (deviceModel != null) {
                            try {
                                if (deviceRepository.findFirstBySerialNumber(serialNumber) == null) {
                                    Device device = new Device();
                                    device.setSerialNumber(serialNumber);
                                    device.setModelId(deviceModel.getId());
                                    //device.setCompanyId(deviceModel.getCompanyId());
                                    deviceRepository.save(device);
                                    /*create a device in contract report (ContractReport.class)*/
                                    rabbitMqContractReportProducer.createContractReport(new ContractReportPayload(device.getId(), EntityTypeEnum.DEVICE));
                                    cacheService.putCache(CacheConstants.DEVICE_BY_SERIAL_NUMBER, serialNumber, device);
                                    counter++;
                                    logHistoryService.logHistory(serialNumber, false, Action.TRANSFER, "Склад компании", "Реестр");
                                }
                            } catch (Exception e) {
                                log.error("Import terminals error: {}", e.getMessage());
                            }
                        }
                    }
                }
            }

            return (long) counter;
        } catch (IOException | EncryptedDocumentException e) {
            log.error(e.getMessage());
            throw validationService.badRequestExceptionThrow(INVALID_ACTION, e.getMessage()).get();
        }
    }

    private String substring(int count, String field) {
        String subString = field;
        if (StringUtils.isNotEmpty(field) && field.length() > count) {
            subString = field.substring(0, count);
        }
        return subString;
    }


    @Transactional(readOnly = true)
    public Long getListCountToSync() {
        return deviceRepository.count();
    }

    @Transactional(readOnly = true)
    public Page<SyncDeviceListDTO> getListToSync(BaseFilter filter) {
        return new PageImpl<>
                (deviceRepository.getListToSync(filter),
                        filter.getOrderedPageable(), 0L);
    }
}
