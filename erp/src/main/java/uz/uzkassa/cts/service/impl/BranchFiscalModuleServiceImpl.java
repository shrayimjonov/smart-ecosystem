package uz.uzkassa.cts.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.Branch;
import uz.uzkassa.cts.domain.BranchFiscalModule;
import uz.uzkassa.cts.domain.FiscalModule;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.device.BaseDeviceFiscalModuleListDTO;
import uz.uzkassa.cts.dto.device.CompanyTerminalListDTO;
import uz.uzkassa.cts.dto.device.SerialNumberDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.dto.fm.FiscalModuleForTransferListDTO;
import uz.uzkassa.cts.dto.fm.admin.BranchFiscalModuleListAdminDTO;
import uz.uzkassa.cts.dto.fm.admin.FiscalModuleDetailAdminDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.CompanyBranchDeviceFilter;
import uz.uzkassa.cts.filter.CompanyDeviceFilter;
import uz.uzkassa.cts.repository.BranchFiscalModuleRepository;
import uz.uzkassa.cts.repository.FiscalModuleRepository;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.BranchFiscalModuleService;
import uz.uzkassa.cts.service.FiscalModuleService;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class BranchFiscalModuleServiceImpl extends BaseServiceImpl<BranchFiscalModule, Long> implements BranchFiscalModuleService {

    private final FiscalModuleRepository fiscalModuleRepository;
    private final BranchFiscalModuleRepository branchFiscalModuleRepository;
    private final FiscalModuleService fiscalModuleService;

    public BranchFiscalModuleServiceImpl(BranchFiscalModuleRepository branchFiscalModuleRepository,
                                         FiscalModuleRepository fiscalModuleRepository,
                                         @Lazy FiscalModuleService fiscalModuleService) {
        super(branchFiscalModuleRepository);
        this.fiscalModuleRepository = fiscalModuleRepository;
        this.branchFiscalModuleRepository = branchFiscalModuleRepository;
        this.fiscalModuleService = fiscalModuleService;
    }

    @Override
    public Page<BranchFiscalModuleListAdminDTO> getListForAdmin(BaseFilter filter) {
        ResultList<BranchFiscalModule> resultList = branchFiscalModuleRepository.getResultList(filter);
        List<BranchFiscalModuleListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(branchFiscalModule -> {
                    BranchFiscalModuleListAdminDTO branchFiscalModuleListDTO = new BranchFiscalModuleListAdminDTO();
                    branchFiscalModuleListDTO.setId(branchFiscalModule.getId());
                    branchFiscalModuleListDTO.setFiscalModule(branchFiscalModule.getFiscalModule().toSerialNumberDTO());
                    branchFiscalModuleListDTO.setStatus(branchFiscalModule.getStatus());
                    branchFiscalModuleListDTO.setCreatedDate(branchFiscalModule.getCreatedDate());
                    branchFiscalModuleListDTO.setCreator(branchFiscalModule.getCreator().toCommonDTO());
                    branchFiscalModuleListDTO.setBranch(branchFiscalModule.getBranch().toCommonDTO());
                    branchFiscalModuleListDTO.setCompany(branchFiscalModule.getBranch().getCompany().toShortDTO());
                    return branchFiscalModuleListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public FiscalModuleDetailAdminDTO getForAdmin(Long id) {
        return branchFiscalModuleRepository.findById(id).map(branchFiscalModule -> fiscalModuleService.getForAdmin(branchFiscalModule.getFiscalModuleId())).orElseThrow(validationService.entityNotFoundExceptionThrow(FISCAL_MODULE));
    }

    @Override
    public List<DeviceStatisticsAdminDTO> getStatisticsForAdmin(BaseFilter filter) {
        return branchFiscalModuleRepository.getStatistics(filter);
    }

    @Override
    public Page<BaseDeviceFiscalModuleListDTO> list(BaseFilter filter) {
        Long branchId = SecurityUtils.getBranchId();
        if (branchId == null || branchId == 0) {
            throw validationService.entityNotFoundExceptionThrow(BRANCH).get();
        }
        ResultList<BaseDeviceFiscalModuleListDTO> resultList = branchFiscalModuleRepository.getList(filter);
        return new PageImpl<>
                (resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public Page<BaseDeviceFiscalModuleListDTO> listByEmployee(BaseFilter filter) {
        ResultList<BaseDeviceFiscalModuleListDTO> resultList = branchFiscalModuleRepository.getListByEmployee(filter);
        return new PageImpl<>
                (resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }

    public Page<FiscalModuleForTransferListDTO> getListForTransfer(CompanyDeviceFilter filter) {
        if (StringUtils.isNotEmpty(filter.getFromSerialNumber())) {
            FiscalModule fiscalModule = fiscalModuleRepository.findBySerialNumber(filter.getFromSerialNumber().toUpperCase());
            if (fiscalModule == null) {
                throw validationService.entityNotFoundExceptionThrow(FISCAL_MODULE).get();
            }
            filter.setFiscalModuleId(fiscalModule.getId());
        }
        ResultList<FiscalModuleForTransferListDTO> resultList = branchFiscalModuleRepository.getListForTransfer(filter);
        return new PageImpl<>
                (resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }

    public Page<SerialNumberDTO> getListForTransfer2(CompanyDeviceFilter filter) {
        ResultList<SerialNumberDTO> resultList = branchFiscalModuleRepository.getListForTransfer2(filter);
        return new PageImpl<>
                (resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public Page<CompanyTerminalListDTO> listAll(CompanyBranchDeviceFilter filter) {
        ResultList<CompanyTerminalListDTO> resultList = branchFiscalModuleRepository.getListAll(filter);
        return new PageImpl<>
                (resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }
}
