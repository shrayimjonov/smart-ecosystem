package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.planroute.PlanRouteItem;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 20.01.2023 10:34
 */
@Repository
public interface PlanRouteItemRepository extends BaseRepository<PlanRouteItem, Long> {

    Optional<PlanRouteItem> findFirstByPlanRouteIdAndVisitPlanDateAndDeletedIsFalse(Long planRouteId, LocalDate visitPlanDate);
}
