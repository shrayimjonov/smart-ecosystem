package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import uz.uzkassa.smartpossupply.domain.planroute.PlanRouteCustomerBranch;
import uz.uzkassa.smartpossupply.dto.agent.performancereport.AgentPerformanceReportVisitDTO;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.kpi.KPICalculateTotalDTO;
import uz.uzkassa.smartpossupply.dto.kpi.KPIPlanRouteCalculateDTO;
import uz.uzkassa.smartpossupply.dto.user.supervisor.SupervisorPlanRouteStatisticDTO;
import uz.uzkassa.smartpossupply.enums.PlanRouteCustomerBranchStatus;
import uz.uzkassa.smartpossupply.filters.AgentPerformanceReportFilter;
import uz.uzkassa.smartpossupply.filters.KPIFilter;
import uz.uzkassa.smartpossupply.filters.PlanRouteCustomerBranchFilter;
import uz.uzkassa.smartpossupply.repository.PlanRouteCustomerBranchRepositoryCustom;

import java.time.LocalDate;
import java.util.List;

public class PlanRouteCustomerBranchRepositoryImpl extends BaseRepositoryImpl implements PlanRouteCustomerBranchRepositoryCustom {

    @Override
    public List<AgentPerformanceReportVisitDTO> getAgentPerformanceReport(AgentPerformanceReportFilter filter) {
        boolean filterByPeriod = filter.getFrom() != null && filter.getTo() != null;
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT prcb.status, COUNT(prcb.id) FROM plan_route pr ");
        sql.append(" INNER JOIN plan_route_template prt ON prt.id = pr.template_id ");
        sql.append(" INNER JOIN plan_route_item pri ON pr.id = pri.plan_route_id ");
        sql.append(" INNER JOIN plan_route_customer_branch prcb ON pri.id = prcb.plan_route_item_id ");
        sql.append(" WHERE pr.deleted = false ");
        sql.append(" AND pr.agent_id =:agentId ");
        sql.append(" AND prt.branch_id =:branchId ");
        if (filterByPeriod) {
            sql.append(" AND (pr.from_date BETWEEN :from AND :to) ");
        }
        sql.append(" GROUP BY prcb.status ");
        Query query = entityManager.createNativeQuery(sql.toString(), AgentPerformanceReportVisitDTO.class);
        query.setParameter("agentId", filter.getAgentId());
        query.setParameter("branchId", filter.getBranchId());
        if (filterByPeriod) {
            query.setParameter("from", filter.getFrom());
            query.setParameter("to", filter.getTo());
        }
        return query.getResultList();
    }

    @Override
    public ResultList<KPIPlanRouteCalculateDTO> calculatePlanRouteCount(KPIFilter filter) {
        ResultList<KPIPlanRouteCalculateDTO> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder("""
                SELECT
                    pr.id AS planRouteId,
                    pr.name AS planRouteName,
                    pr.agent_id AS employeeId,
                    u.first_name || ' ' || u.last_name AS employeeName,
                    COUNT(prcb.id) FILTER (WHERE prcb.status = 'VISITED') AS visitedCount,
                    COUNT(prcb.id) AS totalCount
                FROM plan_route_customer_branch prcb
                         JOIN plan_route_item pri ON prcb.plan_route_item_id = pri.id
                         JOIN plan_route pr ON pri.plan_route_id = pr.id
                         JOIN users u ON pr.agent_id = u.id
                WHERE pr.company_id = :companyId
                """);

        StringBuilder countSql = new StringBuilder("""
                SELECT count(distinct pr.id)
                FROM plan_route_customer_branch prcb
                         JOIN plan_route_item pri ON prcb.plan_route_item_id = pri.id
                         JOIN plan_route pr ON pri.plan_route_id = pr.id
                         JOIN users u ON pr.agent_id = u.id
                WHERE pr.company_id = :companyId
                """);

        if (filter.getFrom() != null) {
            sql.append(" AND prcb.end_date >= :fromDate");
            countSql.append(" AND prcb.end_date >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" AND prcb.end_date <= :toDate");
            countSql.append(" AND prcb.end_date <= :toDate");
        }

        sql.append("  GROUP BY pr.id, pr.name, pr.agent_id, employeeName");
        Query query = entityManager.createNativeQuery(sql.toString(), KPIPlanRouteCalculateDTO.class).setFirstResult(filter.getStart()).setMaxResults(filter.getSize());

        Query countQuery = entityManager.createNativeQuery(countSql.toString(), Long.class);

        query.setParameter("companyId", filter.getCompanyId());
        countQuery.setParameter("companyId", filter.getCompanyId());

        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
            countQuery.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
            countQuery.setParameter("toDate", filter.getTo());
        }

        long count = (long) countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }

    @Override
    public KPICalculateTotalDTO calculatePlanRouteTotal(KPIFilter filter) {
        StringBuilder sql = new StringBuilder("""
                SELECT
                    count(distinct pr.agent_id) AS countEmployees,
                    COUNT(prcb.id) FILTER (WHERE prcb.status = 'VISITED') AS visitedCount,
                    COUNT(prcb.id) AS totalCount
                FROM plan_route_customer_branch prcb
                         JOIN plan_route_item pri ON prcb.plan_route_item_id = pri.id
                         JOIN plan_route pr ON pri.plan_route_id = pr.id
                         JOIN users u ON pr.agent_id = u.id
                WHERE pr.company_id = :companyId and prcb.deleted = false and pr.deleted = u.deleted
                """);

        if (filter.getFrom() != null) {
            sql.append(" AND prcb.endDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" AND prcb.endDate <= :toDate");
        }

        Query query = entityManager.createNativeQuery(sql.toString(), KPICalculateTotalDTO.class);

        query.setParameter("companyId", filter.getCompanyId());

        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
        }
        return (KPICalculateTotalDTO) query.getSingleResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SupervisorPlanRouteStatisticDTO> getPlanRouteStatistics(Long companyId, Long templateId, LocalDate from, LocalDate to) {
        String sql = """
                SELECT pri.visit_plan_date                                                          AS visitedDate,
                    COUNT(prcb.id)                                                                  AS totalCount,
                    COUNT(prcb.id) FILTER (WHERE prcb.status in ('VISITED', 'VISITING'))            AS countOfVisited,
                    COUNT(prcb.id) FILTER (WHERE prcb.status in ('NOT_VISITED', 'NOT_VISITED_YET')) AS countOfNotVisited
                FROM plan_route_customer_branch AS prcb
                    JOIN plan_route_item as pri on prcb.plan_route_item_id = pri.id
                    JOIN plan_route as pr on pri.plan_route_id = pr.id
                    JOIN plan_route_template as prt on pr.template_id = prt.id
                    JOIN branch as b on prt.branch_id = b.id
                WHERE b.company_id = :companyId
                    AND pr.template_id = :templateId
                    AND pri.visit_plan_date BETWEEN :fromDate AND :toDate
                GROUP BY pri.visit_plan_date;
                """;

        Query query = entityManager.createNativeQuery(sql, SupervisorPlanRouteStatisticDTO.class);
        query.setParameter("companyId", companyId);
        query.setParameter("templateId", templateId);
        query.setParameter("fromDate", from);
        query.setParameter("toDate", to);
        return query.getResultList();
    }

    @Override
    public ResultList<PlanRouteCustomerBranch> getPlanRoutesCustomerBranches(PlanRouteCustomerBranchFilter filter) {
        StringBuilder sql = new StringBuilder("""
                SELECT prcb FROM PlanRouteCustomerBranch prcb
                JOIN PlanRouteItem pri ON pri.id = prcb.planRouteItemId
                JOIN planRoute pr ON pr.id = pri.planRouteId
                    WHERE prcb.deleted = false
                        AND pr.agentId = :agentId
                        AND pr.id = :planRouteId
                """);
        if (filter.getStatus() != null) {
            sql.append(" AND prcb.status = :status");
        }
        if (filter.getCustomerBranchId() != null) {
            sql.append(" AND prcb.customerBranchId = :customerBranchId");
        }
        if (filter.getVisitPlanDate() != null) {
            sql.append(" AND pri.visitPlanDate = :visitPlanDate");
        }

        TypedQuery<PlanRouteCustomerBranch> query = entityManager.createQuery(sql.toString(), PlanRouteCustomerBranch.class).setFirstResult(filter.getStart()).setMaxResults(filter.getSize());

        String countSQL = sql.toString().replaceAll("SELECT prcb", "SELECT COUNT(*)");

        TypedQuery<Long> countQuery = entityManager.createQuery(countSQL, Long.class);
        query.setParameter("agentId", filter.getAgentId());
        countQuery.setParameter("agentId", filter.getAgentId());
        query.setParameter("planRouteId", filter.getPlanRouteId());
        countQuery.setParameter("planRouteId", filter.getPlanRouteId());

        if (filter.getStatus() != null) {
            PlanRouteCustomerBranchStatus planRouteCustomerBranchStatus = PlanRouteCustomerBranchStatus.valueOf(filter.getStatus());
            query.setParameter("status", planRouteCustomerBranchStatus);
            countQuery.setParameter("status", planRouteCustomerBranchStatus);
        }
        if (filter.getCustomerBranchId() != null) {
            query.setParameter("customerBranchId", filter.getCustomerBranchId());
            countQuery.setParameter("customerBranchId", filter.getCustomerBranchId());
        }
        if (filter.getVisitPlanDate() != null) {
            query.setParameter("visitPlanDate", filter.getVisitPlanDate());
            countQuery.setParameter("visitPlanDate", filter.getVisitPlanDate());
        }
        ResultList<PlanRouteCustomerBranch> resultList = new ResultList<>();
        Long count = countQuery.getSingleResult();

        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }

}
