package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.waybill.Waybill;
import uz.uzkassa.smartpossupply.enums.WaybillStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface WaybillRepository extends BaseRepository<Waybill, Long>, WaybillRepositoryCustom {

    Optional<Waybill> findFirstByWaybillLocalId(String waybillLocalId);

    @Query("select s from Waybill s where s.id=:id")
    @EntityGraph(attributePaths = {"factura", "shipment", "stockTransfer", "executors"})
    Optional<Waybill> getDetail(@Param("id") Long id);

    @Query("select w.id from Waybill w where w.status=:status and w.consignorTinOrPinfl=:consignorTinOrPinfl and w.deleted = false")
    List<Long> findAllIdsByStatusAndConsignorTinOrPinfl(@Param("status") WaybillStatus status, @Param("consignorTinOrPinfl") String consignorTinOrPinfl);
}
