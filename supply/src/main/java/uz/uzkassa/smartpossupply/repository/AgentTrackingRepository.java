package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.planroute.AgentTracking;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 31.01.2023 16:03
 */
@Repository
public interface AgentTrackingRepository extends BaseRepository<AgentTracking, Long>, AgentTrackingRepositoryCustom {
}
