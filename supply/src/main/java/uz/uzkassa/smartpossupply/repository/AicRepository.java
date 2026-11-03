package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.aic.Aic;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface AicRepository extends BaseRepository<Aic, Long>, AicRepositoryCustom {

    Optional<Aic> findFirstByReportIdAndDeletedFalse(String aicReportId);

    @Query(value = "select code from aic_codes where code in (:codes)", nativeQuery = true)
    Set<String> findAllByCodesIn(@Param("codes") Set<String> codes);

    @Query(value = "select code from aic_codes where code in (:codes)", nativeQuery = true)
    List<String> findAllByCodesIn(@Param("codes") List<String> codes);
}
