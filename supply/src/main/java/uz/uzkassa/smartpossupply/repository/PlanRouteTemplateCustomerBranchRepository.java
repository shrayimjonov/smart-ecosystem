package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.planroutetemplate.PlanRouteTemplateCustomerBranch;

@Repository
public interface PlanRouteTemplateCustomerBranchRepository extends BaseRepository<PlanRouteTemplateCustomerBranch, Long> {

    boolean existsByPlanRouteTemplateItemIdAndCustomerBranchIdAndDeletedIsFalse(Long planRouteTemplateItemId, Long customerBranchId);
}
