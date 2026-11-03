package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.mark.Mark;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface MarkRepository extends BaseRepository<Mark, Long>, MarkRepositoryCustom {
    @Query("select m.code from Mark m where m.code in(:codes) and m.used is true ")
    List<String> findAllByCodeInAndUsedIsTrue(@Param("codes") List<String> codes);

    Optional<Mark> findFirstByCode(String code);

    Optional<Mark> findFirstByParentCode(String parentCode);

    @Query("select m.code from Mark m where m.parentCode=:parentCode")
    List<String> findAllCodesByParentCode(@Param("parentCode") String parentCode);

    @Query("select m.code from Mark m where m.parentCode=:parentCode order by m.createdDate desc")
    List<String> findAllCodesByParentCodeOrderByCreatedDateDesc(@Param("parentCode") String parentCode);

    List<Mark> findAllByParentCodeOrderByCreatedDateDesc(String parentCode);

    @Modifying
    @Query("update Mark set partyProductId=null where partyProductId=:partyProductId")
    void unsetAllPartyProductIdByPartyProductId(@Param("partyProductId") Long partyProductId);

    @Modifying
    @Query("update Mark set partyProductId=null where id=:id")
    void unsetPartyProductIdById(@Param("id") Long markId);

    @Query("select m.originalCode from Mark m where m.partyProductId=:partyProductId")
    Set<String> findAllPrintCodesByPartyProductId(@Param("partyProductId") Long partyProductId);

    @Query("select m.originalCode from Mark m where m.markOrderPartyId=:markOrderPartyId")
    Set<String> findAllPrintCodesByMarkOrderPartyId(@Param("markOrderPartyId") Long markOrderPartyId);

    @Modifying
    @Query("update Mark set partyProductId=:partyProductId where parentCode=:parentCode")
    void updateSetPartyProductIdByParentCode(@Param("partyProductId") Long partyProductId, @Param("parentCode") String parentCode);

    Long countAllByPartyProductId(Long id);

    @Query("select a.code from Mark a where a.partyProductId in (:partyProductId)")
    Set<String> findAllCodesByPartyProductIdIn(@Param("partyProductId") List<Long> partyProductIdList);

    List<Mark> findAllByMarkOrderPartyId(Long markOrderPartyId);
}
