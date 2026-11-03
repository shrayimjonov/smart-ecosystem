package uz.uzkassa.cts.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.dto.changelog.ChangeLogListDTO;
import uz.uzkassa.cts.dto.payload.ChangeLogPayload;
import uz.uzkassa.cts.filter.ClickhouseFilter;
import uz.uzkassa.cts.repository.clickhouse.ClickhouseChangeLogRepository;
import uz.uzkassa.cts.service.ChangeLogService;

import static lombok.AccessLevel.PRIVATE;

@Transactional(readOnly = true)
@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class ChangeLogServiceImpl implements ChangeLogService {

    ClickhouseChangeLogRepository clickhouseChangeLogRepository;

    @Override
    @Transactional
    public void create(ChangeLogPayload payload) {
        clickhouseChangeLogRepository.create(payload);
    }

    public Page<ChangeLogListDTO> getList(ClickhouseFilter filter) {
        return clickhouseChangeLogRepository.getList(filter);
    }
}
