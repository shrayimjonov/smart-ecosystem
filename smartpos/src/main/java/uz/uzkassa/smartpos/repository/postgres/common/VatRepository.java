package uz.uzkassa.smartpos.repository.postgres.common;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.uzkassa.smartpos.domain.postgres.common.Vat;

import java.math.BigDecimal;
import java.util.Optional;


public interface VatRepository extends JpaRepository<Vat, Long> {
    Optional<Vat> findDistinctByNameIgnoreCaseAndPercent(String name, BigDecimal percent);
}
