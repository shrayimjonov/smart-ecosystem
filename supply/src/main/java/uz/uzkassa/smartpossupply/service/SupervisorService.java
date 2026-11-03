package uz.uzkassa.smartpossupply.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.address.Region;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.company.Partners;
import uz.uzkassa.smartpossupply.domain.customerbranch.CustomerBranch;
import uz.uzkassa.smartpossupply.domain.factura.Factura;
import uz.uzkassa.smartpossupply.domain.order.Order;
import uz.uzkassa.smartpossupply.domain.planroute.PlanRoute;
import uz.uzkassa.smartpossupply.domain.planroute.PlanRouteCustomerBranch;
import uz.uzkassa.smartpossupply.domain.planroutetemplate.PlanRouteTemplate;
import uz.uzkassa.smartpossupply.domain.stock.Stock;
import uz.uzkassa.smartpossupply.domain.supervisour.SupervisorEmployee;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.user.supervisor.*;
import uz.uzkassa.smartpossupply.enums.Role;
import uz.uzkassa.smartpossupply.filters.*;
import uz.uzkassa.smartpossupply.repository.*;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class SupervisorService extends BaseService {
    SupervisorEmployeeRepository supervisorEmployeeRepository;
    PlanRouteTemplateRepository planRouteTemplateRepository;
    PlanRouteCustomerBranchRepository planRouteCustomerBranchRepository;
    OrderRepository orderRepository;
    PartnerRepository partnerRepository;

    @Transactional
    public void assignAgent(SupervisorEmployeeAssignDTO dto) {
        if (!SecurityUtils.isSupervisor()) {
            throw validationService.badRequestExceptionThrow("Only supervisors can assign employees").get();
        }
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Set<Long> employeeIds = Optional.ofNullable(dto.getEmployeeIds())
                .map(ids -> ids.stream().filter(Objects::nonNull).collect(Collectors.toSet()))
                .orElse(Collections.emptySet());

        if (!employeeIds.isEmpty()) {
            validation(currentUserId, employeeIds);
        }

        supervisorEmployeeRepository.deleteAllBySupervisorId(currentUserId);
        supervisorEmployeeRepository.flush();

        if (employeeIds.isEmpty()) {
            return;
        }
        List<SupervisorEmployee> employees = employeeIds.stream().map(employeeId -> {
            SupervisorEmployee employee = new SupervisorEmployee();
            employee.setEmployeeId(employeeId);
            employee.setSupervisorId(currentUserId);
            return employee;
        }).toList();
        supervisorEmployeeRepository.saveAll(employees);
    }

    public Page<SupervisorDTO> getAll(SupervisorFilter filter) {
        filter.setCompanyId(SecurityUtils.getCompanyId());
        filter.setRole(Role.ROLE_SUPERVISOR.getCode());
        ResultList<SupervisorDTO> resultList = supervisorEmployeeRepository.getAll(filter);
        return new PageImpl<>(resultList.getList(), filter.getSortedPageable(), resultList.getCount());
    }

    public SupervisorDTO getBySupervisorId(Long id) {
        Long companyId = SecurityUtils.getCompanyId();
        return supervisorEmployeeRepository.bySupervisorId(id, companyId)
                .orElseThrow(validationService.entityNotFoundExceptionThrow("Супервизор не найден", "id", id));
    }

    public Page<EmployeeAssignmentDTO> getAllEmployee(SupervisorFilter filter) {
        filter.setCompanyId(SecurityUtils.getCompanyId());
        filter.setSupervisorId(SecurityUtils.getCurrentUserId());
        filter.setCustomerBranchId(SecurityUtils.getCurrentBranchId());
        ResultList<EmployeeAssignmentDTO> resultList = supervisorEmployeeRepository.getEmployees(filter);
        return new PageImpl<>(resultList.getList(), filter.getSortedPageable(), resultList.getCount());
    }

    private void validation(Long supervisorId, Set<Long> employeeIds) {
        Long currentBranchId = SecurityUtils.getCurrentBranchId();
        if (currentBranchId == null) {
            throw validationService.badRequestExceptionThrow("Supervisor branch is not selected").get();
        }

        long validCount = userRepository.countValidEmployees(
                Set.of(Role.ROLE_AGENT, Role.ROLE_EXPEDITOR), currentBranchId, employeeIds);
        if (validCount != employeeIds.size()) {
            throw validationService.badRequestExceptionThrow("Some employees are not agents/expeditors of the current branch").get();
        }

        boolean assignedToOther = supervisorEmployeeRepository
                .existsBySupervisorIdIsNotAndEmployeeIdIsInAndDeletedIsFalse(supervisorId, employeeIds);
        if (assignedToOther) {
            throw validationService.badRequestExceptionThrow("Some employees are already assigned to another supervisor").get();
        }
    }

    public Page<SupervisorPlanRouteDTO> getPlanRouteTemplate(SupervisorFilter filter) {
        Page<PlanRouteTemplate> planRouteTemplates = planRouteTemplateRepository.getPlanRouteTemplateBySupervisorId(filter.getSupervisorId(), filter.getPageable());
        List<SupervisorPlanRouteDTO> supervisorPlanRouteDTO = planRouteTemplates.getContent().stream().map(planRouteTemplate -> {
            SupervisorPlanRouteDTO dto = new SupervisorPlanRouteDTO();
            dto.setPlanRoute(planRouteTemplate.toCommonDTO());
            dto.setAgent(Optional.ofNullable(planRouteTemplate.getAgent())
                    .map(User::toCommonDTO)
                    .orElse(null));
            dto.setCustomerBranchCount(planRouteTemplate.getCustomerBranchCount());
            planRouteTemplate.getExpeditors().forEach(expeditor -> {
                if (expeditor != null && expeditor.getExpeditor() != null) {
                    CommonDTO expeditorDTO = expeditor.getExpeditor().toCommonDTO();
                    dto.getExpeditor().add(expeditorDTO);
                }
            });
            dto.setRegion(
                    Optional.ofNullable(planRouteTemplate.getRegion())
                            .map(Region::toCommonDTO)
                            .orElse(null)
            );
            dto.setCreatedDate(planRouteTemplate.getCreatedDate());
            return dto;
        }).toList();
        return new PageImpl<>(supervisorPlanRouteDTO, filter.getSortedPageable(), supervisorPlanRouteDTO.size());
    }


    public Page<SupervisorPlanRouteDTO> getPlanRouteTemplateForMobile(SupervisorFilter filter) {
        Page<PlanRouteTemplate> planRouteTemplates = planRouteTemplateRepository.getPlanRouteTemplateBySupervisorId(SecurityUtils.getCurrentUserId(), filter.getPageable());
        List<SupervisorPlanRouteDTO> supervisorPlanRouteDTO = planRouteTemplates.getContent().stream().map(planRouteTemplate -> {
            SupervisorPlanRouteDTO dto = new SupervisorPlanRouteDTO();
            dto.setPlanRoute(planRouteTemplate.toCommonDTO());
            dto.setAgent(Optional.ofNullable(planRouteTemplate.getAgent())
                    .map(User::toCommonDTO)
                    .orElse(null));
            dto.setCustomerBranchCount(planRouteTemplate.getCustomerBranchCount());
            dto.setCreatedDate(planRouteTemplate.getCreatedDate());
            return dto;
        }).toList();
        return new PageImpl<>(supervisorPlanRouteDTO, filter.getSortedPageable(), supervisorPlanRouteDTO.size());
    }

    public List<SupervisorPlanRouteStatisticDTO> getPlanRouteStatistics(Long templateId, LocalDate from, LocalDate to) {
        Long companyId = SecurityUtils.getCurrentCompanyId();
        return planRouteCustomerBranchRepository.getPlanRouteStatistics(companyId, templateId, from, to);
    }

    public Long getCountOfOrders(Long templateId, LocalDateTime from, LocalDateTime to) {
        return orderRepository.getCountOfOrdersByTemplateId(SecurityUtils.getCompanyId(), templateId, from, to);
    }

    public Page<SupervisorCustomerDTO> getCustomers(CompanyFilter filter) {
        filter.setCompanyId(SecurityUtils.getCurrentCompanyId());
        ResultList<Partners> resultList = partnerRepository.getResultList(filter, false);
        List<SupervisorCustomerDTO> customerDTOS = resultList.getList().stream().map(partner -> {
            Company customer = partner.getCustomer();
            SupervisorCustomerDTO dto = new SupervisorCustomerDTO();
            dto.setId(customer.getId());
            dto.setName(customer.getName());
            dto.setTin(customer.getTin());
            dto.setOfficialName(customer.getBrand());
            dto.setPhone(customer.getPhone());
            dto.setType(customer.getBusinessType());
            return dto;
        }).toList();
        return new PageImpl<>(customerDTOS, filter.getSortedPageable(), resultList.getCount());
    }

    public List<AgentStatisticsDTO> getAgentStatistics(List<Long> agentIds, SimpleFilter filter) {
        List<Long> requestedAgentIds = resolveAgentIds(agentIds);
        if (requestedAgentIds.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, List<AgentStatisticDTO>> statisticsByAgentId =
                orderRepository.getAgentStatisticsByCategory(requestedAgentIds, filter)
                .stream()
                .collect(Collectors.groupingBy(AgentStatisticByCategoryRowDTO::getAgentId,
                        Collectors.mapping(AgentStatisticByCategoryRowDTO::toAgentStatisticDTO, Collectors.toList())));
        return requestedAgentIds.stream()
                .map(agentId -> new AgentStatisticsDTO(agentId, statisticsByAgentId.getOrDefault(agentId, List.of())))
                .toList();
    }

    public List<AgentStatisticsByProductDTO> getAgentStatisticsByProducts(List<Long> agentIds, SimpleFilter filter) {
        List<Long> requestedAgentIds = resolveAgentIds(agentIds);
        if (requestedAgentIds.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, List<StatisticByProductDTO>> statisticsByAgentId =
                orderRepository.getAgentStatisticsByProduct(requestedAgentIds, filter)
                .stream()
                .collect(Collectors.groupingBy(AgentStatisticByProductRowDTO::getAgentId,
                        Collectors.mapping(AgentStatisticByProductRowDTO::toStatisticByProductDTO, Collectors.toList())));
        return requestedAgentIds.stream()
                .map(agentId -> new AgentStatisticsByProductDTO(agentId, statisticsByAgentId.getOrDefault(agentId, List.of())))
                .toList();
    }

    public List<PlanRouteInfoDTO> getPlanRoutes(Long agentId, SimpleFilter filter) {
        return planRouteCustomerBranchRepository.findAllByAgentId(agentId, filter.getFrom(), filter.getTo())
                .stream().map(planRouteCustomerBranch -> {
                    PlanRouteInfoDTO dto = new PlanRouteInfoDTO();
                    dto.setCustomerBranch(planRouteCustomerBranch.getCustomerBranch().toCommonDTO());
                    dto.setAddress(addressMapper.toDetailDTO(planRouteCustomerBranch.getCustomerBranch().getAddress()));
                    dto.setStartTime(planRouteCustomerBranch.getStartDate());
                    dto.setEndTime(planRouteCustomerBranch.getEndDate());
                    return dto;
                }).toList();
    }

    private List<Long> resolveAgentIds(List<Long> agentIds) {
        SupervisorFilter allAgentsFilter = new SupervisorFilter();
        allAgentsFilter.setPage(0);
        allAgentsFilter.setSize(Integer.MAX_VALUE);
        List<Long> assignedAgentIds = getAssignedAgents(allAgentsFilter).getContent()
                .stream()
                .map(AgentInfoDTO::getId)
                .toList();

        List<Long> requestedAgentIds = agentIds == null ? List.of() : agentIds.stream().filter(Objects::nonNull).distinct().toList();
        if (requestedAgentIds.isEmpty()) {
            return assignedAgentIds;
        }
        if (!assignedAgentIds.containsAll(requestedAgentIds)) {
            throw validationService.badRequestExceptionThrow("One or more agentIds are not assigned to the current supervisor").get();
        }
        return requestedAgentIds;
    }

    public Page<AgentInfoDTO> getAssignedAgents(SupervisorFilter filter) {
        filter.setSupervisorId(SecurityUtils.getCurrentUserId());
        if (filter.getRole() == null) {
            filter.setRole(Role.ROLE_AGENT.getCode());
        }
        ResultList<AgentInfoDTO> resultList = supervisorEmployeeRepository.getAssignedEmployees(filter);
        return new PageImpl<>(resultList.getList(), filter.getSortedPageable(), resultList.getCount());
    }

    public Page<OrderInfoDTO> getOrders(SupervisorOrderFilter filter) {
        filter.setSupervisorId(SecurityUtils.getCurrentUserId());
        ResultList<Order> resultList = orderRepository.getSupervisorOrder(filter);
        List<OrderInfoDTO> results = resultList.getList().stream().map(order -> {
            OrderInfoDTO dto = new OrderInfoDTO();
            dto.setOrderNumber(order.getOrderNumber());
            dto.setCustomer(
                    Optional.ofNullable(order.getCustomer())
                            .map(Company::toCommonDTO)
                            .orElse(null)
            );
            dto.setCustomerBranch(
                    Optional.ofNullable(order.getPlanRouteCustomerBranch())
                            .map(PlanRouteCustomerBranch::getCustomerBranch)
                            .map(CustomerBranch::toCommonDTO)
                            .orElse(null)
            );
            dto.setAgent(Optional.ofNullable(order.getAgent())
                    .map(User::toCommonDTO)
                    .orElse(null));

            dto.setExpeditor(Optional.ofNullable(order.getExpeditor())
                    .map(User::toCommonDTO)
                    .orElse(null));
            dto.setPlanRoute(
                    Optional.ofNullable(order.getPlanRoute())
                            .map(PlanRoute::toCommonDTO)
                            .orElse(null)
            );
            dto.setTotalPrice(order.getTotalPrice());
            dto.setOrderDate(order.getOrderDate());
            dto.setStatus(order.getStatus());
            dto.setId(order.getId());
            return dto;
        }).toList();
        return new PageImpl<>(results, filter.getSortedPageable(), resultList.getCount());
    }

    public Page<AgentPlanRouteInfoDTO> getAgentsWithPlanRoute(SupervisorFilter filter) {
        filter.setSupervisorId(SecurityUtils.getCurrentUserId());
        ResultList<AgentPlanRouteInfoDTO> resultList = supervisorEmployeeRepository.getAgentsWithPlanRoute(filter);
        return new PageImpl<>(resultList.getList(), filter.getSortedPageable(), resultList.getCount());
    }

    public CommonStatisticsDTO getCommonStatistics(SupervisorFilter filter) {
        filter.setSupervisorId(SecurityUtils.getCurrentUserId());

        // Execute both queries in parallel
        CompletableFuture<CommonStatisticsDTO> planRoutesFuture =
                CompletableFuture.supplyAsync(() ->
                        supervisorEmployeeRepository.getPlanRoutesStatistics(filter)
                );

        CompletableFuture<Long> countOrderFuture =
                CompletableFuture.supplyAsync(() ->
                        supervisorEmployeeRepository.getCommonStatistics(filter)
                );

        // Wait for both to complete and combine results
        return planRoutesFuture.thenCombine(countOrderFuture,
                (planRoutesStatistics, countOrder) -> {
                    planRoutesStatistics.setCountOrders(countOrder);
                    return planRoutesStatistics;
                }
        ).join();
    }


    public ExpeditorStatisticsDTO getExpeditorCommonStatistics(SupervisorFilter filter) {
        ExpeditorStatisticsDTO expeditorStatisticsDTO = new ExpeditorStatisticsDTO();

        Long countOfExpeditors = supervisorEmployeeRepository.getExpeditorOnPlanRoute(filter);
        expeditorStatisticsDTO.setCountOfExpeditors(countOfExpeditors);

        ExpeditorCompletedStockOutDTO expeditorStatistics = supervisorEmployeeRepository.getExpeditorStockStatistics(filter);
        expeditorStatisticsDTO.setOrderStatistics(expeditorStatistics);

        return expeditorStatisticsDTO;
    }

    public ExpeditorCancelledStockOutDTO getExpeditorStatistics(SupervisorExpeditorFilter filter) {
        return supervisorEmployeeRepository.getExpeditorStatistics(filter);
    }

    public ExpeditorOrderDTO getExpeditorOrderStatistics(SupervisorOrderFilter filter) {
        return supervisorEmployeeRepository.getExpeditorOrderStatistics(filter);
    }

    public Page<ExpeditorStockDTO> getExpeditorStockStatistics(SupervisorExpeditorFilter filter) {
        filter.setSupervisorId(SecurityUtils.getCurrentUserId());
        ResultList<Stock> resultList = supervisorEmployeeRepository.getExpeditorStocks(filter);
        List<ExpeditorStockDTO> result = resultList.getList().stream().map(stock -> {
            ExpeditorStockDTO dto = new ExpeditorStockDTO();
            dto.setStockNumber(stock.getStockNumber());
            dto.setStatus(stock.getStatus());
            dto.setCustomerBranch(Optional.ofNullable(stock.getDeliveryBranch()).map(CustomerBranch::toCommonDTO).orElse(null));
            dto.setOrder(Optional.ofNullable(stock.getOrder()).map(Order::toCommonDTO).orElse(null));
            dto.setPaymentType(stock.getPaymentType());
            dto.setFactura(Optional.ofNullable(stock.getFactura()).map(Factura::toCommonDTO).orElse(null));
            dto.setCreatedDate(stock.getCreatedDate());
            return dto;
        }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }
}
