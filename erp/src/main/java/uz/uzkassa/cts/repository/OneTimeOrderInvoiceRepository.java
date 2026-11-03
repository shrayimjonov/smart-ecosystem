package uz.uzkassa.cts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.OneTimeOrderInvoice;
import uz.uzkassa.cts.repository.custom.OneTimeOrderInvoiceRepositoryCustom;

@Repository
public interface OneTimeOrderInvoiceRepository extends BaseRepository<OneTimeOrderInvoice, Long>, OneTimeOrderInvoiceRepositoryCustom {

    Page<OneTimeOrderInvoice> findByContractIdOrderByIdDesc(Long contractId, Pageable pageable);

    OneTimeOrderInvoice findByxFileId(String xFileId);

    @Modifying
    @Query(value = "update OneTimeOrderInvoice oi set oi.xFileId = :xFileId where oi.id =:id")
    void updateXFileId(@Param("id") Long id,
                       @Param("xFileId") String xFileId);
}
