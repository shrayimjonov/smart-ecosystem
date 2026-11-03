package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.DeviceSuspendHistory;
import uz.uzkassa.cts.dto.devicesuspendhistory.response.DeviceSuspendHistoryResponseDto;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.DeviceSuspendHistoryFilter;

public interface DeviceSuspendHistoryOldService extends BaseService<DeviceSuspendHistory, Long> {
    DeviceSuspendHistory getLastByCustomerBranchDeviceId(Long customerBranchDeviceId);

    Page<DeviceSuspendHistoryResponseDto> getByContractId(Long contractId, BaseFilter filter);

    void suspend(Long customerBranchDeviceId);

    Page<DeviceSuspendHistoryResponseDto> getList(DeviceSuspendHistoryFilter filter);

    byte[] download(DeviceSuspendHistoryFilter filter);
}
