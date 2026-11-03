package uz.uzkassa.cts.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.constants.CacheConstants;
import uz.uzkassa.cts.domain.Bank;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.repository.custom.BankRepositoryCustom;

import java.util.Optional;

@Repository
public interface BankRepository extends BaseRepository<Bank, Long>, BankRepositoryCustom {

    @Cacheable(cacheNames = CacheConstants.BANK_ENTITY_BY_CODE_CACHE)
    Optional<Bank> findByCode(String code);

    Optional<Bank> findByCodeAndStatus(String code, CommonStatus status);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);
}
