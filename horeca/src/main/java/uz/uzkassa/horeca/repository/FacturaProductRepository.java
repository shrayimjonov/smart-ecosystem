package uz.uzkassa.horeca.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.uzkassa.horeca.domain.factura.FacturaProduct;

public interface FacturaProductRepository extends BaseRepository<FacturaProduct, Long>, FacturaProductRepositoryCustom {
    @Modifying
    @Query("update FacturaProduct fp set fp.deleted=true where fp.facturaDetailId=:facturaDetailId")
    void deleteAllByFacturaDetailId(@Param("facturaDetailId") Long facturaDetailId);
}
