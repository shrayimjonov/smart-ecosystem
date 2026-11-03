package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.History;
import uz.uzkassa.cts.dto.device.HistoryListDTO;
import uz.uzkassa.cts.dto.payload.HistoryPayload;
import uz.uzkassa.cts.filter.HistoryListFilter;

public interface HistoryService extends BaseService<History, Long> {

    void create(HistoryPayload historyPayload);

    Page<HistoryListDTO> list(HistoryListFilter filter);
}
