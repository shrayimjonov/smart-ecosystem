package uz.uzkassa.smartpossupply.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.Bank;

import java.util.Optional;

@Repository
public interface BankRepository extends BaseRepository<Bank, Long>, BankRepositoryCustom {
    @EntityGraph(attributePaths = {"parent"})
    @Cacheable(cacheNames = BANK_BY_MFO_CACHE)
    Optional<Bank> findByMfo(String mfo);
}
