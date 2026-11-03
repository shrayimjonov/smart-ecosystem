package uz.uzkassa.cts.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.branchdevice.BranchDeviceListAdminDTO;
import uz.uzkassa.cts.dto.device.*;
import uz.uzkassa.cts.dto.device.admin.DeviceDetailAdminDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.dto.sync.SyncBranchDeviceListDTO;
import uz.uzkassa.cts.enums.Action;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.*;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.BranchDeviceService;
import uz.uzkassa.cts.service.DeviceService;
import uz.uzkassa.cts.service.LogHistoryService;
import uz.uzkassa.cts.service.WarehouseHistoryService;

import java.util.List;

@Transactional
@Service
public class BranchDeviceServiceImpl extends BaseServiceImpl<BranchDevice, Long> implements BranchDeviceService {

    private final BranchDeviceRepository branchDeviceRepository;
    private final CustomerDeviceRepository customerDeviceRepository;
    private final FiscalizationRepository fiscalizationRepository;
    private final BranchRepository branchRepository;
    private final CompanyDeviceRepository companyDeviceRepository;
    private final WarehouseHistoryService warehouseHistoryService;
    private final DeviceRepository deviceRepository;
    private final DeviceService deviceService;
    private final LogHistoryService logHistoryService;

    public BranchDeviceServiceImpl(BranchDeviceRepository branchDeviceRepository,
                                   CustomerDeviceRepository customerDeviceRepository, FiscalizationRepository fiscalizationRepository, BranchRepository branchRepository,
                                   CompanyDeviceRepository companyDeviceRepository,
                                   WarehouseHistoryService warehouseHistoryService,
                                   DeviceRepository deviceRepository,
                                   @Lazy DeviceService deviceService, LogHistoryService logHistoryService) {
        super(branchDeviceRepository);
        this.branchDeviceRepository = branchDeviceRepository;
        this.customerDeviceRepository = customerDeviceRepository;
        this.fiscalizationRepository = fiscalizationRepository;
        this.branchRepository = branchRepository;
        this.companyDeviceRepository = companyDeviceRepository;
        this.warehouseHistoryService = warehouseHistoryService;
        this.deviceRepository = deviceRepository;
        this.deviceService = deviceService;
        this.logHistoryService = logHistoryService;
    }

    @Override
    public void add(BranchDeviceDTO branchDeviceDTO) {
        Branch branch = branchRepository.findById(branchDeviceDTO.getBranchId()).orElseThrow(validationService.entityNotFoundExceptionThrow(BRANCH));

        branchDeviceDTO.getDevices().forEach(deviceDTO -> {

            Device device = deviceRepository.findByModelIdAndSerialNumber(deviceDTO.getModelId(), deviceDTO.getSerialNumber());
            if (device == null) {
                throw validationService.entityNotFoundExceptionThrow(DEVICE).get();
            }

            if (branchDeviceRepository.totalByDeviceId(device.getId()).intValue() > 0) {
                throw validationService.entityAlreadyExistExceptionThrow(DEVICE, SERIAL_NUMBER, deviceDTO.getSerialNumber()).get();
            }
            if (customerDeviceRepository.totalByDeviceId(device.getId()).intValue() > 0) {
                throw validationService.entityAlreadyExistExceptionThrow(DEVICE, SERIAL_NUMBER, device.getSerialNumber()).get();
            }
            if (fiscalizationRepository.totalByDeviceId(device.getId()).intValue() > 0) {
                throw validationService.entityAlreadyExistExceptionThrow(FISCALIZATION, SERIAL_NUMBER, device.getSerialNumber()).get();
            }
            CompanyDevice companyDevice = companyDeviceRepository.findFirstByCompanyIdAndDeviceId(branch.getCompanyId(), device.getId());
            if (companyDevice == null) {
                throw validationService.entityNotFoundExceptionThrow(DEVICE).get();
            }
            //Update company device status
            companyDevice.setStatus(DeviceStatus.TRANSFERRED);
            companyDeviceRepository.save(companyDevice);
            warehouseHistoryService.create(deviceDTO.getSerialNumber(), companyDevice.getId(), EntityTypeEnum.COMPANY_DEVICE, DeviceStatus.TRANSFERRED);

            //Register a device to branch
            BranchDevice branchDevice = branchDeviceRepository.findFirstByBranchIdAndDeviceId(branch.getId(), device.getId());
            if (branchDevice == null) {
                branchDevice = new BranchDevice();
            }
            branchDevice.setBranchId(branch.getId());
            branchDevice.setDeviceId(device.getId());
            branchDevice.setStatus(DeviceStatus.REGISTERED);
            branchDeviceRepository.save(branchDevice);
            warehouseHistoryService.create(deviceDTO.getSerialNumber(), branchDevice.getId(), EntityTypeEnum.BRANCH_DEVICE, DeviceStatus.REGISTERED);

            logHistoryService.logHistory(device.getSerialNumber(), false, Action.TRANSFER, companyDevice.getCompany().getName(), branch.getName());
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SerialNumberDTO> lookUp(BaseFilter filter) {

        ResultList<SerialNumberDTO> resultList = branchDeviceRepository.getLookUp(filter);
        return new PageImpl<>
                (resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BaseDeviceFiscalModuleListDTO> list(BaseFilter filter) {
        Long branchId = SecurityUtils.getBranchId();
        if (branchId == null || branchId == 0) {
            throw validationService.entityNotFoundExceptionThrow(BRANCH).get();
        }

        ResultList<BaseDeviceFiscalModuleListDTO> resultList = branchDeviceRepository.getList(filter);
        return new PageImpl<>
                (resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }

    public Page<DeviceForTransferListDTO> getListForTransfer(CompanyDeviceFilter filter) {
        if (StringUtils.isNotEmpty(filter.getFromSerialNumber())) {
            Device device = deviceRepository.findBySerialNumber(filter.getFromSerialNumber().toUpperCase());
            if (device == null) {
                throw validationService.entityNotFoundExceptionThrow(DEVICE, SERIAL_NUMBER, filter.getFromSerialNumber().toUpperCase()).get();
            }
            filter.setDeviceId(device.getId());
        }
        ResultList<DeviceForTransferListDTO> resultList = branchDeviceRepository.getListForTransfer(filter);
        return new PageImpl<>
                (resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }

    public Page<SerialNumberDTO> getListForTransfer2(CompanyDeviceFilter filter) {
        List<SerialNumberDTO> result = branchDeviceRepository.getListForTransfer2(filter);
        return new PageImpl<>(result, filter.getOrderedPageable(), result.size());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchDevice> freeDevices(Long id, List<String> serialNumbers) {
        return branchDeviceRepository.freeDevices(id, serialNumbers);
    }

    @Override
    public void deleteByDeviceId(Long deviceId) {
        List<BranchDevice> branchDevices = branchDeviceRepository.findAllByDeviceId(deviceId);
        if (CollectionUtils.isEmpty(branchDevices)) {
            throw validationService.entityNotFoundExceptionThrow(DEVICE).get();
        }

        branchDevices.forEach(branchDevice -> {
            branchDevice.setDeleted(true);
            branchDevice.setStatus(DeviceStatus.DELETED);
            branchDeviceRepository.save(branchDevice);
            warehouseHistoryService.create(branchDevice.getDevice().getSerialNumber(), branchDevice.getId(), EntityTypeEnum.BRANCH_DEVICE, DeviceStatus.DELETED);

            logHistoryService.logHistory(branchDevice.getDevice().getSerialNumber(), false, Action.DELETE, branchDevice.getBranch().getName(), null);
        });
    }

    @Override
    public Page<CompanyTerminalListDTO> listAll(CompanyBranchDeviceFilter filter) {
        ResultList<CompanyTerminalListDTO> resultList = branchDeviceRepository.getListAll(filter);
        return new PageImpl<>
                (resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public Page<BaseDeviceFiscalModuleListDTO> listByEmployee(BaseFilter filter) {
        ResultList<BaseDeviceFiscalModuleListDTO> resultList = branchDeviceRepository.getListByEmployee(filter);
        return new PageImpl<>
                (resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }

    @Transactional(readOnly = true)
    public Long getListCountToSync() {
        if (!isSuperAdmin() && getCompanyId() != null)
            return branchDeviceRepository.countByCompanyId(getCompanyId());
        else
            return branchDeviceRepository.count();
    }

    @Transactional(readOnly = true)
    public Page<SyncBranchDeviceListDTO> getListToSync(BaseFilter filter) {

        return new PageImpl<>
            (branchDeviceRepository.getListToSync(filter), filter.getOrderedPageable(), 0L);
    }

    @Override
    public DeviceDetailAdminDTO getForAdmin(Long id) {
        return branchDeviceRepository.findById(id).map(branchDevice -> deviceService.getForAdmin(branchDevice.getDeviceId())).orElseThrow(validationService.entityNotFoundExceptionThrow(DEVICE));
    }

    @Override
    public Page<BranchDeviceListAdminDTO> getListForAdmin(BaseFilter filter) {
        ResultList<BranchDevice> resultList = branchDeviceRepository.getResultList(filter);
        List<BranchDeviceListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(branchDevice -> {
                    BranchDeviceListAdminDTO branchDeviceListDTO = new BranchDeviceListAdminDTO();
                    branchDeviceListDTO.setId(branchDevice.getId());
                    branchDeviceListDTO.setDevice(branchDevice.getDevice().toSerialNumberDTO());
                    branchDeviceListDTO.setStatus(branchDevice.getStatus());
                    branchDeviceListDTO.setCompany(branchDevice.getBranch().getCompany().toShortDTO());
                    branchDeviceListDTO.setBranch(branchDevice.getBranch().toCommonDTO());
                    branchDeviceListDTO.setCreatedDate(branchDevice.getCreatedDate());
                    branchDeviceListDTO.setCreator(branchDevice.getCreator().toCommonDTO());
                    return branchDeviceListDTO;
                }).toList();
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public List<DeviceStatisticsAdminDTO> getStatisticsForAdmin(BaseFilter filter) {
        return branchDeviceRepository.getStatistics(filter);
    }
}
