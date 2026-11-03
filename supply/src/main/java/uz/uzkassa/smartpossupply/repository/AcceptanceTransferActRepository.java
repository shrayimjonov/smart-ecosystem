package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.acceptancetransferact.AcceptanceTransferAct;

import java.util.Optional;

@Repository
public interface AcceptanceTransferActRepository extends BaseRepository<AcceptanceTransferAct, Long>, AcceptanceTransferActRepositoryCustom {

    Optional<AcceptanceTransferAct> findFirstBySilenId(String silenId);

    Optional<AcceptanceTransferAct> findFirstByStockOutId(Long stockOutId);

    @Query("SELECT coalesce(max(aa.incrementNumber), 0) FROM AcceptanceTransferAct aa where aa.companyId=:companyId")
    Long getMaxIncrementNumber(@Param("companyId") Long companyId);

    @Query("select a from AcceptanceTransferAct a where a.id=:id")
    @EntityGraph(attributePaths = {"branch", "seller", "buyer", "acceptanceTransferActDetail", "acceptanceTransferActDetail.products"})
    Optional<AcceptanceTransferAct> getDetail(@Param("id") Long id);
}
