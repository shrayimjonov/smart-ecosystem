package uz.uzkassa.cts.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.device.BaseDeviceFiscalModuleListDTO;
import uz.uzkassa.cts.dto.device.CompanyTerminalListDTO;
import uz.uzkassa.cts.dto.device.DeviceTransferDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.dto.fm.FiscalModuleForTransferListDTO;
import uz.uzkassa.cts.dto.fm.admin.CompanyFiscalModuleListAdminDTO;
import uz.uzkassa.cts.dto.fm.admin.FiscalModuleDetailAdminDTO;
import uz.uzkassa.cts.enums.Action;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.CompanyBranchDeviceFilter;
import uz.uzkassa.cts.filter.CompanyDeviceFilter;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.service.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Transactional
@Service
public class CompanyFiscalModuleServiceImpl extends BaseServiceImpl<CompanyFiscalModule, Long> implements CompanyFiscalModuleService {

    private final CompanyFiscalModuleRepository companyFiscalModuleRepository;
    private final BranchRepository branchRepository;
    private final WarehouseHistoryService warehouseHistoryService;
    private final FiscalModuleRepository fiscalModuleRepository;
    private final BranchFiscalModuleRepository branchFiscalModuleRepository;
    private final FiscalModuleService fiscalModuleService;
    private final BranchFiscalModuleService branchFiscalModuleService;
    private final CompanyRepository companyRepository;
    private final LogHistoryService logHistoryService;


    public CompanyFiscalModuleServiceImpl(CompanyFiscalModuleRepository companyFiscalModuleRepository,
                                          BranchRepository branchRepository,
                                          WarehouseHistoryService warehouseHistoryService,
                                          FiscalModuleRepository fiscalModuleRepository,
                                          BranchFiscalModuleRepository branchFiscalModuleRepository,
                                          FiscalModuleService fiscalModuleService,
                                          BranchFiscalModuleService branchFiscalModuleService,
                                          CompanyRepository companyRepository, LogHistoryService logHistoryService) {
        super(companyFiscalModuleRepository);
        this.companyFiscalModuleRepository = companyFiscalModuleRepository;
        this.branchRepository = branchRepository;
        this.warehouseHistoryService = warehouseHistoryService;
        this.fiscalModuleRepository = fiscalModuleRepository;
        this.branchFiscalModuleRepository = branchFiscalModuleRepository;
        this.fiscalModuleService = fiscalModuleService;
        this.branchFiscalModuleService = branchFiscalModuleService;
        this.companyRepository = companyRepository;
        this.logHistoryService = logHistoryService;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BaseDeviceFiscalModuleListDTO> list(BaseFilter filter) {
        ResultList<BaseDeviceFiscalModuleListDTO> resultList = companyFiscalModuleRepository.getList(filter);
        return new PageImpl<>(resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public Page<CompanyTerminalListDTO> listAll(CompanyBranchDeviceFilter filter) {
        if (filter.getCompanyId() == null) {
            return fiscalModuleService.listAll(filter);
        }
        if (filter.getBranchId() != null) {
            return branchFiscalModuleService.listAll(filter);
        }

        ResultList<CompanyTerminalListDTO> resultList = companyFiscalModuleRepository.getListAll(filter);
        return new PageImpl<>(resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }


    @Override
    public void transfer(DeviceTransferDTO deviceTransferDTO) {

        //Transfer from registry to branch
        if (deviceTransferDTO.getToBranchId() != null && deviceTransferDTO.getFromCompanyId() == null && deviceTransferDTO.getFromBranchId() == null) {

            Branch toBranch = branchRepository.findById(deviceTransferDTO.getToBranchId()).orElseThrow(validationService.entityNotFoundExceptionThrow(BRANCH));

            for (Long fiscalModuleId : deviceTransferDTO.getDevices()) {

                FiscalModule fiscalModule = fiscalModuleRepository.findById(fiscalModuleId).orElseThrow(validationService.entityNotFoundExceptionThrow(FISCAL_MODULE));

                CompanyFiscalModule companyFiscalModule = companyFiscalModuleRepository.findFirstByCompanyIdAndFiscalModuleId(toBranch.getCompanyId(), fiscalModuleId);
                if (companyFiscalModule == null) {
                    companyFiscalModule = new CompanyFiscalModule();
                }

                BranchFiscalModule branchFiscalModule = branchFiscalModuleRepository.findFirstByBranchIdAndFiscalModuleIdAndStatus(deviceTransferDTO.getToBranchId(), fiscalModuleId, DeviceStatus.REGISTERED);
                if (branchFiscalModule == null) {
                    branchFiscalModule = new BranchFiscalModule();
                }

                //Register to company first
                companyFiscalModule.setFiscalModuleId(fiscalModuleId);
                companyFiscalModule.setFiscalModule(fiscalModule);
                companyFiscalModule.setCompanyId(toBranch.getCompanyId());
                companyFiscalModule.setCompany(toBranch.getCompany());
                companyFiscalModule.setStatus(DeviceStatus.REGISTERED);
                companyFiscalModuleRepository.save(companyFiscalModule);
                warehouseHistoryService.create(fiscalModule.getSerialNumber(), companyFiscalModule.getId(), EntityTypeEnum.COMPANY_FISCAL_MODULE, DeviceStatus.REGISTERED);

                companyFiscalModule.setStatus(DeviceStatus.TRANSFERRED);
                warehouseHistoryService.create(fiscalModule.getSerialNumber(), companyFiscalModule.getId(), EntityTypeEnum.COMPANY_FISCAL_MODULE, DeviceStatus.TRANSFERRED);
                companyFiscalModuleRepository.save(companyFiscalModule);
                logHistoryService.logHistory(fiscalModule.getSerialNumber(), true, Action.TRANSFER, "Реестр", companyFiscalModule.getCompany().getName());

                //Register to branch
                branchFiscalModule.setBranchId(toBranch.getId());
                branchFiscalModule.setBranch(toBranch);
                branchFiscalModule.setFiscalModuleId(fiscalModuleId);
                branchFiscalModule.setFiscalModule(fiscalModule);
                branchFiscalModule.setStatus(DeviceStatus.REGISTERED);
                branchFiscalModuleRepository.save(branchFiscalModule);
                warehouseHistoryService.create(fiscalModule.getSerialNumber(), branchFiscalModule.getId(), EntityTypeEnum.BRANCH_FISCAL_MODULE, DeviceStatus.REGISTERED);
                logHistoryService.logHistory(fiscalModule.getSerialNumber(), true, Action.TRANSFER, companyFiscalModule.getCompany().getName(), toBranch.getName());

                //Update fiscal module status on registry
                fiscalModuleService.updateStatus(fiscalModule, DeviceStatus.REGISTERED);
            }

            return;
        }

        //Transfer from branch to registry
        if (deviceTransferDTO.getFromBranchId() != null && deviceTransferDTO.getToBranchId() == null && deviceTransferDTO.getToCompanyId() == null) {

            Branch fromBranch = branchRepository.findById(deviceTransferDTO.getFromBranchId()).orElseThrow(validationService.entityNotFoundExceptionThrow(BRANCH));

            for (Long fiscalModuleId : deviceTransferDTO.getDevices()) {

                FiscalModule fiscalModule = fiscalModuleRepository.findById(fiscalModuleId).orElseThrow(validationService.entityNotFoundExceptionThrow(FISCAL_MODULE));

                BranchFiscalModule fromBranchFiscalModule = branchFiscalModuleRepository.findFirstByBranchIdAndFiscalModuleId(deviceTransferDTO.getFromBranchId(), fiscalModuleId);
                if (fromBranchFiscalModule == null) {
                    continue;
                }

                //Transfer fiscal module from branch
                fromBranchFiscalModule.setStatus(DeviceStatus.TRANSFERRED);
                warehouseHistoryService.create(fiscalModule.getSerialNumber(), fromBranch.getId(), EntityTypeEnum.BRANCH_FISCAL_MODULE, DeviceStatus.TRANSFERRED);
                branchFiscalModuleRepository.save(fromBranchFiscalModule);
                logHistoryService.logHistory(fiscalModule.getSerialNumber(), true, Action.TRANSFER, fromBranch.getName(), "Реестр");

                //Update fiscal module status on registry
                fiscalModuleService.updateStatus(fiscalModule, DeviceStatus.UNREGISTERED);
            }

            return;
        }


        //Transfer from registry to company
        if (deviceTransferDTO.getToCompanyId() != null && deviceTransferDTO.getFromCompanyId() == null && deviceTransferDTO.getFromBranchId() == null) {

            Company toCompany = companyRepository.findById(deviceTransferDTO.getToCompanyId()).orElseThrow(validationService.entityNotFoundExceptionThrow(COMPANY));

            for (Long fiscalModuleId : deviceTransferDTO.getDevices()) {

                FiscalModule fiscalModule = fiscalModuleRepository.findById(fiscalModuleId).orElseThrow(validationService.entityNotFoundExceptionThrow(FISCAL_MODULE));

                CompanyFiscalModule toCompanyFiscalModule = companyFiscalModuleRepository.findFirstByCompanyIdAndFiscalModuleId(toCompany.getId(), fiscalModuleId);
                if (toCompanyFiscalModule == null) {
                    toCompanyFiscalModule = new CompanyFiscalModule();
                }

                toCompanyFiscalModule.setCompanyId(toCompany.getId());
                toCompanyFiscalModule.setCompany(toCompany);
                toCompanyFiscalModule.setFiscalModuleId(fiscalModuleId);
                toCompanyFiscalModule.setFiscalModule(fiscalModule);
                toCompanyFiscalModule.setStatus(DeviceStatus.REGISTERED);
                companyFiscalModuleRepository.save(toCompanyFiscalModule);
                warehouseHistoryService.create(fiscalModule.getSerialNumber(), toCompanyFiscalModule.getId(), EntityTypeEnum.COMPANY_FISCAL_MODULE, DeviceStatus.REGISTERED);

                logHistoryService.logHistory(fiscalModule.getSerialNumber(), true, Action.TRANSFER, "Реестр", toCompany.getName());

                //Update fiscal module status on registry
                fiscalModuleService.updateStatus(fiscalModule, DeviceStatus.REGISTERED);
            }

            return;
        }

        //Transfer from company to registry
        if (deviceTransferDTO.getFromCompanyId() != null && deviceTransferDTO.getToCompanyId() == null && deviceTransferDTO.getToBranchId() == null) {

            Company fromCompany = companyRepository.findById(deviceTransferDTO.getFromCompanyId()).orElseThrow(validationService.entityNotFoundExceptionThrow(COMPANY));

            for (Long fiscalModuleId : deviceTransferDTO.getDevices()) {

                FiscalModule fiscalModule = fiscalModuleRepository.findById(fiscalModuleId).orElseThrow(validationService.entityNotFoundExceptionThrow(FISCAL_MODULE));

                CompanyFiscalModule fromCompanyFiscalModule = companyFiscalModuleRepository.findFirstByCompanyIdAndFiscalModuleId(fromCompany.getId(), fiscalModuleId);
                if (fromCompanyFiscalModule == null) {
                    continue;
                }

                fromCompanyFiscalModule.setCompanyId(fromCompany.getId());
                fromCompanyFiscalModule.setCompany(fromCompany);
                fromCompanyFiscalModule.setFiscalModuleId(fiscalModuleId);
                fromCompanyFiscalModule.setFiscalModule(fiscalModule);
                fromCompanyFiscalModule.setStatus(DeviceStatus.TRANSFERRED);
                companyFiscalModuleRepository.save(fromCompanyFiscalModule);
                warehouseHistoryService.create(fiscalModule.getSerialNumber(), fromCompany.getId(), EntityTypeEnum.COMPANY_FISCAL_MODULE, DeviceStatus.TRANSFERRED);

                logHistoryService.logHistory(fiscalModule.getSerialNumber(), true, Action.TRANSFER, fromCompany.getName(), "Реестр");

                //Update fiscal module status on registry
                fiscalModuleService.updateStatus(fiscalModule, DeviceStatus.UNREGISTERED);
            }

            return;
        }

        //Transfer from one company to another company
        if (deviceTransferDTO.getFromBranchId() == null && deviceTransferDTO.getToBranchId() == null && !Objects.equals(deviceTransferDTO.getFromCompanyId(), deviceTransferDTO.getToCompanyId())) {

            Company fromCompany = companyRepository.findById(deviceTransferDTO.getFromCompanyId()).orElseThrow(validationService.entityNotFoundExceptionThrow(COMPANY));
            Company toCompany = companyRepository.findById(deviceTransferDTO.getToCompanyId()).orElseThrow(validationService.entityNotFoundExceptionThrow(COMPANY));

            for (Long fiscalModuleId : deviceTransferDTO.getDevices()) {

                FiscalModule fiscalModule = fiscalModuleRepository.findById(fiscalModuleId).orElseThrow(validationService.entityNotFoundExceptionThrow(FISCAL_MODULE));

                CompanyFiscalModule companyFiscalModule = companyFiscalModuleRepository.findFirstByCompanyIdAndFiscalModuleId(fromCompany.getId(), fiscalModule.getId());
                if (companyFiscalModule == null) {
                    throw validationService.entityNotFoundExceptionThrow(FISCAL_MODULE).get();
                }

                //Transfer
                companyFiscalModule.setStatus(DeviceStatus.TRANSFERRED);
                companyFiscalModuleRepository.save(companyFiscalModule);
                warehouseHistoryService.create(fiscalModule.getSerialNumber(), companyFiscalModule.getId(), EntityTypeEnum.COMPANY_FISCAL_MODULE, DeviceStatus.TRANSFERRED);

                //Registered
                companyFiscalModule.setCompanyId(toCompany.getId());
                companyFiscalModule.setCompany(toCompany);
                companyFiscalModule.setFiscalModuleId(fiscalModule.getId());
                companyFiscalModule.setFiscalModule(fiscalModule);
                companyFiscalModule.setStatus(DeviceStatus.REGISTERED);
                companyFiscalModuleRepository.save(companyFiscalModule);
                warehouseHistoryService.create(fiscalModule.getSerialNumber(), companyFiscalModule.getId(), EntityTypeEnum.COMPANY_FISCAL_MODULE, DeviceStatus.REGISTERED);

                logHistoryService.logHistory(fiscalModule.getSerialNumber(), true, Action.TRANSFER, fromCompany.getName(), toCompany.getName());
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
            for (Long fiscalModuleId : deviceTransferDTO.getDevices()) {

                FiscalModule fiscalModule = fiscalModuleRepository.findById(fiscalModuleId).orElseThrow(validationService.entityNotFoundExceptionThrow(FISCAL_MODULE));

                BranchFiscalModule branchFiscalModule = branchFiscalModuleRepository.findFirstByBranchIdAndFiscalModuleId(fromBranch.getId(), fiscalModule.getId());
                if (branchFiscalModule == null) {
                    throw validationService.entityNotFoundExceptionThrow(FISCAL_MODULE).get();
                }

                //Transfer
                branchFiscalModule.setStatus(DeviceStatus.TRANSFERRED);
                branchFiscalModuleRepository.save(branchFiscalModule);
                warehouseHistoryService.create(fiscalModule.getSerialNumber(), branchFiscalModule.getId(), EntityTypeEnum.BRANCH_FISCAL_MODULE, DeviceStatus.TRANSFERRED);

                //Register
                branchFiscalModule.setBranchId(toBranch.getId());
                branchFiscalModule.setBranch(toBranch);
                branchFiscalModule.setFiscalModuleId(fiscalModule.getId());
                branchFiscalModule.setFiscalModule(fiscalModule);
                branchFiscalModule.setStatus(DeviceStatus.REGISTERED);
                branchFiscalModuleRepository.save(branchFiscalModule);
                warehouseHistoryService.create(fiscalModule.getSerialNumber(), branchFiscalModule.getId(), EntityTypeEnum.BRANCH_FISCAL_MODULE, DeviceStatus.REGISTERED);

                logHistoryService.logHistory(fiscalModule.getSerialNumber(), true, Action.TRANSFER, fromBranch.getName(), toBranch.getName());
            }

            return;
        }

        //From company to branch
        if (fromBranch == null && toBranch != null) {
            for (Long fiscalModuleId : deviceTransferDTO.getDevices()) {

                FiscalModule fiscalModule = fiscalModuleRepository.findById(fiscalModuleId).orElseThrow(validationService.entityNotFoundExceptionThrow(FISCAL_MODULE));

                CompanyFiscalModule fromCompanyFiscalModule = companyFiscalModuleRepository.findFirstByCompanyIdAndFiscalModuleId(deviceTransferDTO.getFromCompanyId(), fiscalModuleId);
                if (fromCompanyFiscalModule == null) {
                    continue;
                }

                BranchFiscalModule toBranchFiscalModule = branchFiscalModuleRepository.findFirstByBranchIdAndFiscalModuleId(toBranch.getId(), fiscalModule.getId());
                if (toBranchFiscalModule == null) {
                    toBranchFiscalModule = new BranchFiscalModule();
                }

                fromCompanyFiscalModule.setStatus(DeviceStatus.TRANSFERRED);
                companyFiscalModuleRepository.save(fromCompanyFiscalModule);
                warehouseHistoryService.create(fiscalModule.getSerialNumber(), fromCompanyFiscalModule.getId(), EntityTypeEnum.COMPANY_FISCAL_MODULE, DeviceStatus.TRANSFERRED);

                toBranchFiscalModule.setBranchId(toBranch.getId());
                toBranchFiscalModule.setBranch(toBranch);
                toBranchFiscalModule.setFiscalModuleId(fiscalModule.getId());
                toBranchFiscalModule.setFiscalModule(fiscalModule);
                toBranchFiscalModule.setStatus(DeviceStatus.REGISTERED);
                branchFiscalModuleRepository.save(toBranchFiscalModule);
                warehouseHistoryService.create(fiscalModule.getSerialNumber(), toBranchFiscalModule.getId(), EntityTypeEnum.BRANCH_FISCAL_MODULE, DeviceStatus.REGISTERED);

                logHistoryService.logHistory(fiscalModule.getSerialNumber(), true, Action.TRANSFER, fromCompanyFiscalModule.getCompany().getName(), toBranch.getName());
            }
            return;
        }

        //From branch to company
        if (fromBranch != null) {

            Company toCompany = companyRepository.findById(deviceTransferDTO.getToCompanyId()).orElseThrow(validationService.entityNotFoundExceptionThrow(COMPANY));

            for (Long fiscalModuleId : deviceTransferDTO.getDevices()) {

                FiscalModule fiscalModule = fiscalModuleRepository.findById(fiscalModuleId).orElseThrow(validationService.entityNotFoundExceptionThrow(FISCAL_MODULE));

                BranchFiscalModule fromBranchFiscalModule = branchFiscalModuleRepository.findFirstByBranchIdAndFiscalModuleId(fromBranch.getId(), fiscalModule.getId());
                if (fromBranchFiscalModule == null) {
                    continue;
                }

                CompanyFiscalModule toCompanyFiscalModule = companyFiscalModuleRepository.findFirstByCompanyIdAndFiscalModuleId(deviceTransferDTO.getToCompanyId(), fiscalModule.getId());
                if (toCompanyFiscalModule == null) {
                    toCompanyFiscalModule = new CompanyFiscalModule();
                }

                //Transfer
                fromBranchFiscalModule.setStatus(DeviceStatus.TRANSFERRED);
                branchFiscalModuleRepository.save(fromBranchFiscalModule);
                warehouseHistoryService.create(fiscalModule.getSerialNumber(), fromBranchFiscalModule.getId(), EntityTypeEnum.BRANCH_FISCAL_MODULE, DeviceStatus.TRANSFERRED);

                //Register
                toCompanyFiscalModule.setFiscalModuleId(fiscalModuleId);
                toCompanyFiscalModule.setFiscalModule(fiscalModule);
                toCompanyFiscalModule.setCompanyId(toCompany.getId());
                toCompanyFiscalModule.setCompany(toCompany);
                toCompanyFiscalModule.setStatus(DeviceStatus.REGISTERED);
                companyFiscalModuleRepository.save(toCompanyFiscalModule);
                warehouseHistoryService.create(fiscalModule.getSerialNumber(), toCompanyFiscalModule.getId(), EntityTypeEnum.COMPANY_FISCAL_MODULE, DeviceStatus.REGISTERED);

                logHistoryService.logHistory(fiscalModule.getSerialNumber(), true, Action.TRANSFER, fromBranch.getName(), toCompanyFiscalModule.getCompany().getName());
            }
        }

    }


    @Override
    @Transactional(readOnly = true)
    public Page<FiscalModuleForTransferListDTO> listForTransfer(CompanyDeviceFilter filter) {
        if (filter.getBranchId() != null) {
            return branchFiscalModuleService.getListForTransfer(filter);
        }
        if (filter.getCompanyId() != null) {
            return getListForTransfer(filter);
        }
        return fiscalModuleService.getListForTransfer(filter);
    }

    @Override
    public Page<CompanyFiscalModuleListAdminDTO> getListForAdmin(BaseFilter filter) {
        ResultList<CompanyFiscalModule> resultList = companyFiscalModuleRepository.getResultList(filter);
        List<CompanyFiscalModuleListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(companyFiscalModule -> {
                    CompanyFiscalModuleListAdminDTO companyFiscalModuleListDTO = new CompanyFiscalModuleListAdminDTO();
                    companyFiscalModuleListDTO.setId(companyFiscalModule.getId());
                    companyFiscalModuleListDTO.setFiscalModule(companyFiscalModule.getFiscalModule().toSerialNumberDTO());
                    companyFiscalModuleListDTO.setStatus(companyFiscalModule.getStatus());
                    companyFiscalModuleListDTO.setCreatedDate(companyFiscalModule.getCreatedDate());
                    companyFiscalModuleListDTO.setCreator(companyFiscalModule.getCreator().toCommonDTO());
                    return companyFiscalModuleListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public FiscalModuleDetailAdminDTO getForAdmin(Long id) {
        return companyFiscalModuleRepository.findById(id).map(companyFiscalModule -> fiscalModuleService.getForAdmin(companyFiscalModule.getFiscalModuleId())).orElseThrow(validationService.entityNotFoundExceptionThrow(FISCAL_MODULE));
    }

    @Override
    public List<DeviceStatisticsAdminDTO> getStatisticsForAdmin(BaseFilter filter) {
        return companyFiscalModuleRepository.getStatistics(filter);
    }

    private Page<FiscalModuleForTransferListDTO> getListForTransfer(CompanyDeviceFilter filter) {
        if (StringUtils.isNotEmpty(filter.getFromSerialNumber())) {
            FiscalModule fiscalModule = fiscalModuleRepository.findBySerialNumber(filter.getFromSerialNumber().toUpperCase());
            if (fiscalModule == null) {
                throw validationService.entityNotFoundExceptionThrow(FISCAL_MODULE, SERIAL_NUMBER, filter.getFromSerialNumber().toUpperCase()).get();
            }
            filter.setFiscalModuleId(fiscalModule.getId());
        }
        ResultList<FiscalModuleForTransferListDTO> resultList = companyFiscalModuleRepository.getListForTransfer(filter);
        return new PageImpl<>(resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }
}
