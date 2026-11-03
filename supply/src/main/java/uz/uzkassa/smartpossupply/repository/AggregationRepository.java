package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.aggregation.Aggregation;
import uz.uzkassa.smartpossupply.enums.AggregationStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface AggregationRepository extends BaseRepository<Aggregation, Long>, AggregationRepositoryCustom {

    List<Aggregation> findAllByParentCode(String parentCode);

    List<Aggregation> findAllByPartyProductId(Long partyProductId);

    Optional<Aggregation> findFirstByCode(String code);

    @Query("select a.code from Aggregation a where a.code in(:codes) and a.status=:status")
    List<String> findAllByCodesInAndStatusIs(@Param("codes") List<String> codes, @Param("status") AggregationStatus aggregationStatus);

    @Query("select a.code from Aggregation a where a.parentCode=:parentCode")
    Set<String> findAllCodesByParentCode(@Param("parentCode") String parentCode);

    @Modifying
    @Query("update Aggregation set partyProductId=:partyProductId, parentCode=:parentCode where code in(:codes)")
    void updateAllSetPartyProductIdAndParentCodeWhereAggregationCodeSetIn(@Param("partyProductId") Long partyProductId, @Param("parentCode") String parentCode, @Param("codes") Set<String> aggregationCodes);

    @Modifying
    @Query("update Aggregation set partyProductId=null where partyProductId=:partyProductId")
    void unsetAllPartyProductIdByPartyProductId(@Param("partyProductId") Long partyProductId);

    @Modifying
    @Query("update Aggregation set partyProductId=null where id=:id")
    void unsetAggregationPartyProductIdById(@Param("id") Long aggregationId);

    @Modifying
    @Query("update Aggregation set status=:status, aggregationDate=:aggregationDate, reportId=:turonAggregationId where partyProductId=:partyProductId")
    void updateAllStatusAndAggregatedDateByPartyProductId(@Param("status") AggregationStatus status, @Param("aggregationDate") LocalDateTime aggregationDate, @Param("turonAggregationId") String turonAggregationId, @Param("partyProductId") Long groupProductId);

    @Query("select a.originalCode from Aggregation a where a.partyProductId=:partyProductId")
    Set<String> findAllPrintCodesByPartyProductId(@Param("partyProductId") Long partyProductId);

    @Query("select a.code from Aggregation a where a.partyProductId in (:partyProductId)")
    Set<String> findAllCodesByPartyProductIdIn(@Param("partyProductId") List<Long> partyProductIdList);

    @Query("select a.code from Aggregation a where a.partyProductId =:partyProductId")
    Set<String> findAllCodesByPartyProductId(@Param("partyProductId") Long partyProductId);

    List<Aggregation> findAllByCodeIn(Collection<String> codes);

    @Query("select count(id) from Aggregation where aggregationOrderId=:aggregationOrderId and status <>:status")
    Long countByOrderId(@Param("aggregationOrderId") Long aggregationOrderId, @Param("status") AggregationStatus status);
}
