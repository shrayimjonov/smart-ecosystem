package uz.uzkassa.horeca.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import uz.uzkassa.horeca.constants.CacheConstants;
import uz.uzkassa.horeca.domain.factura.FacturaHost;

import java.util.Optional;

public interface FacturaHostRepository extends BaseRepository<FacturaHost, Long> {
    @Cacheable(cacheNames = CacheConstants.FACTURA_ACTIVE_HOST_CACHE)
    Optional<FacturaHost> findFirstByActiveIsTrue();

    @Modifying
    @Query("update FacturaHost set active=false where 1=1")
    void updateAll();
}
