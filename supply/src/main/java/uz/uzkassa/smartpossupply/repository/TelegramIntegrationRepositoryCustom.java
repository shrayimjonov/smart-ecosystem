package uz.uzkassa.smartpossupply.repository;

import uz.uzkassa.smartpossupply.domain.telegram.TelegramIntegration;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.BaseFilter;

public interface TelegramIntegrationRepositoryCustom {
    ResultList<TelegramIntegration> getResultList(BaseFilter filter);
}
