package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.aggregation.AggregationBlock;
import uz.uzkassa.smartpossupply.enums.AggregationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface AggregationBlockRepository extends BaseRepository<AggregationBlock, Long>, AggregationBlockRepositoryCustom {

    @Query("select ab from AggregationBlock ab where ab.id=:id")
    @EntityGraph(attributePaths = {"product"})
    Optional<AggregationBlock> getDetail(@Param("id") Long id);

    Optional<AggregationBlock> findFirstByCode(String code);

    List<AggregationBlock> findAllByPartyProductId(Long partyProductId);

    @Query("select a.printCode from AggregationBlock a where a.parentCode=:parentCode")
    Set<String> findAllPrintCodesByParentCode(@Param("parentCode") String parentCode);

    @Modifying
    @Query("update AggregationBlock set partyProductId=null where partyProductId=:partyProductId")
    void unsetAllPartyProductIdByPartyProductId(@Param("partyProductId") Long partyProductId);

    @Modifying
    @Query("update AggregationBlock set partyProductId=null where id=:id")
    void unsetPartyProductIdById(@Param("id") Long aggregationBlockId);

    @Modifying
    @Query("update AggregationBlock set status=:status, aggregationDate=:aggregationDate, reportId=:turonAggregationId where partyProductId=:partyProductId")
    void updateAllStatusAndAggregatedDateByPartyProductId(@Param("status") AggregationStatus status, @Param("aggregationDate") LocalDateTime aggregationDate, @Param("turonAggregationId") String turonAggregationId, @Param("partyProductId") Long groupProductId);

    @Query("select b.printCode from AggregationBlock b where b.partyProductId=:partyProductId")
    Set<String> findAllPrintCodesByPartyProductId(@Param("partyProductId") Long partyProductId);

    @Query("select b.printCode from AggregationBlock b where b.markOrderPartyId=:markOrderPartyId")
    Set<String> findAllPrintCodesByMarkOrderPartyId(@Param("markOrderPartyId") Long markOrderPartyId);

    @Query("select b.code from AggregationBlock b where b.markOrderPartyId=:markOrderPartyId")
    Set<String> findAllCodesByMarkOrderPartyId(@Param("markOrderPartyId") Long markOrderPartyId);

    @Query("select a.code from AggregationBlock a where a.partyProductId in (:partyProductId)")
    Set<String> findAllCodesByPartyProductIdIn(@Param("partyProductId") List<Long> partyProductIdList);

    @Query("select a.code from AggregationBlock a where a.partyProductId = :partyProductId")
    Set<String> findAllCodesByPartyProductId(@Param("partyProductId") Long partyProductId);
}
