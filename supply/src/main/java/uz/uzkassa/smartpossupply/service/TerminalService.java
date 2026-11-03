package uz.uzkassa.smartpossupply.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.Terminal;
import uz.uzkassa.smartpossupply.domain.company.CompanyPermission;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.branch.CommonStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.integration.TerminalSyncDTO;
import uz.uzkassa.smartpossupply.dto.terminal.TerminalServiceDTO;
import uz.uzkassa.smartpossupply.dto.terminal.TerminalListDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.LauncherStatus;
import uz.uzkassa.smartpossupply.filters.TerminalFilter;
import uz.uzkassa.smartpossupply.repository.TerminalRepository;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 03.12.2024
 */
@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class TerminalService extends BaseService {

    TerminalRepository terminalRepository;
    LauncherService launcherService;

    public Page<TerminalListDTO> getList(TerminalFilter filter) {
        ResultList<Terminal> resultList = terminalRepository.getResultList(filter);
        List<TerminalListDTO> result = resultList.getList().stream().map(terminal -> {
            TerminalListDTO terminalListDTO = new TerminalListDTO();
            terminalListDTO.setId(terminal.getId());
            terminalListDTO.setSerialNumber(terminal.getSerialNumber());
            terminalListDTO.setFiscalModuleSerialNumber(terminal.getFiscalModuleSerialNumber());
            terminalListDTO.setStatus(terminal.getStatus());
            terminalListDTO.setLauncherStatus(terminal.getLauncherStatus());
            terminalListDTO.setCompany(terminal.getCompany().toCommonBaseDTO());
            terminalListDTO.setBranch(terminal.getBranch().toCommonDTO());
            return terminalListDTO;
        }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    @Transactional
    public Long sync(TerminalSyncDTO terminalSyncDTO) {
        Long companyId = companyRepository.getIdByErpId(terminalSyncDTO.getCustomer().getId());
        if (companyId == null) {
            return null;
        }
        Long branchId = branchRepository.getIdByErpId(terminalSyncDTO.getCustomerBranch().getId());
        if (branchId == null) {
            return null;
        }
        Terminal terminal = terminalRepository.findFirstBySerialNumber(terminalSyncDTO.getDeviceSerial()).orElseGet(Terminal::new);
        terminal.setCompanyId(companyId);
        terminal.setBranchId(branchId);
        terminal.setSerialNumber(terminalSyncDTO.getDeviceSerial());
        terminal.setFiscalModuleSerialNumber(terminalSyncDTO.getFiscalModuleSerial());
        return terminalRepository.save(terminal).getId();
    }

    @Transactional
    public void updateStatus(Long id, CommonStatus status, Boolean syncWithLauncher) {
        terminalRepository.findById(id).ifPresentOrElse(terminal -> {
            if (syncWithLauncher != null && syncWithLauncher) {
                if (CommonStatus.IN_ACTIVE == status) {
                    launcherService.blockTerminals(List.of(terminal));
                    terminal.setLauncherStatus(LauncherStatus.BLOCK);
                } else {
                    launcherService.unblockTerminals(List.of(terminal));
                    terminal.setLauncherStatus(LauncherStatus.UNBLOCK);
                }
            }
            terminal.setStatus(status);
            terminalRepository.save(terminal);
        }, () -> validationService.entityNotFoundExceptionThrow(Terminal.class.getSimpleName(), "id", id));
    }

    public CommonStatisticsDTO getStatistics(TerminalFilter filter) {
        return terminalRepository.getStatistics(filter);
    }

    public Page<TerminalServiceDTO> getTerminals(TerminalFilter filter) {
        filter.setCompanyId(SecurityUtils.getCompanyId());
        ResultList<Terminal> resultList = terminalRepository.getResultList(filter);
        List<TerminalServiceDTO> result = resultList.getList().stream().map(terminal -> {
            TerminalServiceDTO terminalServiceDTO = new TerminalServiceDTO();
            terminalServiceDTO.setId(terminal.getId());
            terminalServiceDTO.setSerialNumber(terminal.getSerialNumber());
            terminalServiceDTO.setFiscalModuleSerialNumber(terminal.getFiscalModuleSerialNumber());
            terminalServiceDTO.setStatus(terminal.getStatus());
            terminalServiceDTO.setLauncherStatus(terminal.getLauncherStatus());
            terminalServiceDTO.setBranch(terminal.getBranch().toCommonDTO());
            terminalServiceDTO.setStartTime(terminal.getStartTime());
            terminalServiceDTO.setEndTime(terminal.getEndTime());
            return terminalServiceDTO;
        }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    @Transactional
    public void setPermissions(CompanyPermission permission, List<String> serialNumbers) {
        log.info("Check permissions id: {}", permission.getId());
        List<Terminal> terminals = terminalRepository.findAllByCompanyIdAndSerialNumberIn(permission.getCompanyId(), serialNumbers);
        terminals.forEach(terminal -> {
            terminal.setPermissionId(permission.getId());
            terminal.setStartTime(permission.getStartDate());
            terminal.setEndTime(permission.getEndDate());
        });
        terminalRepository.saveAll(terminals);
    }
}
