package uz.uzkassa.cts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.OneTimeOrder;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.repository.custom.OneTimeOrderRepositoryCustom;

import java.util.List;

@Repository
public interface OneTimeOrderRepository extends BaseRepository<OneTimeOrder, Long>, OneTimeOrderRepositoryCustom {

    List<OneTimeOrder> findByInvoiceIdIsNullAndStatusOrderByIdDesc(ContractStatus status);

    Page<OneTimeOrder> findByContractIdOrderByIdDesc(Long contractId, Pageable pageable);

    @EntityGraph(attributePaths = {"customer", "service", "contract", "customer.branch", "invoice"})
    Page<OneTimeOrder> findAll(Specification specification, Pageable pageable);
}
