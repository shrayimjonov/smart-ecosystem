package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.planroute.PlanRouteCustomerBranch;
import uz.uzkassa.smartpossupply.enums.PlanRouteCustomerBranchStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 02.02.2023 12:03
 */
@Repository
public interface PlanRouteCustomerBranchRepository extends BaseRepository<PlanRouteCustomerBranch, Long>, PlanRouteCustomerBranchRepositoryCustom {

    Optional<PlanRouteCustomerBranch> findByIdAndDeletedIsFalse(Long id);

    boolean existsByPlanRouteItemIdAndCustomerBranchIdAndPlanRouteItem_VisitPlanDateAndDeletedIsFalse(Long planRouteItemId, Long customerBranchId, LocalDate visitPlanDate);

    boolean existsByPlanRouteItemIdAndCustomerBranchIdAndDeletedIsFalse(Long planRouteItemId, Long customerBranchId);

    @Modifying
    @Query("update PlanRouteCustomerBranch set deleted = true where planRouteItemId=:planRouteItemId")
    void deleteAllByPlanRouteItemId(@Param("planRouteItemId") Long planRouteItemId);

    @Modifying
    @Query("update PlanRouteCustomerBranch prcb set prcb.deleted = true where prcb.planRouteItemId=:planRouteItemId and prcb.customerBranchId=:customerBranchId and prcb.unscheduled=:unscheduled")
    void deleteByPlanRouteItemIdAndCustomerBranchIdAndUnscheduled(@Param("planRouteItemId") Long planRouteItemId, @Param("customerBranchId") Long customerBranchId, @Param("unscheduled") boolean unscheduled);

    @Modifying
    @Transactional
    @Query(" update PlanRouteCustomerBranch set status=:toStatus where deleted=false and  planRouteItem.visitPlanDate> :visitPlanDate and status=:fromStatus")
    void updateStatus(@Param("fromStatus") PlanRouteCustomerBranchStatus fromStatus,
                      @Param("toStatus") PlanRouteCustomerBranchStatus toStatus,
                      @Param("visitPlanDate") LocalDate visitPlanDate);

    @EntityGraph(attributePaths = {"order", "planRouteItem"})
    Page<PlanRouteCustomerBranch> findAllByCustomerBranchId(@Param("customerBranchId") Long customerBranchId, Pageable pageable);

    @Query("""
            SELECT prcb FROM PlanRouteCustomerBranch prcb
                        JOIN PlanRouteItem pri on pri.id = prcb.planRouteItemId
                        JOIN PlanRoute pr on pr.id = pri.planRouteId
            WHERE pr.agentId = :agentId
                        AND prcb.status = 'VISITED'
                        AND prcb.startDate BETWEEN :from AND :to
            """)
    @EntityGraph(attributePaths = {"customerBranch"})
    List<PlanRouteCustomerBranch> findAllByAgentId(Long agentId, LocalDateTime from, LocalDateTime to);

    Optional<PlanRouteCustomerBranch> findFirstByPlanRouteItemIdAndCustomerBranchIdAndDeletedIsFalse(Long planRouteItemId, Long customerBranchId);
}
