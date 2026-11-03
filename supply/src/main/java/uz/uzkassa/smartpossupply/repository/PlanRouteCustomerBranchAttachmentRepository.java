package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.planroute.PlanRouteCustomerBranchAttachment;
import uz.uzkassa.smartpossupply.enums.PlanRouteAttachmentType;

import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 25.12.2023
 */
@Repository
public interface PlanRouteCustomerBranchAttachmentRepository extends BaseRepository<PlanRouteCustomerBranchAttachment, Long> {

    Optional<PlanRouteCustomerBranchAttachment> findFirstByPlanRouteCustomerBranchIdAndAttachmentType(Long planRouteCustomerBranchId, PlanRouteAttachmentType attachmentType);

}
