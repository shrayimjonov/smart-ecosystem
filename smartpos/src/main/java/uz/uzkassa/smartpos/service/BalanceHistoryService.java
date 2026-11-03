package uz.uzkassa.smartpos.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpos.repository.postgres.common.CityRepository;
import uz.uzkassa.smartpos.repository.postgres.warehouse.BalanceHistoryRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceHistoryService {
    private final BalanceHistoryRepository balanceHistoryRepository;
    private final CityRepository cityRepository;

    @Transactional
    public void cleanup() {
        cityRepository.findAll(Sort.by("id")).forEach(city -> {
            log.info("==================================================================================");
            log.info("==================================================================================");
            log.info("Balance History cleanup started. City: {}, {}", city.getId(), city.getNameRu());
            try {
                balanceHistoryRepository.cleanup(city.getId());
            } catch (Exception e) {
                log.error("Balance History cleanup failed. City: {}, {}", city.getId(), city.getNameRu());
            }
            log.info("Balance History cleanup finished");
            log.info("==================================================================================");
            log.info("==================================================================================");
        });
    }
}
