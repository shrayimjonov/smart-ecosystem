package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.factura.FacturaProduct;

@Repository
public interface FacturaProductRepository extends BaseRepository<FacturaProduct, Long>, FacturaProductRepositoryCustom {

    @Modifying
    @Query("update FacturaProduct fp set fp.deleted=true where fp.facturaDetail.id=:facturaDetailId")
    void deleteAllByFacturaDetailId(@Param("facturaDetailId") Long facturaDetailId);
}
