package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.config.CacheConstants;
import uz.uzkassa.silen.domain.Dropout;
import uz.uzkassa.silen.enumeration.CodeType;

import java.util.List;
import java.util.Optional;

@Repository
public interface DropoutRepository extends JpaRepository<Dropout, String>, DropoutRepositoryCustom, CacheConstants {

    Optional<Dropout> findFirstByCode(String code);

    @Query("select printCode from Dropout where codeType=:codeType and organizationId=:organizationId")
    List<String> findAllAggregationsByOrg(@Param("codeType") CodeType codeType, @Param("organizationId") String organizationId);

    @Query("select distinct(organizationId) from Dropout where codeType=:codeType")
    List<String> findAllAggregationsOrg(@Param("codeType") CodeType codeType);

    List<Dropout> findAllByReportId(String reportId);
}
