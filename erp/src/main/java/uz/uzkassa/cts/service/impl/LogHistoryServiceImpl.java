package uz.uzkassa.cts.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.constants.CacheConstants;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.domain.address.District;
import uz.uzkassa.cts.domain.address.Neighborhood;
import uz.uzkassa.cts.domain.address.Region;
import uz.uzkassa.cts.domain.hrms.Position;
import uz.uzkassa.cts.domain.permission.Authority;
import uz.uzkassa.cts.dto.payload.HistoryPayload;
import uz.uzkassa.cts.enums.Action;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqHistoryProducer;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.CacheService;
import uz.uzkassa.cts.service.LogHistoryService;

import java.util.List;

@Transactional
@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class LogHistoryServiceImpl implements LogHistoryService {

    RabbitMqHistoryProducer rabbitMqHistoryProducer;

    @Override
    public void logHistory(String serialNumber, boolean isFM, Action action, String fromWarehouse, String toWarehouse, String fromValue, String toValue) {
        HistoryPayload historyPayload = new HistoryPayload.HistoryBuilder(serialNumber, isFM, action.name(), SecurityUtils.getCurrentUserName(), SecurityUtils.getCurrentUserId())
                .setFromWarehouse(fromWarehouse)
                .setToWarehouse(toWarehouse)
                .setFromValue(fromValue)
                .setToValue(toValue)
                .build();
        rabbitMqHistoryProducer.create(historyPayload);
    }

    @Override
    public void logHistory(String serialNumber, boolean isFM, Action action, String from, String to) {
        this.logHistory(serialNumber, isFM, action, from, to, null, null);
    }
}
