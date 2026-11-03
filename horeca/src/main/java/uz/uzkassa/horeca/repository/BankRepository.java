package uz.uzkassa.horeca.repository;

import org.springframework.cache.annotation.Cacheable;
import uz.uzkassa.horeca.constants.CacheConstants;
import uz.uzkassa.horeca.domain.Bank;

import java.util.Optional;

/**
 * Date 10.10.2022 13:55
 */
public interface BankRepository extends BaseRepository<Bank, Long>, BankRepositoryCustom {
    @Cacheable(cacheNames = CacheConstants.BANK_BY_MFO_CACHE)
    Optional<Bank> findFirstByMfo(String mfo);
}
