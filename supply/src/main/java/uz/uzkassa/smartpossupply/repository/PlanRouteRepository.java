package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.planroute.PlanRoute;
import uz.uzkassa.smartpossupply.enums.PlanRouteStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 20.01.2023 10:36
 */
@Repository
public interface PlanRouteRepository extends BaseRepository<PlanRoute, Long>, PlanRouteRepositoryCustom {

    @Query("select pr from PlanRoute pr where pr.id=:id")
    //@EntityGraph(attributePaths = {"agent", "region", "template", "items"})
    Optional<PlanRoute> getDetail(@Param("id") Long id);

    @Query("SELECT coalesce(max(p.incrementNumber),0) from PlanRoute p where p.companyId=:companyId")
    Long getMaxIncrementNumber(@Param("companyId") Long companyId);

    boolean existsByAgentIdAndStatusAndDeletedIsFalse(Long agentId, PlanRouteStatus status);

    boolean existsByAgentIdAndStatusAndDeletedIsFalseAndIdNot(Long agentId, PlanRouteStatus status, Long id);

//    @EntityGraph(attributePaths = {"items"})
    Optional<PlanRoute> findFirstByAgentIdAndStatusOrderByCreatedDateDesc(Long agentId, PlanRouteStatus status);

    //@EntityGraph(attributePaths = {"items"})
    List<PlanRoute> findAllByStatusAndToDateIsBefore(PlanRouteStatus status, LocalDate toDate);

    //@EntityGraph(attributePaths = {"agent", "region", "template", "items"})
    Optional<PlanRoute> findFirstByTemplateIdAndStatusAndDeletedIsFalseOrderByIdDesc(Long templateId, PlanRouteStatus status);
}
