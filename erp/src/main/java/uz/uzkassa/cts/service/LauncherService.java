package uz.uzkassa.cts.service;

import uz.uzkassa.cts.dto.launcher.LauncherTerminalRequestDto;

import java.util.List;

public interface LauncherService {
    String authenticate();
    void action(LauncherTerminalRequestDto dto);
    void notify(LauncherTerminalRequestDto dto);
    void updateBankType(LauncherTerminalRequestDto dto);
    void sendNotification(String content, String deviceSerialNumber);
    void block(List<String> devices);
    void unlock(List<String> devices);
}
