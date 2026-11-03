package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.uzkassa.silen.domain.FacturaHost;

import java.util.Optional;

public interface FacturaHostRepository extends JpaRepository<FacturaHost, Long> {

    Optional<FacturaHost> findFirstByActiveIsTrue();
}
