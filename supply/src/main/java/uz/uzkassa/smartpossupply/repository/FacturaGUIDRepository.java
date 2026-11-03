package uz.uzkassa.smartpossupply.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.factura.FacturaGUID;

import java.util.Optional;

@Repository
public interface FacturaGUIDRepository extends BaseRepository<FacturaGUID, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
    Optional<FacturaGUID> findFirstByFacturaIdIsNotNullAndUsedIsFalse();
}
