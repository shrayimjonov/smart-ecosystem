package uz.uzkassa.cts.service;

import uz.uzkassa.cts.enums.Action;

public interface LogHistoryService {

    void logHistory(String serialNumber, boolean isFM, Action action, String fromWarehouse, String toWarehouse, String fromValue, String toValue);

    void logHistory(String serialNumber, boolean isFM, Action action, String from, String to);
}
