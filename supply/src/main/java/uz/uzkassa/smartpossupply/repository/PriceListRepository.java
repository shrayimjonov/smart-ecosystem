package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.pricelist.PriceList;
import uz.uzkassa.smartpossupply.enums.CommonStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 04.02.2023 13:44
 */
@Repository
public interface PriceListRepository extends BaseRepository<PriceList, Long>, PriceListRepositoryCustom {

    @Query("SELECT coalesce(max(p.incrementNumber), 0) FROM PriceList p where p.branch.companyId=:companyId")
    Long getMaxIncrementNumber(@Param("companyId") Long companyId);

    @Modifying
    @Query("update PriceList set status=:toStatus where expireDate > :expireDate and status=:fromStatus")
    void updateStatus(@Param("fromStatus") CommonStatus fromStatus,
                      @Param("toStatus") CommonStatus toStatus,
                      @Param("expireDate") LocalDate expireDate);

    @EntityGraph(attributePaths = {"branch", "items"})
    @Query("SELECT pl FROM PriceList pl WHERE pl.id =:id")
    Optional<PriceList> getDetail(@Param("id") Long id);

    @Query(value = "SELECT plcg.price_list_id FROM price_list_customer_groups plcg " +
            "JOIN price_list pl ON pl.id = plcg.price_list_id " +
            "WHERE plcg.customer_group_id = :groupId AND pl.deleted = false", nativeQuery = true)
    List<Long> findIdsByCustomerGroupId(@Param("groupId") Long groupId);


    @Query("SELECT DISTINCT c.id FROM PriceList pl JOIN pl.customerGroups g JOIN g.customers c " +
            "WHERE pl.id = :priceListId AND g.status = :activeStatus")
    Set<Long> findActiveGroupMemberIds(@Param("priceListId") Long priceListId, @Param("activeStatus") CommonStatus activeStatus);
}
