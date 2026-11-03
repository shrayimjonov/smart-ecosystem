package uz.uzkassa.cts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.Ledger;
import uz.uzkassa.cts.domain.LedgerDetail;
import uz.uzkassa.cts.repository.custom.LedgerRepositoryCustom;

import java.util.List;


@Repository
public interface LedgerRepository extends BaseRepository<Ledger, Long>, LedgerRepositoryCustom {

    @Query(value = """
            SELECT c.* FROM public.ledger_detail c
            JOIN (SELECT tin, MAX(id) as MaxPID FROM public.ledger_detail GROUP BY tin) pu
            ON c.id = pu.MaxPID""", nativeQuery = true)
    Page<LedgerDetail> getBalance(Pageable pageable);

    @Query(value = """
            SELECT c.id, c.created_date, c.last_modified_date, c.deleted,\
             c.account_number, c.balance, c.credit, c.customer_name, c.debit, \
            c.link, c.payment_date, c.tin, c.transaction_number FROM public.ledger_detail c
            JOIN (SELECT ld.tin, MAX(ld.id) as MaxPID FROM public.ledger_detail ld GROUP BY ld.tin) pu
            ON c.id = pu.MaxPID""", nativeQuery = true)
    List<LedgerDetail> getBalance();

    Ledger findByReferenceTo(String referenceTo);


}
