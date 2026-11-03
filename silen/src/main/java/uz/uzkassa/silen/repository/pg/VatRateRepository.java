package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.VatRate;

import java.util.Optional;

@Repository
public interface VatRateRepository extends JpaRepository<VatRate, Long> {

    Optional<VatRate> findFirstByCodeAndDeletedIsFalse(String code);

}
