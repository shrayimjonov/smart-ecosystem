package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.planroutetemplate.PlanRouteTemplate;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.CompanyStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRouteTemplateRepository extends BaseRepository<PlanRouteTemplate, Long>, PlanRouteTemplateRepositoryCustom {
    @Query("SELECT coalesce(max(prt.incrementNumber),0) from PlanRouteTemplate prt join prt.agent a join a.company c where c.id=:companyId")
    Long getMaxIncrementNumber(@Param("companyId") Long companyId);

    @Query("select prt.id from PlanRouteTemplate prt join prt.agent a join a.company c where prt.deleted=false and c.deleted = false and prt.status=:status and c.status=:companyStatus")
    List<Long> getIdsByStatus(@Param("status") CommonStatus status, @Param("companyStatus") CompanyStatus companyStatus);

    boolean existsByAgentIdAndStatusAndDeletedIsFalse(Long agentId, CommonStatus status);

    boolean existsByAgentIdAndStatusAndIdNotAndDeletedIsFalse(Long agentId, CommonStatus status, Long id);

    @Query("select distinct pr.template from PlanRouteExpeditor pre join pre.planRoute pr where pre.expeditorId=:expeditorId")
    List<PlanRouteTemplate> getPlanRouteTemplates(@Param("expeditorId") Long expeditorId);

    @Query("""
            SELECT prt
              FROM PlanRouteTemplate prt
              JOIN SupervisorEmployee se ON prt.agentId = se.employeeId
              LEFT JOIN FETCH prt.agent
              WHERE se.supervisorId = :supervisorId
                AND prt.deleted = false
           """)
    Page<PlanRouteTemplate> getPlanRouteTemplateBySupervisorId(Long supervisorId, Pageable pageable);

    @EntityGraph(attributePaths = {"agent", "branch", "region", "items"})
    @Query("select prt from PlanRouteTemplate prt where prt.id=:id")
    Optional<PlanRouteTemplate> getDetail(@Param("id") Long id);
}
