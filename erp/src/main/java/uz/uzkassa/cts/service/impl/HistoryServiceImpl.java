package uz.uzkassa.cts.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.History;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.device.HistoryListDTO;
import uz.uzkassa.cts.dto.payload.HistoryPayload;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.filter.HistoryListFilter;
import uz.uzkassa.cts.mappings.HistoryMapper;
import uz.uzkassa.cts.repository.HistoryRepository;
import uz.uzkassa.cts.service.HistoryService;
import uz.uzkassa.cts.utils.DateUtil;
import uz.uzkassa.cts.validator.LocalizationMessage;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class HistoryServiceImpl extends BaseServiceImpl<History, Long> implements HistoryService {

    private final HistoryRepository historyRepository;
    private final HistoryMapper historyMapper;
    private final LocalizationMessage localizationMessage;

    public HistoryServiceImpl(HistoryRepository historyRepository, HistoryMapper historyMapper, LocalizationMessage localizationMessage) {
        super(historyRepository);
        this.historyRepository = historyRepository;
        this.historyMapper = historyMapper;
        this.localizationMessage = localizationMessage;
    }

    @Override
    public void create(HistoryPayload historyPayload) {
        historyRepository.save(historyMapper.toEntity(historyPayload));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HistoryListDTO> list(HistoryListFilter filter) {
        ResultList<History> resultList = historyRepository.getListResult(filter);
        List<HistoryListDTO> result = new ArrayList<>();
        for (History history : resultList.getList()) {
            StringBuilder text = new StringBuilder();
            switch (history.getAction()) {
                case TRANSFER:
                    if (history.isFm()) {
                        text.append(localizationMessage.localize("fiscal.module.transfer.history", new Object[]{
                                history.getSerialNumber(),
                                history.getFromWarehouse(),
                                history.getToWarehouse(),
                                DateUtil.getLocalDateTimeAsStringWithoutSecond(history.getModifiedDate()),
                                history.getModifier(),
                        }));
                    } else {
                        text.append(localizationMessage.localize("device.transfer.history", new Object[]{
                                history.getSerialNumber(),
                                history.getFromWarehouse(),
                                history.getToWarehouse(),
                                DateUtil.getLocalDateTimeAsStringWithoutSecond(history.getModifiedDate()),
                                history.getModifier(),
                        }));
                    }

                    break;
                case UPDATE:
                    if (history.isFm()) {
                        text.append(localizationMessage.localize("fiscal.module.update.history", new Object[]{
                                history.getSerialNumber(),
                                DeviceStatus.get(history.getFromValue()) != null ? DeviceStatus.get(history.getFromValue()).getName() : history.getFromValue(),
                                DeviceStatus.get(history.getToValue()) != null ? DeviceStatus.get(history.getToValue()).getName() : history.getToValue(),
                                DateUtil.getLocalDateTimeAsStringWithoutSecond(history.getModifiedDate()),
                                history.getModifier(),
                        }));
                    } else {
                        text.append(localizationMessage.localize("device.update.history", new Object[]{
                                history.getSerialNumber(),
                                DeviceStatus.get(history.getFromValue()) != null ? DeviceStatus.get(history.getFromValue()).getName() : history.getFromValue(),
                                DeviceStatus.get(history.getToValue()) != null ? DeviceStatus.get(history.getToValue()).getName() : history.getToValue(),
                                DateUtil.getLocalDateTimeAsStringWithoutSecond(history.getModifiedDate()),
                                history.getModifier(),
                        }));
                    }
                    break;
                case DELETE:
                    if (history.isFm()) {
                        text.append(localizationMessage.localize("fiscal.module.delete.history", new Object[]{
                                history.getSerialNumber(),
                                history.getFromWarehouse(),
                                DateUtil.getLocalDateTimeAsStringWithoutSecond(history.getModifiedDate()),
                                history.getModifier(),
                        }));
                    } else {
                        text.append(localizationMessage.localize("device.delete.history", new Object[]{
                                history.getSerialNumber(),
                                history.getFromWarehouse(),
                                DateUtil.getLocalDateTimeAsStringWithoutSecond(history.getModifiedDate()),
                                history.getModifier(),
                        }));
                    }
                    break;
            }
            if (!text.isEmpty()) {
                result.add(new HistoryListDTO(text.toString()));
            }
        }

        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }
}
