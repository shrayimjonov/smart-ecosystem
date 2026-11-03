package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.transaction.TransactionItem;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 07.02.2023 19:33
 */
@Repository
public interface TransactionItemRepository extends BaseRepository<TransactionItem, Long> {
}
