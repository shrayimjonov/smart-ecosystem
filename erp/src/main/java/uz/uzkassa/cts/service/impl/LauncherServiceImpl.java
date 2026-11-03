package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.uzkassa.cts.dto.launcher.LauncherStatus;
import uz.uzkassa.cts.dto.launcher.LauncherNotification;
import uz.uzkassa.cts.dto.launcher.LauncherTerminalRequestDto;
import uz.uzkassa.cts.integrations.LauncherClient;
import uz.uzkassa.cts.service.LauncherService;
import uz.uzkassa.cts.service.ThymeleafService;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class LauncherServiceImpl implements LauncherService {

    private final ThymeleafService thymeleafService;
    private final LauncherClient launcherClient;

    public LauncherServiceImpl(ThymeleafService thymeleafService, LauncherClient launcherClient) {
        this.thymeleafService = thymeleafService;
        this.launcherClient = launcherClient;
    }

    @Override
    public String authenticate() {
        return launcherClient.getToken();
    }

    @Override
    public void action(LauncherTerminalRequestDto dto) {
        launcherClient.send(dto);
    }

    public void updateBankType(LauncherTerminalRequestDto dto) {
        launcherClient.updateBankType(dto);
    }

    @Override
    public void notify(LauncherTerminalRequestDto dto) {
        launcherClient.send(dto);
    }

    @Override
    public void sendNotification(String content, String deviceSerialNumber) {
        notify(new LauncherTerminalRequestDto(
                new LauncherNotification(content, "Оплата услуг", 1L),
                Collections.singletonList(deviceSerialNumber))
        );
    }

    @Override
    public void block(List<String> devices) {
        blockUnlock(devices, LauncherStatus.BLOCK);
    }

    @Override
    public void unlock(List<String> devices) {
        blockUnlock(devices, LauncherStatus.UNBLOCK);
    }

    private void blockUnlock(List<String> devices, LauncherStatus launcherStatus) {
        LauncherTerminalRequestDto launcherTerminalRequestDto = new LauncherTerminalRequestDto();
        launcherTerminalRequestDto.setTerminalNumbers(devices);
        launcherTerminalRequestDto.setAction(launcherStatus.name());
        if (launcherStatus == LauncherStatus.BLOCK) {
            launcherTerminalRequestDto.setMessage(thymeleafService.generateCronBlockTextHTML());
        }
        action(launcherTerminalRequestDto);
    }
}
