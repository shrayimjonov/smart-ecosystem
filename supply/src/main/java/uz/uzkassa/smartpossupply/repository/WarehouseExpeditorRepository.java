package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.warehouse.WarehouseExpeditor;

import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 08.11.2024
 */
@Repository
public interface WarehouseExpeditorRepository extends BaseRepository<WarehouseExpeditor, Long>, WarehouseExpeditorRepositoryCustom {

    Optional<WarehouseExpeditor> findFirstByExpeditorIdAndWarehouseIdOrderByTransferDateDesc(Long expeditorId, Long warehouseId);
}