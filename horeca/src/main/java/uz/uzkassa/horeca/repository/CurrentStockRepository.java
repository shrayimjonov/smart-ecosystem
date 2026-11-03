package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.stock.CurrentStock;
import uz.uzkassa.horeca.enums.CurrentStockStatus;

import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 10.11.2024
 */

public interface CurrentStockRepository extends BaseRepository<CurrentStock, Long>, CurrentStockRepositoryCustom {

    Optional<CurrentStock> findFirstByMenuItemIdAndStatusAndDeletedIsFalse(Long menuItemId, CurrentStockStatus status);

    Optional<CurrentStock>findFirstByMenuItemIdAndDeletedIsFalseOrderByCreatedDateDesc(Long menuItemId);
}
