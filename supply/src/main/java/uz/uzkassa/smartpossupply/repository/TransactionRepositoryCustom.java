package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.transaction.Transaction;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.TransactionFilter;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 08.02.2023 11:51
 */
@Repository
public interface TransactionRepositoryCustom {
    ResultList<Transaction> getResultList(TransactionFilter filter);
}
