package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.planroute.PlanRouteExpeditor;
import uz.uzkassa.smartpossupply.enums.PlanRouteStatus;

import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 15.01.2025
 */
@Repository
public interface PlanRouteExpeditorRepository extends BaseRepository<PlanRouteExpeditor, Long> {

    @Query("select pre.planRouteId from PlanRouteExpeditor  pre where pre.expeditorId=:expeditorId and pre.planRoute.deleted=false and pre.planRoute.status=:status")
    List<Long> findAllByExpeditorIdAndStatusAndDeletedIsFalse(@Param("expeditorId") Long expeditorId, @Param("status") PlanRouteStatus status);
}
