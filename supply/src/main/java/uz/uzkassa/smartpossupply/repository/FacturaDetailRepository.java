package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.factura.FacturaDetail;

import java.util.Optional;

@Repository
public interface FacturaDetailRepository extends BaseRepository<FacturaDetail, Long>, FacturaDetailRepositoryCustom {

    boolean existsByFacturaProductId(String facturaProductId);

    boolean existsByAgentFacturaId(String agentFacturaId);

    Optional<FacturaDetail> findFirstByFactura_Id(Long facturaId);

    @Modifying
    @Query("update FacturaDetail set sellerVatRegCode=:vatRegCode, sellerVatRegStatus=:vatRegStatus where factura.id=:facturaId")
    void updateSellerVatRegistration(@Param("facturaId") Long facturaId, @Param("vatRegCode") String vatRegCode, @Param("vatRegStatus") Integer vatRegStatus);

    @Modifying
    @Query("update FacturaDetail set buyerVatRegCode=:vatRegCode, buyerVatRegStatus=:vatRegStatus where factura.id=:facturaId")
    void updateBuyerVatRegistration(@Param("facturaId") Long facturaId, @Param("vatRegCode") String vatRegCode, @Param("vatRegStatus") Integer vatRegStatus);
}
