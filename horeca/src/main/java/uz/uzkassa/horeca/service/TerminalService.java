package uz.uzkassa.horeca.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.horeca.domain.Terminal;
import uz.uzkassa.horeca.domain.base.SimpleEntity;
import uz.uzkassa.horeca.domain.branch.Branch;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.dto.sync.FiscalizationSyncDTO;
import uz.uzkassa.horeca.dto.terminal.TerminalAdminDTO;
import uz.uzkassa.horeca.dto.terminal.TerminalListAdminDTO;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.repository.TerminalRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class TerminalService extends BaseService {

    TerminalRepository terminalRepository;

    public List<CommonDTO> getItems(Long branchId) {
        return terminalRepository.findAllByBranchIdAndDeletedIsFalse(getCurrentBranchId(branchId)).stream().map(SimpleEntity::toCommonDTO).collect(Collectors.toList());
    }

    public List<CommonDTO> getAdminItems(BaseFilter filter) {
        return terminalRepository.getResultList(filter)
                .getList().stream().map(SimpleEntity::toCommonDTO)
                .collect(Collectors.toList());
    }

    public Page<TerminalListAdminDTO> getList(BaseFilter filter) {
        ResultList<Terminal> resultList = terminalRepository.getResultList(filter);
        List<TerminalListAdminDTO> result = resultList.getList().stream().map(terminal -> {
            TerminalListAdminDTO terminalListDTO = new TerminalListAdminDTO();
            terminalListDTO.setId(terminal.getId());
            terminalListDTO.setSerialNumber(terminal.getSerialNumber());
            terminalListDTO.setFiscalModuleSerialNumber(terminal.getFiscalModuleSerialNumber());
            terminalListDTO.setBranch(terminal.getBranch().toCommonDTO());
            return terminalListDTO;
        }).collect(Collectors.toList());
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    @Transactional
    public Long sync(FiscalizationSyncDTO fiscalizationSyncDTO) {
        try {
            log.info("SYNC PAYLOAD: {}", objectMapper.writeValueAsString(fiscalizationSyncDTO));
        } catch (JsonProcessingException e) {
            log.error("Object writing error: {}", e.getMessage());
        }
        Branch branch = branchRepository.findFirstByCtsId(fiscalizationSyncDTO.getCustomerBranch().getId()).orElseThrow(notFoundExceptionThrow(Branch.class.getSimpleName()));
        Terminal terminal = terminalRepository.findFirstByBranchIdAndSerialNumberAndFiscalModuleSerialNumberAndDeletedIsFalse(branch.getId(), fiscalizationSyncDTO.getDeviceSerial(), fiscalizationSyncDTO.getFiscalModuleSerial()).orElseGet(Terminal::new);
        terminal.setSerialNumber(fiscalizationSyncDTO.getDeviceSerial());
        terminal.setFiscalModuleSerialNumber(fiscalizationSyncDTO.getFiscalModuleSerial());
        terminal.setBranchId(branch.getId());
        return terminalRepository.save(terminal).getId();
    }

    public Long update(Long id, TerminalAdminDTO terminalAdminDTO) {
        Terminal terminal = terminalRepository.findById(id)
                .orElseThrow(notFoundExceptionThrow(Terminal.class.getSimpleName()));
        terminal.setSerialNumber(terminalAdminDTO.getSerialNumber());
        terminal.setFiscalModuleSerialNumber(terminalAdminDTO.getFiscalModuleSerialNumber());
        terminal.setBranchId(terminalAdminDTO.getBranchId());
        return terminalRepository.save(terminal).getId();
    }

    public TerminalAdminDTO get(Long id) {
        Terminal terminal = terminalRepository.findById(id)
                .orElseThrow(notFoundExceptionThrow(Terminal.class.getSimpleName()));
        TerminalAdminDTO terminalAdminDTO = new TerminalAdminDTO();
        terminalAdminDTO.setId(terminal.getId());
        terminalAdminDTO.setSerialNumber(terminal.getSerialNumber());
        terminalAdminDTO.setFiscalModuleSerialNumber(terminal.getFiscalModuleSerialNumber());
        terminalAdminDTO.setBranchId(terminal.getBranchId());
        return terminalAdminDTO;
    }
}
