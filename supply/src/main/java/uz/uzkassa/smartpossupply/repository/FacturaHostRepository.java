package uz.uzkassa.smartpossupply.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.factura.FacturaHost;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacturaHostRepository extends BaseRepository<FacturaHost, Long> {

    @Cacheable(cacheNames = FACTURA_ACTIVE_HOST_CACHE)
    Optional<FacturaHost> findFirstByActiveIsTrue();

    @Modifying
    @Query("update FacturaHost set active=false where 1=1")
    void updateAll();

    @Query("select fh from FacturaHost fh order by fh.id")
    List<FacturaHost> findAllOrderById();
}
