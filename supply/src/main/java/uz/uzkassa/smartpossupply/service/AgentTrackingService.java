package uz.uzkassa.smartpossupply.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.planroute.AgentTracking;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.planroute.AgentTrackingDTO;
import uz.uzkassa.smartpossupply.dto.planroute.AgentTrackingListDTO;
import uz.uzkassa.smartpossupply.dto.planroute.AgentTrackingOfflineDTO;
import uz.uzkassa.smartpossupply.filters.PlanRouteFilter;
import uz.uzkassa.smartpossupply.repository.AgentTrackingRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AgentTrackingService extends BaseService {

    AgentTrackingRepository agentTrackingRepository;

    @Transactional
    public Long create(AgentTrackingDTO agentTrackingDTO) {
        AgentTracking agentTracking = new AgentTracking();
        agentTracking.setAgentId(getCurrentUserId());
        agentTracking.setCreatedDate(LocalDateTime.now());
        agentTracking.setLatitude(agentTrackingDTO.getLatitude());
        agentTracking.setLongitude(agentTrackingDTO.getLongitude());
        agentTracking.setEntityType(agentTrackingDTO.getEntityType());
        agentTracking.setEntityId(agentTrackingDTO.getEntityId());
        agentTrackingRepository.save(agentTracking);
        return agentTracking.getId();
    }

    @Transactional
    public void createOffline(AgentTrackingOfflineDTO agentTrackingOfflineDTO) {
        Long agentId = agentTrackingOfflineDTO.getAgentId();
        List<AgentTracking> agentTrackings = agentTrackingOfflineDTO.getLocations().stream().map(location -> {
            AgentTracking agentTracking = new AgentTracking();
            agentTracking.setAgentId(agentId);
            agentTracking.setCreatedDate(location.getCreatedDate());
            agentTracking.setLatitude(location.getLatitude());
            agentTracking.setLongitude(location.getLongitude());
            agentTracking.setStatus(location.getStatus());
            return agentTracking;
        }).toList();
        agentTrackingRepository.saveAll(agentTrackings);
    }

    public Page<AgentTrackingListDTO> getList(PlanRouteFilter filter) {
        ResultList<AgentTracking> resultList = agentTrackingRepository.getResultList(filter);
        List<AgentTrackingListDTO> result = resultList.getList().stream().map(agentTracking -> {
            AgentTrackingListDTO agentTrackingListDTO = new AgentTrackingListDTO();
            agentTrackingListDTO.setId(agentTracking.getId());
            agentTrackingListDTO.setTrackingDate(agentTracking.getCreatedDate());
            agentTrackingListDTO.setLatitude(agentTracking.getLatitude());
            agentTrackingListDTO.setLongitude(agentTracking.getLongitude());
            agentTrackingListDTO.setStatus(agentTracking.getStatus());
            agentTrackingListDTO.setAddress(agentTracking.getAddress());
            agentTrackingListDTO.setEntityType(agentTracking.getEntityType());
            agentTrackingListDTO.setEntityId(agentTracking.getEntityId());
            return agentTrackingListDTO;
        }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }
}
