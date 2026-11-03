package uz.uzkassa.smartpos.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import uz.uzkassa.smartpos.domain.postgres.terminal.Device;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.dto.integration.launcher.CtoActionDTO;
import uz.uzkassa.smartpos.dto.launcher.ActiveVersionDTO;
import uz.uzkassa.smartpos.dto.launcher.GetLastVersionRequestDTO;
import uz.uzkassa.smartpos.dto.launcher.GetLastVersionResponseDTO;
import uz.uzkassa.smartpos.enums.DeviceType;
import uz.uzkassa.smartpos.enums.TerminalAction;
import uz.uzkassa.smartpos.integration.launcher.LauncherFeignClient;
import uz.uzkassa.smartpos.repository.postgres.company.BillingPermissionRepository;
import uz.uzkassa.smartpos.repository.postgres.terminal.DeviceRepository;
import uz.uzkassa.smartpos.utils.ServerUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LauncherService extends SmartposService {
    private final TemplateEngine templateEngine;
    private final LauncherFeignClient launcherFeignClient;
    private final BillingPermissionRepository billingPermissionRepository;
    private final DeviceRepository deviceRepository;

    @Scheduled(fixedRate = 60 * 60 * 1000, initialDelay = 10 * 60 * 1000)
    public void blockTerminals() {
        List<CommonDTO> companies = companyRepository.expiredCompanies();
        log.info("==================================================================================");
        log.info("Launcher block companies: {}", companies.stream().map(CommonDTO::getName).collect(Collectors.toList()));
        log.info("==================================================================================");
        blockTerminals(companies.stream().map(CommonDTO::getId).collect(Collectors.toList()));
    }

    public void blockTerminals(List<Long> companyIds) {
        List<Device> terminals = deviceRepository.findAllByCompanyIdInAndType(companyIds, DeviceType.FISCAL);
        if (CollectionUtils.isNotEmpty(terminals)) {
            CtoActionDTO terminalActionDTO = new CtoActionDTO();
            terminalActionDTO.setAction(TerminalAction.BLOCK);
            terminalActionDTO.setTerminalNumbers(terminals.stream().map(Device::getSerialNumber).collect(Collectors.toSet()));
            terminalActionDTO.setMessage(templateEngine.process("/block-terminal", new Context()));
            log.info("Launcher block terminals: {}", terminalActionDTO.getTerminalNumbers());
            launcherFeignClient.terminalAction(getToken(), terminalActionDTO);
        }
    }

    public void unblockTerminals(Set<Long> companyIds) {
        List<Device> terminals = deviceRepository.findAllByCompanyIdInAndType(companyIds, DeviceType.FISCAL);
        if (CollectionUtils.isNotEmpty(terminals)) {
            CtoActionDTO terminalActionDTO = new CtoActionDTO();
            terminalActionDTO.setAction(TerminalAction.UNBLOCK);
            terminalActionDTO.setTerminalNumbers(terminals.stream().map(Device::getSerialNumber).collect(Collectors.toSet()));
            log.info("Launcher unblock terminals: {}", terminalActionDTO.getTerminalNumbers());
            launcherFeignClient.terminalAction(getToken(), terminalActionDTO);
        }
    }

    public void unblockTerminals() {
        Set<Long> longs = billingPermissionRepository.getcompanyIds();
        ServerUtils.partition(longs, 200).forEach(this::unblockTerminals);
    }

    private String getToken() {
        return launcherFeignClient.getToken(applicationProperties.getLauncher().getLogin(), applicationProperties.getLauncher().getPassword());
    }

    public ActiveVersionDTO getActiveVersion(final String packageName) {
        final ResponseEntity<GetLastVersionResponseDTO> response = launcherFeignClient.getActiveVersion(getToken(),
                GetLastVersionRequestDTO.builder()
                        .packageName(packageName)
                        .build());
        return Optional.ofNullable(response)
                .map(ResponseEntity::getBody)
                .map(r -> ActiveVersionDTO.builder()
                        .versionCode(r.getVersionCode())
                        .versionName(r.getVersionName())
                        .build())
                .orElse(null);
    }
}
