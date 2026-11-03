package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.planroutetemplate.PlanRouteTemplateItem;

@Repository
public interface PlanRouteTemplateItemRepository extends BaseRepository<PlanRouteTemplateItem, Long> {
    @Modifying
    @Query("update PlanRouteTemplateItem set deleted = true  where planRouteTemplateId=:planRouteTemplateId")
    void deleteAllByPlanRouteTemplateId(@Param("planRouteTemplateId") Long planRouteTemplateId);

}
