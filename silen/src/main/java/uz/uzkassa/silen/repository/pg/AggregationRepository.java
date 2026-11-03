package uz.uzkassa.silen.repository.pg;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.Aggregation;
import uz.uzkassa.silen.enumeration.AggregationStatus;
import uz.uzkassa.silen.enumeration.ProductType;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Spring Data  repository for the Aggregation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AggregationRepository extends JpaRepository<Aggregation, String>, AggregationRepositoryCustom {

    Optional<Aggregation> findFirstByCode(String code);

    Optional<Aggregation> findFirstByCodeOrCode(String code18,String code20);

    List<Aggregation> findAllByPartyAggregationId(String partyAggregationId);

    List<Aggregation> findAllByPartyAggregationIdOrderByCreatedDateAsc(String partyAggregationId);

    Long countAllByPartyAggregationIdAndStatusIn(String partyAggregationId, Collection<AggregationStatus> status);

    List<Aggregation> findAllByCodeInAndStatusIn(Collection<String> codes, List<AggregationStatus> status);

    List<Aggregation> findAllByCodeIn(Collection<String> codes);

    @Query("select a from Aggregation a where a.code in (:codes) or a.parentCode in (:codes)")
    List<Aggregation> findAllByCodeInOrParentCodeIn(@Param("codes") Set<String> codes);

    @Query("select a.organizationId from Aggregation a where a.status in (:statuses) and a.product.type in (:types) group by a.organizationId")
    List<String> findForResend(@Param("statuses") EnumSet<AggregationStatus> statuses, @Param("types") EnumSet<ProductType> types);

    @Query("select o.code from Aggregation o where o.parentCode=:parentCode18 or o.parentCode=:parentCode20")
    List<String> findAllCodeByParentCode(@Param("parentCode18") String parentCode18, @Param("parentCode20") String parentCode20);


    @Query("select o.code from Aggregation o where o.parentCode=:parentCode")
    List<String> findAllCodeByParentCode(@Param("parentCode") String parentCode);

    @Query("select o.printCode from Aggregation o where o.parentCode=:parentCode")
    Set<String> findAllPrintCodeByParentCode(@Param("parentCode") String parentCode);

    List<Aggregation> findAllByParentCode(String code);

    @Query("select sum(o.quantity) from Aggregation o where o.parentCode=:parentCode")
    Long findAllQuantitySumByParentCode(@Param("parentCode") String code);

    List<Aggregation> findAllByParentCodeIn(Collection<String> code);

    List<Aggregation> findAllByUnitAndCodeIn(Integer unit, Set<String> codes);

    boolean existsAggregationByProductId(String productId);

    List<Aggregation> findAllByOrganizationIdAndStatusInAndAggregationDateAfter(String organizationId, EnumSet<AggregationStatus> status, LocalDateTime lastModifiedDate);

    Page<Aggregation> findAllByStatusInAndLastModifiedDateAfterOrderByAggregationDateDesc(EnumSet<AggregationStatus> status, LocalDateTime aggregationDate, Pageable forLimit);

    Page<Aggregation> findAllByStatusAndLastModifiedDateAfterOrderByLastModifiedDateDesc(AggregationStatus status, LocalDateTime aggregationDate, Pageable forLimit);

    @Query("select o from Aggregation o where o.status=:status and o.organization.deleted=false")
    List<Aggregation> findAllByStatusAndOrganizationNotDeleted(@Param("status") AggregationStatus status);

    List<Aggregation> findAllBySerialIdAndStatusAndDeletedIsFalse(String serialNumberId, AggregationStatus status);

    List<Aggregation> findAllByUtilisationIdIn(Set<String> reportIdSet);

    @Query("select o from Aggregation o where o.status=:status and o.utilisationId is not null and o.organization.deleted=false")
    List<Aggregation> findAllByStatusErrorAndOrganizationNotDeletedAndUtilisationIdNotEmpty(@Param("status") AggregationStatus status);

    @Modifying
    @Query("update Aggregation set parentCode=:parentCode where code in (:codes)")
    void updateParentCode(@Param("codes") Set<String> codes, @Param("parentCode") String parentCode);

    @Modifying
    @Query("update Aggregation set code=concat('00',code) where code=:code and status='DRAFT'")
    void updateCodeFillWithZero(@Param("code") String code);

    @Modifying
    @Query("update Aggregation set parentCode=null where parentCode=:parentCode")
    void unsetParentCode(@Param("parentCode") String parentCode);


    @Modifying
    @Query("update Aggregation set quantity= quantity-:amount where code=:code")
    void decrementQuantity(@Param("code") String code, @Param("amount") Integer amount);

    @Query("select coalesce(sum(o.quantity),0) from Aggregation o where o.parentCode=:parentCode")
    Long countByParentCode(@Param("parentCode") String parentCode);


    @Query(value = "select nextval(:organizationGcp)", nativeQuery = true)
    Long nextVal(@Param("organizationGcp") String organizationGcp);

    List<Aggregation> findAllByStatusOrderByLastModifiedDateDesc(AggregationStatus status);

    Page<Aggregation> findAllByStatusAndLastModifiedDateIsBeforeOrderByOrganizationId(AggregationStatus status, LocalDateTime lastModifiedDate, Pageable pageable);

    List<Aggregation> findAllByUnitAndStatusNotAndProductionDateBetween(int unit, AggregationStatus status, LocalDateTime productionDate, LocalDateTime productionDate2);
}
