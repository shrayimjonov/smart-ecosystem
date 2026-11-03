package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.dto.agent.performancereport.AgentPerformanceReportDTO;
import uz.uzkassa.smartpossupply.enums.Role;
import uz.uzkassa.smartpossupply.enums.UserStatus;
import uz.uzkassa.smartpossupply.filters.AgentPerformanceReportFilter;
import uz.uzkassa.smartpossupply.repository.OrderRepository;
import uz.uzkassa.smartpossupply.repository.PlanRouteCustomerBranchRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AgentService extends BaseService {

    OrderRepository orderRepository;
    PlanRouteCustomerBranchRepository planRouteCustomerBranchRepository;

    public List<AgentPerformanceReportDTO> getPerformanceReport(AgentPerformanceReportFilter filter) {
        Long companyId = Optional.ofNullable(filter.getCompanyId()).orElse(getCurrentCompanyId());
        List<User> agents = null;
        if (filter.getAgentId() != null) {
            User agent = userRepository.findById(filter.getAgentId()).orElseThrow(validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "id", filter.getAgentId()));
            agents = new ArrayList<>(1);
            agents.add(agent);
        } else if (filter.getBranchId() != null) {
            agents = userRepository.findAllByBranchIdAndStatusAndRoleInAndDeletedIsFalse(filter.getBranchId(), UserStatus.ACTIVE, List.of(Role.ROLE_AGENT, Role.ROLE_NETWORK_AGENT));
        } else if (companyId != null) {
            agents = userRepository.findAllByCompanyIdAndStatusAndRoleInAndDeletedIsFalse(companyId, UserStatus.ACTIVE, List.of(Role.ROLE_AGENT, Role.ROLE_NETWORK_AGENT));
        }
        if (CollectionUtils.isEmpty(agents)) {
            return Collections.emptyList();
        }

        List<AgentPerformanceReportDTO> result = new ArrayList<>();
        for (User agent : agents) {

            filter.setAgentId(agent.getId());
            filter.setBranchId(agent.getBranchId());

            AgentPerformanceReportDTO agentPerformanceReportDTO = new AgentPerformanceReportDTO();
            if (agent.getBranch() != null) {
                agentPerformanceReportDTO.setBranch(agent.getBranch().toCommonDTO());
            }
            agentPerformanceReportDTO.setAgent(agent.toCommonDTO());

            agentPerformanceReportDTO.setVisits(planRouteCustomerBranchRepository.getAgentPerformanceReport(filter));

            agentPerformanceReportDTO.setOrders(orderRepository.getAgentPerformanceReport(filter));

            agentPerformanceReportDTO.setOrderTotalPrice(orderRepository.getAgentPerformanceReportOrderTotalPrice(filter));

            agentPerformanceReportDTO.setPayments(orderRepository.getAgentPerformanceReportPayments(filter));

            agentPerformanceReportDTO.setProducts(orderRepository.getAgentPerformanceReportOrderProducts(filter));

            agentPerformanceReportDTO.setCategories(orderRepository.getAgentPerformanceReportOrderCategories(filter));

            result.add(agentPerformanceReportDTO);
        }
        return result;
    }
}
