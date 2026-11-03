package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import uz.uzkassa.smartpossupply.domain.Terminal;
import uz.uzkassa.smartpossupply.dto.launcher.LauncherActionDTO;
import uz.uzkassa.smartpossupply.dto.launcher.LauncherTerminalRequestDto;
import uz.uzkassa.smartpossupply.enums.TerminalAction;
import uz.uzkassa.smartpossupply.integration.LauncherClient;
import uz.uzkassa.smartpossupply.repository.TerminalRepository;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 11.12.2024
 */
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class LauncherService extends BaseService {

    TerminalRepository terminalRepository;
    TemplateEngine templateEngine;
    LauncherClient launcherClient;

    @Transactional
    public void blockTerminalsByCompanyId(Long companyId) {
        List<Terminal> terminals = terminalRepository.findByBranch_CompanyIdIn(List.of(Optional.ofNullable(companyId).orElseGet(SecurityUtils::getCurrentCompanyId)));
        this.blockTerminals(terminals);
    }

    @Transactional
    public void blockTerminals(LauncherActionDTO launcherActionDTO) {
        this.blockTerminals(terminalRepository.findAllById(launcherActionDTO.getTerminalIds()));
    }

    @Transactional
    public void blockTerminals(List<Terminal> terminals) {
        this.send(terminals, TerminalAction.BLOCK);
    }

    @Transactional
    public void unblockTerminalsByCompanyId(Long companyId) {
        List<Terminal> terminals = terminalRepository.findByBranch_CompanyIdIn(List.of(Optional.ofNullable(companyId).orElseGet(SecurityUtils::getCurrentCompanyId)));
        this.unblockTerminals(terminals);
    }

    @Transactional
    public void unBlockTerminals(LauncherActionDTO launcherActionDTO) {
        this.unblockTerminals(terminalRepository.findAllById(launcherActionDTO.getTerminalIds()));
    }

    @Transactional
    public void unblockTerminals(List<Terminal> terminals) {
        this.send(terminals, TerminalAction.UNBLOCK);
    }

    @Transactional
    public void send(List<Terminal> terminals, TerminalAction action) {
        LauncherTerminalRequestDto terminalActionDTO = new LauncherTerminalRequestDto();
        terminalActionDTO.setAction(action.name());
        terminalActionDTO.setTerminalNumbers(terminals.stream().map(Terminal::getSerialNumber).toList());
        if (TerminalAction.BLOCK == action) {
            terminalActionDTO.setMessage(templateEngine.process("BlockTerminal", new Context()));
        }
        launcherClient.send(terminalActionDTO);
    }
}
