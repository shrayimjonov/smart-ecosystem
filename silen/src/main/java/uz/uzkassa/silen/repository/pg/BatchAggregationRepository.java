package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.BatchAggregation;
import uz.uzkassa.silen.enumeration.BatchAggregationStatus;
import uz.uzkassa.silen.enumeration.ReportStatus;

import java.util.Optional;

/**
 * Spring Data  repository for the BatchAggregation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BatchAggregationRepository extends JpaRepository<BatchAggregation, String>, BatchAggregationRepositoryCustom {
    @Query("select max(intNumber) from BatchAggregation where organizationId=?1")
    Integer getMaxIntNumber(String organizationId);


    Optional<BatchAggregation> findFirstByOrganizationIdAndStatusOrderByCreatedDateDesc(String organizationId, BatchAggregationStatus status);
}
