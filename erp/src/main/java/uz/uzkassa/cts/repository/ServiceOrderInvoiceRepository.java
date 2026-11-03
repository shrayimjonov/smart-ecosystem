package uz.uzkassa.cts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.ServiceOrderInvoice;
import uz.uzkassa.cts.repository.custom.ServiceOrderInvoiceRepositoryCustom;

@Repository
public interface ServiceOrderInvoiceRepository extends BaseRepository<ServiceOrderInvoice, Long>, ServiceOrderInvoiceRepositoryCustom {

    Page<ServiceOrderInvoice> findByContractIdOrderByIdDesc(Long contractId, Pageable pageable);

    ServiceOrderInvoice findByxFileId(String xFileId);

    @EntityGraph(attributePaths = {"contract", "contract.customer"})
    Page<ServiceOrderInvoice> findAll(@Nullable Specification<ServiceOrderInvoice> spec, Pageable pageable);
}
