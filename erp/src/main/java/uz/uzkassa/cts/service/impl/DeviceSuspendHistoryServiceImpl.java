package uz.uzkassa.cts.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import uz.uzkassa.cts.domain.DeviceSuspendHistory;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.devicesuspendhistory.admin.DeviceSuspendHistoryListAdminDTO;
import uz.uzkassa.cts.filter.DeviceSuspendHistoryFilter;
import uz.uzkassa.cts.repository.DeviceSuspendHistoryRepository;
import uz.uzkassa.cts.service.DeviceSuspendHistoryService;

import java.util.List;
import java.util.Optional;

@Service
public class DeviceSuspendHistoryServiceImpl extends BaseServiceImpl<DeviceSuspendHistory, Long> implements DeviceSuspendHistoryService {

    private final DeviceSuspendHistoryRepository deviceSuspendHistoryRepository;

    public DeviceSuspendHistoryServiceImpl(DeviceSuspendHistoryRepository deviceSuspendHistoryRepository) {
        super(deviceSuspendHistoryRepository);
        this.deviceSuspendHistoryRepository = deviceSuspendHistoryRepository;
    }

    @Override
    public Page<DeviceSuspendHistoryListAdminDTO> getListForAdmin(DeviceSuspendHistoryFilter filter) {
        ResultList<DeviceSuspendHistory> resultList = deviceSuspendHistoryRepository.getResultList(filter);
        List<DeviceSuspendHistoryListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(deviceSuspendHistory -> {
                    DeviceSuspendHistoryListAdminDTO deviceSuspendHistoryListAdminDTO = new DeviceSuspendHistoryListAdminDTO();
                    deviceSuspendHistoryListAdminDTO.setDeviceSerialNumber(deviceSuspendHistory.getDeviceSerialNumber());
                    deviceSuspendHistoryListAdminDTO.setStartDate(deviceSuspendHistory.getStartDate());
                    deviceSuspendHistoryListAdminDTO.setDueDate(deviceSuspendHistory.getDueDate());
                    deviceSuspendHistoryListAdminDTO.setCustomer(deviceSuspendHistory.getContract().getCustomer().toCommonDTO());
                    deviceSuspendHistoryListAdminDTO.setTariff(deviceSuspendHistory.getTariff().toCommonDTO());
                    deviceSuspendHistoryListAdminDTO.setCreator(Optional.ofNullable(deviceSuspendHistory.getCreator()).map(SimpleEntity::toCommonDTO).orElse(null));
                    deviceSuspendHistoryListAdminDTO.setLastModifier(Optional.ofNullable(deviceSuspendHistory.getLastModifier()).map(SimpleEntity::toCommonDTO).orElse(null));
                    return deviceSuspendHistoryListAdminDTO;
                }).toList();
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }
}
