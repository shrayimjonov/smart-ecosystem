package uz.uzkassa.smartpos.repository.postgres.sales;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.postgres.sales.Transaction;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction>, TransactionRepositoryCustom {
    Optional<Transaction> findByUid(String uid);
    List<Transaction> findAllByUidIn(Collection<String> uids);
}
