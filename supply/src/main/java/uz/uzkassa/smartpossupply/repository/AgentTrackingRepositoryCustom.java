package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.planroute.AgentTracking;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.PlanRouteFilter;

/**
 * Created by: Shuxratjon Rayimjonov
 * Date: 11.12.2024
 */
@Repository
public interface AgentTrackingRepositoryCustom {
    ResultList<AgentTracking> getResultList(PlanRouteFilter filter);
}
