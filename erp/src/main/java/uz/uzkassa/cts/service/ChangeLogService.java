package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.dto.changelog.ChangeLogListDTO;
import uz.uzkassa.cts.dto.payload.ChangeLogPayload;
import uz.uzkassa.cts.filter.ClickhouseFilter;

public interface ChangeLogService {

    void create(ChangeLogPayload payload);

    Page<ChangeLogListDTO> getList(ClickhouseFilter filter);
}
