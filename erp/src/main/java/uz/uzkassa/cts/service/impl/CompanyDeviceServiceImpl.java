package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.companydevice.admin.CompanyDeviceListAdminDTO;
import uz.uzkassa.cts.dto.device.CompanyDeviceListNewDTO;
import uz.uzkassa.cts.dto.device.CompanyTerminalListDTO;
import uz.uzkassa.cts.dto.device.DeviceForTransferListDTO;
import uz.uzkassa.cts.dto.device.DeviceTransferDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceDetailAdminDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.enums.Action;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.filter.SyncDeviceFilter;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.CompanyBranchDeviceFilter;
import uz.uzkassa.cts.filter.CompanyDeviceFilter;
import uz.uzkassa.cts.filter.CompanyDeviceListFilter;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.service.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Transactional
@Service
@Slf4j
public class CompanyDeviceServiceImpl extends BaseServiceImpl<CompanyDevice, Long> implements CompanyDeviceService {

    private final CompanyDeviceRepository companyDeviceRepository;
    private final CompanyRepository companyRepository;
    private final BranchRepository branchRepository;
    private final BranchDeviceRepository branchDeviceRepository;
    private final DeviceRepository deviceRepository;
    private final BranchDeviceService branchDeviceService;
    private final DeviceService deviceService;
    private final WarehouseHistoryService warehouseHistoryService;
    private final LogHistoryService logHistoryService;

    public CompanyDeviceServiceImpl(CompanyDeviceRepository companyDeviceRepository,
                                    CompanyRepository companyRepository,
                                    BranchRepository branchRepository,
                                    BranchDeviceRepository branchDeviceRepository,
                                    DeviceRepository deviceRepository,
                                    BranchDeviceService branchDeviceService,
                                    DeviceService deviceService,
                                    WarehouseHistoryService warehouseHistoryService, LogHistoryService logHistoryService) {
        super(companyDeviceRepository);
        this.companyDeviceRepository = companyDeviceRepository;
        this.companyRepository = companyRepository;
        this.branchRepository = branchRepository;
        this.branchDeviceRepository = branchDeviceRepository;
        this.deviceRepository = deviceRepository;
        this.branchDeviceService = branchDeviceService;
        this.deviceService = deviceService;
        this.warehouseHistoryService = warehouseHistoryService;
        this.logHistoryService = logHistoryService;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DeviceForTransferListDTO> listForTransfer(CompanyDeviceFilter filter) {
        if (filter.getBranchId() != null) {
            return branchDeviceService.getListForTransfer(filter);
        }
        if (filter.getCompanyId() != null) {
            return getListForTransfer(filter);
        }
        return deviceService.getListForTransfer(filter);
    }

    @Override
    public void sync(SyncDeviceFilter syncDeviceFilter) {
        List<Device> deviceList = deviceService.getSyncItems(syncDeviceFilter);
        if (deviceList != null && !deviceList.isEmpty()) {
            for (Device device : deviceList) {

                if (companyDeviceRepository.totalByDeviceId(syncDeviceFilter.getCompanyId(), device.getId()).intValue() > 0) {
                    continue;
                }

                CompanyDevice companyDevice = new CompanyDevice();
                companyDevice.setCompanyId(syncDeviceFilter.getCompanyId());
                companyDevice.setDeviceId(device.getId());
                companyDeviceRepository.save(companyDevice);
                warehouseHistoryService.create(device.getSerialNumber(), companyDevice.getId(), EntityTypeEnum.COMPANY_DEVICE, DeviceStatus.REGISTERED);

                deviceService.updateStatus(device, DeviceStatus.REGISTERED);

                logHistoryService.logHistory(device.getSerialNumber(), false, Action.TRANSFER, "Реестр", companyDevice.getCompany().getName());
            }
        }
    }

    private Page<DeviceForTransferListDTO> getListForTransfer(CompanyDeviceFilter filter) {
        if (StringUtils.isNotEmpty(filter.getFromSerialNumber())) {
            Device device = deviceRepository.findBySerialNumber(filter.getFromSerialNumber().toUpperCase());
            if (device == null) {
                throw validationService.entityNotFoundExceptionThrow(DEVICE, SERIAL_NUMBER, filter.getFromSerialNumber().toUpperCase()).get();
            }
            filter.setDeviceId(device.getId());
        }
        ResultList<DeviceForTransferListDTO> resultList = companyDeviceRepository.getListForTransfer(filter);
        return new PageImpl<>(resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }


    @Override
    public void transfer(DeviceTransferDTO deviceTransferDTO) {

        //Transfer from registry to branch
        if (deviceTransferDTO.getToBranchId() != null && deviceTransferDTO.getFromCompanyId() == null && deviceTransferDTO.getFromBranchId() == null) {

            Branch toBranch = branchRepository.findById(deviceTransferDTO.getToBranchId()).orElseThrow(validationService.entityNotFoundExceptionThrow(BRANCH));

            for (Long deviceId : deviceTransferDTO.getDevices()) {

                Device device = deviceRepository.findById(deviceId).orElseThrow(validationService.entityNotFoundExceptionThrow(DEVICE));

                CompanyDevice toCompanyDevice = companyDeviceRepository.findFirstByCompanyIdAndDeviceId(toBranch.getCompanyId(), deviceId);
                if (toCompanyDevice == null) {
                    toCompanyDevice = new CompanyDevice();
                }

                BranchDevice toBranchDevice = branchDeviceRepository.findFirstByBranchIdAndDeviceId(deviceTransferDTO.getToBranchId(), deviceId);
                if (toBranchDevice == null) {
                    toBranchDevice = new BranchDevice();
                }

                //Register to company first
                toCompanyDevice.setDeviceId(deviceId);
                toCompanyDevice.setDevice(device);
                toCompanyDevice.setCompanyId(toBranch.getCompanyId());
                toCompanyDevice.setCompany(toBranch.getCompany());
                toCompanyDevice.setStatus(DeviceStatus.REGISTERED);
                companyDeviceRepository.save(toCompanyDevice);
                warehouseHistoryService.create(device.getSerialNumber(), toCompanyDevice.getId(), EntityTypeEnum.COMPANY_DEVICE, DeviceStatus.REGISTERED);

                toCompanyDevice.setStatus(DeviceStatus.TRANSFERRED);
                warehouseHistoryService.create(device.getSerialNumber(), toCompanyDevice.getId(), EntityTypeEnum.COMPANY_DEVICE, DeviceStatus.TRANSFERRED);
                companyDeviceRepository.save(toCompanyDevice);

                //from registry to company
                logHistoryService.logHistory(device.getSerialNumber(), false, Action.TRANSFER, "Реестр", toCompanyDevice.getCompany().getName());

                //Register to branch
                toBranchDevice.setBranchId(toBranch.getId());
                toBranchDevice.setBranch(toBranch);
                toBranchDevice.setDeviceId(deviceId);
                toBranchDevice.setDevice(device);
                toBranchDevice.setStatus(DeviceStatus.REGISTERED);
                branchDeviceRepository.save(toBranchDevice);
                warehouseHistoryService.create(device.getSerialNumber(), toBranchDevice.getId(), EntityTypeEnum.BRANCH_DEVICE, DeviceStatus.REGISTERED);

                //Update device status on registry
                deviceService.updateStatus(device, DeviceStatus.REGISTERED);

                //from company to branch
                logHistoryService.logHistory(device.getSerialNumber(), false, Action.TRANSFER, toCompanyDevice.getCompany().getName(), toBranch.getName());
            }

            return;
        }

        //Transfer from branch to registry
        if (deviceTransferDTO.getFromBranchId() != null && deviceTransferDTO.getToBranchId() == null && deviceTransferDTO.getToCompanyId() == null) {

            Branch fromBranch = branchRepository.findById(deviceTransferDTO.getFromBranchId()).orElseThrow(validationService.entityNotFoundExceptionThrow(BRANCH));

            for (Long deviceId : deviceTransferDTO.getDevices()) {

                Device device = deviceRepository.findById(deviceId).orElseThrow(validationService.entityNotFoundExceptionThrow(DEVICE));

                BranchDevice fromBranchDevice = branchDeviceRepository.findFirstByBranchIdAndDeviceId(deviceTransferDTO.getFromBranchId(), deviceId);
                if (fromBranchDevice == null) {
                    continue;
                }

                //Transfer a device from branch
                fromBranchDevice.setStatus(DeviceStatus.TRANSFERRED);
                warehouseHistoryService.create(device.getSerialNumber(), fromBranch.getId(), EntityTypeEnum.BRANCH_DEVICE, DeviceStatus.TRANSFERRED);
                branchDeviceRepository.save(fromBranchDevice);

                //Update device status on registry
                deviceService.updateStatus(device, DeviceStatus.UNREGISTERED);

                //from branch
                logHistoryService.logHistory(device.getSerialNumber(), false, Action.TRANSFER, fromBranch.getName(), "Реестр");
            }

            return;
        }


        //Transfer from registry to company
        if (deviceTransferDTO.getToCompanyId() != null && deviceTransferDTO.getFromCompanyId() == null && deviceTransferDTO.getFromBranchId() == null) {

            Company toCompany = companyRepository.findById(deviceTransferDTO.getToCompanyId()).orElseThrow(validationService.entityNotFoundExceptionThrow(COMPANY));

            for (Long deviceId : deviceTransferDTO.getDevices()) {

                Device device = deviceRepository.findById(deviceId).orElseThrow(validationService.entityNotFoundExceptionThrow(DEVICE));

                CompanyDevice toCompanyDevice = companyDeviceRepository.findFirstByCompanyIdAndDeviceId(toCompany.getId(), deviceId);
                if (toCompanyDevice == null) {
                    toCompanyDevice = new CompanyDevice();
                }

                toCompanyDevice.setCompanyId(toCompany.getId());
                toCompanyDevice.setCompany(toCompany);
                toCompanyDevice.setDeviceId(deviceId);
                toCompanyDevice.setDevice(device);
                toCompanyDevice.setStatus(DeviceStatus.REGISTERED);
                companyDeviceRepository.save(toCompanyDevice);
                warehouseHistoryService.create(device.getSerialNumber(), toCompanyDevice.getId(), EntityTypeEnum.COMPANY_DEVICE, DeviceStatus.REGISTERED);

                //Update device status on registry
                deviceService.updateStatus(device, DeviceStatus.REGISTERED);

                //from registry to company
                logHistoryService.logHistory(device.getSerialNumber(), false, Action.TRANSFER, "Реестр", toCompanyDevice.getCompany().getName());
            }

            return;
        }

        //Transfer from company to registry
        if (deviceTransferDTO.getFromCompanyId() != null && deviceTransferDTO.getToCompanyId() == null && deviceTransferDTO.getToBranchId() == null) {

            Company fromCompany = companyRepository.findById(deviceTransferDTO.getFromCompanyId()).orElseThrow(validationService.entityNotFoundExceptionThrow(COMPANY));

            for (Long deviceId : deviceTransferDTO.getDevices()) {

                Device device = deviceRepository.findById(deviceId).orElseThrow(validationService.entityNotFoundExceptionThrow(DEVICE));

                CompanyDevice fromCompanyDevice = companyDeviceRepository.findFirstByCompanyIdAndDeviceId(fromCompany.getId(), deviceId);
                if (fromCompanyDevice == null) {
                    continue;
                }

                fromCompanyDevice.setCompanyId(fromCompany.getId());
                fromCompanyDevice.setCompany(fromCompany);
                fromCompanyDevice.setDeviceId(deviceId);
                fromCompanyDevice.setDevice(device);
                fromCompanyDevice.setStatus(DeviceStatus.TRANSFERRED);
                companyDeviceRepository.save(fromCompanyDevice);
                warehouseHistoryService.create(device.getSerialNumber(), fromCompany.getId(), EntityTypeEnum.COMPANY_DEVICE, DeviceStatus.TRANSFERRED);

                //Update device status on registry
                deviceService.updateStatus(device, DeviceStatus.UNREGISTERED);

                logHistoryService.logHistory(device.getSerialNumber(), false, Action.TRANSFER, fromCompany.getName(), "Реестр");

            }

            return;
        }

        //Transfer from one company to another company
        if (deviceTransferDTO.getFromBranchId() == null && deviceTransferDTO.getToBranchId() == null && !Objects.equals(deviceTransferDTO.getFromCompanyId(), deviceTransferDTO.getToCompanyId())) {

            Company fromCompany = companyRepository.findById(deviceTransferDTO.getFromCompanyId()).orElseThrow(validationService.entityNotFoundExceptionThrow(COMPANY));
            Company toCompany = companyRepository.findById(deviceTransferDTO.getToCompanyId()).orElseThrow(validationService.entityNotFoundExceptionThrow(COMPANY));

            for (Long deviceId : deviceTransferDTO.getDevices()) {

                Device device = deviceRepository.findById(deviceId).orElseThrow(validationService.entityNotFoundExceptionThrow(DEVICE));

                CompanyDevice companyDevice = companyDeviceRepository.findFirstByCompanyIdAndDeviceId(fromCompany.getId(), device.getId());
                if (companyDevice == null) {
                    throw validationService.entityNotFoundExceptionThrow(DEVICE).get();
                }

                companyDevice.setStatus(DeviceStatus.TRANSFERRED);
                companyDeviceRepository.save(companyDevice);
                warehouseHistoryService.create(device.getSerialNumber(), companyDevice.getId(), EntityTypeEnum.COMPANY_DEVICE, DeviceStatus.TRANSFERRED);

                companyDevice.setCompanyId(toCompany.getId());
                companyDevice.setCompany(toCompany);
                companyDevice.setDeviceId(device.getId());
                companyDevice.setDevice(device);
                companyDevice.setStatus(DeviceStatus.REGISTERED);
                companyDeviceRepository.save(companyDevice);
                warehouseHistoryService.create(device.getSerialNumber(), companyDevice.getId(), EntityTypeEnum.COMPANY_DEVICE, DeviceStatus.REGISTERED);

                logHistoryService.logHistory(device.getSerialNumber(), false, Action.TRANSFER, fromCompany.getName(), toCompany.getName());

            }

            return;
        }


        Branch toBranch = deviceTransferDTO.getToBranchId() != null ? branchRepository.findById(deviceTransferDTO.getToBranchId()).orElseThrow(validationService.entityNotFoundExceptionThrow(BRANCH)) : null;
        Branch fromBranch = deviceTransferDTO.getFromBranchId() != null ? branchRepository.findById(deviceTransferDTO.getFromBranchId()).orElseThrow(validationService.entityNotFoundExceptionThrow(BRANCH)) : null;


        //Transfer from one branch to another branch
        if (fromBranch != null && toBranch != null) {
            if (fromBranch.getId().equals(toBranch.getId())) {
                return;
            }
            for (Long deviceId : deviceTransferDTO.getDevices()) {

                Device device = deviceRepository.findById(deviceId).orElseThrow(validationService.entityNotFoundExceptionThrow(DEVICE));

                BranchDevice branchDevice = branchDeviceRepository.findFirstByBranchIdAndDeviceId(fromBranch.getId(), device.getId());
                if (branchDevice == null) {
                    throw validationService.entityNotFoundExceptionThrow(DEVICE).get();
                }


                //Transfer
                branchDevice.setStatus(DeviceStatus.TRANSFERRED);
                branchDeviceRepository.save(branchDevice);
                warehouseHistoryService.create(device.getSerialNumber(), branchDevice.getId(), EntityTypeEnum.BRANCH_DEVICE, DeviceStatus.TRANSFERRED);

                //Register
                branchDevice.setBranchId(toBranch.getId());
                branchDevice.setBranch(toBranch);
                branchDevice.setDeviceId(device.getId());
                branchDevice.setDevice(device);
                branchDevice.setStatus(DeviceStatus.REGISTERED);
                branchDeviceRepository.save(branchDevice);
                warehouseHistoryService.create(device.getSerialNumber(), branchDevice.getId(), EntityTypeEnum.BRANCH_DEVICE, DeviceStatus.REGISTERED);

                logHistoryService.logHistory(device.getSerialNumber(), false, Action.TRANSFER, fromBranch.getName(), toBranch.getName());

            }

            return;
        }

        //From company to branch
        if (fromBranch == null && toBranch != null) {
            for (Long deviceId : deviceTransferDTO.getDevices()) {

                Device device = deviceRepository.findById(deviceId).orElseThrow(validationService.entityNotFoundExceptionThrow(DEVICE));

                CompanyDevice fromCompanyDevice = companyDeviceRepository.findFirstByCompanyIdAndDeviceId(deviceTransferDTO.getFromCompanyId(), deviceId);
                if (fromCompanyDevice == null) {
                    continue;
                }

                BranchDevice toBranchDevice = branchDeviceRepository.findFirstByBranchIdAndDeviceId(toBranch.getId(), device.getId());
                if (toBranchDevice == null) {
                    toBranchDevice = new BranchDevice();
                }

                fromCompanyDevice.setStatus(DeviceStatus.TRANSFERRED);
                companyDeviceRepository.save(fromCompanyDevice);
                warehouseHistoryService.create(device.getSerialNumber(), fromCompanyDevice.getId(), EntityTypeEnum.COMPANY_DEVICE, DeviceStatus.TRANSFERRED);

                toBranchDevice.setBranchId(toBranch.getId());
                toBranchDevice.setBranch(toBranch);
                toBranchDevice.setDeviceId(device.getId());
                toBranchDevice.setDevice(device);
                toBranchDevice.setStatus(DeviceStatus.REGISTERED);
                branchDeviceRepository.save(toBranchDevice);
                warehouseHistoryService.create(device.getSerialNumber(), toBranchDevice.getId(), EntityTypeEnum.BRANCH_DEVICE, DeviceStatus.REGISTERED);

                logHistoryService.logHistory(device.getSerialNumber(), false, Action.TRANSFER, fromCompanyDevice.getCompany().getName(), toBranch.getName());

            }
            return;
        }

        //From branch to company
        if (fromBranch != null) {

            Company toCompany = companyRepository.findById(deviceTransferDTO.getToCompanyId()).orElseThrow(validationService.entityNotFoundExceptionThrow(COMPANY));

            for (Long deviceId : deviceTransferDTO.getDevices()) {

                Device device = deviceRepository.findById(deviceId).orElseThrow(validationService.entityNotFoundExceptionThrow(DEVICE));

                BranchDevice fromBranchDevice = branchDeviceRepository.findFirstByBranchIdAndDeviceId(fromBranch.getId(), device.getId());
                if (fromBranchDevice == null) {
                    continue;
                }

                CompanyDevice toCompanyDevice = companyDeviceRepository.findFirstByCompanyIdAndDeviceId(deviceTransferDTO.getToCompanyId(), device.getId());
                if (toCompanyDevice == null) {
                    toCompanyDevice = new CompanyDevice();
                }

                //Transfer
                fromBranchDevice.setStatus(DeviceStatus.TRANSFERRED);
                branchDeviceRepository.save(fromBranchDevice);
                warehouseHistoryService.create(device.getSerialNumber(), fromBranchDevice.getId(), EntityTypeEnum.BRANCH_DEVICE, DeviceStatus.TRANSFERRED);

                //Register
                toCompanyDevice.setCompanyId(toCompany.getId());
                toCompanyDevice.setCompany(toCompany);
                toCompanyDevice.setDeviceId(deviceId);
                toCompanyDevice.setDevice(device);
                toCompanyDevice.setStatus(DeviceStatus.REGISTERED);
                companyDeviceRepository.save(toCompanyDevice);
                warehouseHistoryService.create(device.getSerialNumber(), toCompanyDevice.getId(), EntityTypeEnum.COMPANY_DEVICE, DeviceStatus.REGISTERED);

                logHistoryService.logHistory(device.getSerialNumber(), false, Action.TRANSFER, fromBranch.getName(), toCompanyDevice.getCompany().getName());
            }
        }

    }

    @Override
    @Transactional(readOnly = true)
    public Page<CompanyDeviceListNewDTO> list(CompanyDeviceListFilter filter) {
        ResultList<CompanyDeviceListNewDTO> resultList = companyDeviceRepository.getList(filter);
        return new PageImpl<>(resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public void deleteByDeviceId(Long deviceId) {
        List<CompanyDevice> companyDevices = companyDeviceRepository.findAllByDeviceId(deviceId);
        if (CollectionUtils.isEmpty(companyDevices)) {
            throw validationService.entityNotFoundExceptionThrow(DEVICE).get();
        }

        companyDevices.forEach(companyDevice -> {
            companyDevice.setDeleted(true);
            companyDevice.setStatus(DeviceStatus.DELETED);
            companyDeviceRepository.save(companyDevice);
            warehouseHistoryService.create(companyDevice.getDevice().getSerialNumber(), companyDevice.getId(), EntityTypeEnum.COMPANY_DEVICE, DeviceStatus.DELETED);

            logHistoryService.logHistory(companyDevice.getDevice().getSerialNumber(), false, Action.DELETE, companyDevice.getCompany().getName(), null);
        });
    }

    @Override
    public Page<CompanyTerminalListDTO> listAll(CompanyBranchDeviceFilter filter) {
        if (filter.getCompanyId() == null) {
            return deviceService.listAll(filter);
        }
        if (filter.getBranchId() != null) {
            return branchDeviceService.listAll(filter);
        }
        ResultList<CompanyTerminalListDTO> resultList = companyDeviceRepository.getListAll(filter);
        return new PageImpl<>(resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public Page<CompanyDeviceListAdminDTO> getListForAdmin(BaseFilter filter) {
        ResultList<CompanyDevice> resultList = companyDeviceRepository.getResultList(filter);
        List<CompanyDeviceListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(companyDevice -> {
                    CompanyDeviceListAdminDTO companyDeviceListDTO = new CompanyDeviceListAdminDTO();
                    companyDeviceListDTO.setId(companyDevice.getId());
                    companyDeviceListDTO.setDevice(companyDevice.getDevice().toSerialNumberDTO());
                    companyDeviceListDTO.setStatus(companyDevice.getStatus());
                    companyDeviceListDTO.setCompany(companyDevice.getCompany().toShortDTO());
                    companyDeviceListDTO.setCreatedDate(companyDevice.getCreatedDate());
                    companyDeviceListDTO.setCreator(companyDevice.getCreator().toCommonDTO());
                    return companyDeviceListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public DeviceDetailAdminDTO getForAdmin(Long id) {
        return companyDeviceRepository.findById(id).map(companyDevice -> deviceService.getForAdmin(companyDevice.getDeviceId())).orElseThrow(validationService.entityNotFoundExceptionThrow(DEVICE));
    }

    @Override
    public List<DeviceStatisticsAdminDTO> getStatisticsForAdmin(BaseFilter filter) {
        return companyDeviceRepository.getStatistics(filter);
    }
}
