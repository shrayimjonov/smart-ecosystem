package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.Query;
import uz.uzkassa.smartpossupply.domain.stock.Stock;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.user.supervisor.*;
import uz.uzkassa.smartpossupply.enums.SortTypeEnum;
import uz.uzkassa.smartpossupply.filters.SupervisorExpeditorFilter;
import uz.uzkassa.smartpossupply.filters.SupervisorFilter;
import uz.uzkassa.smartpossupply.filters.SupervisorOrderFilter;
import uz.uzkassa.smartpossupply.repository.SupervisorEmployeeRepositoryCustom;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class SupervisorEmployeeRepositoryCustomImpl extends BaseRepositoryImpl implements SupervisorEmployeeRepositoryCustom {

    private static final String SUPERVISOR_SELECT = """
            SELECT u.id                                                                      AS supervisorId,
                   NULLIF(TRIM(CONCAT_WS(' ', u.first_name, u.last_name)), '')               AS supervisorFullName,
                   u.phone                                                                   AS phoneNumber,
                   rt.countPlanRoutes                                                        AS countPlanRoutes,
                   ag.countAgents                                                            AS countAgents,
                   rt.countCustomers                                                         AS countCustomers,
                   rt.countCustomerBranches                                                  AS countCustomerBranches
            """;

    private static final String SUPERVISOR_FROM_WITH_COUNTERS = """
            FROM users u
            LEFT JOIN LATERAL (
                SELECT COUNT(DISTINCT se.employee_id) AS countAgents
                FROM supervisor_employees se
                         JOIN users a ON a.id = se.employee_id
                WHERE se.supervisor_id = u.id
                  AND se.deleted = FALSE
                  AND a.deleted = FALSE
                  AND a.role = 'ROLE_AGENT'
            ) ag ON TRUE
            LEFT JOIN LATERAL (
                SELECT COUNT(DISTINCT prt.id)                  AS countPlanRoutes,
                       COUNT(DISTINCT cb.customer_id)          AS countCustomers,
                       COUNT(DISTINCT prtcb.customer_branch_id) AS countCustomerBranches
                FROM supervisor_employees se
                         JOIN users a ON a.id = se.employee_id
                         JOIN plan_route_template prt ON prt.agent_id = a.id AND prt.deleted = FALSE
                         LEFT JOIN plan_route_template_item prti
                                   ON prti.plan_route_template_id = prt.id AND prti.deleted = FALSE
                         LEFT JOIN plan_route_template_customer_branch prtcb
                                   ON prtcb.plan_route_template_item_id = prti.id AND prtcb.deleted = FALSE
                         LEFT JOIN customer_branch cb ON cb.id = prtcb.customer_branch_id
                WHERE se.supervisor_id = u.id
                  AND se.deleted = FALSE
                  AND a.deleted = FALSE
            ) rt ON TRUE
            """;

    // Company is checked on the supervisor itself; companyId must always come from the token.
    private static final String SUPERVISOR_BASE_WHERE = """
            WHERE u.company_id = :companyId
              AND u.role = 'ROLE_SUPERVISOR'
              AND u.deleted = FALSE
            """;

    @Override
    @SuppressWarnings("unchecked")
    public ResultList<SupervisorDTO> getAll(SupervisorFilter filter) {
        StringBuilder where = new StringBuilder(SUPERVISOR_BASE_WHERE);

        if (filter.getSupervisorId() != null) {
            where.append(" AND u.id = :supervisorId");
        }

        if (filter.getAgentId() != null) {
            where.append("""
                     AND EXISTS (SELECT 1
                                 FROM supervisor_employees se
                                          JOIN users a ON a.id = se.employee_id
                                 WHERE se.supervisor_id = u.id
                                   AND se.employee_id = :agentId
                                   AND se.deleted = FALSE
                                   AND a.deleted = FALSE)
                    """);
        }

        if (filter.getPlanRouteId() != null) {
            where.append("""
                     AND EXISTS (SELECT 1
                                 FROM supervisor_employees se
                                          JOIN users a ON a.id = se.employee_id
                                          JOIN plan_route_template prt ON prt.agent_id = a.id
                                 WHERE se.supervisor_id = u.id
                                   AND prt.id = :planRouteId
                                   AND prt.deleted = FALSE
                                   AND se.deleted = FALSE
                                   AND a.deleted = FALSE)
                    """);
        }

        // Supervisor has at least one agent/expeditor working in this branch (all branches from user_branch).
        if (filter.getBranchId() != null) {
            where.append("""
                     AND EXISTS (SELECT 1
                                 FROM supervisor_employees se
                                          JOIN users e ON e.id = se.employee_id
                                 WHERE se.supervisor_id = u.id
                                   AND (EXISTS (SELECT 1 FROM user_branch ub
                                                WHERE ub.user_id = e.id AND ub.branch_id = :branchId)
                                        OR e.branch_id = :branchId)
                                   AND e.role IN ('ROLE_AGENT', 'ROLE_EXPEDITOR')
                                   AND se.deleted = FALSE
                                   AND e.deleted = FALSE)
                    """);
        }

        // customerBranchId is intentionally ignored (SS-164, addendum item 5): replaced by branchId on the front.

        // Full-name variants let "first last" / "last first" queries match across columns.
        if (filter.isNotEmpty()) {
            where.append(" AND (LOWER(CONCAT_WS(' ', u.first_name, u.last_name, u.patronymic))").append(filter.getSearchKey())
                    .append(" OR LOWER(CONCAT_WS(' ', u.last_name, u.first_name, u.patronymic))").append(filter.getSearchKey())
                    .append(" OR LOWER(u.first_name)").append(filter.getSearchKey())
                    .append(" OR LOWER(u.last_name)").append(filter.getSearchKey())
                    .append(" OR LOWER(u.patronymic)").append(filter.getSearchKey())
                    .append(" OR LOWER(u.phone)").append(filter.getSearchKey())
                    .append(")");
        }

        String direction = filter.getSortOrder() == SortTypeEnum.asc ? "ASC" : "DESC";
        String sql = SUPERVISOR_SELECT + SUPERVISOR_FROM_WITH_COUNTERS + where + " ORDER BY u.id " + direction;
        String countSql = "SELECT COUNT(u.id) FROM users u " + where;

        Query query = entityManager.createNativeQuery(sql, SupervisorDTO.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());
        Query countQuery = entityManager.createNativeQuery(countSql, Long.class);

        for (Query q : List.of(query, countQuery)) {
            q.setParameter("companyId", filter.getCompanyId());
            if (filter.getSupervisorId() != null) {
                q.setParameter("supervisorId", filter.getSupervisorId());
            }
            if (filter.getAgentId() != null) {
                q.setParameter("agentId", filter.getAgentId());
            }
            if (filter.getPlanRouteId() != null) {
                q.setParameter("planRouteId", filter.getPlanRouteId());
            }
            if (filter.getBranchId() != null) {
                q.setParameter("branchId", filter.getBranchId());
            }
            if (filter.isNotEmpty()) {
                q.setParameter("searchKey", filter.getSearchValue());
            }
        }

        ResultList<SupervisorDTO> resultList = new ResultList<>();
        Long count = (Long) countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<SupervisorDTO> bySupervisorId(Long supervisorId, Long companyId) {
        String sql = SUPERVISOR_SELECT + SUPERVISOR_FROM_WITH_COUNTERS + SUPERVISOR_BASE_WHERE
                + " AND u.id = :supervisorId";

        Query nativeQuery = entityManager.createNativeQuery(sql, SupervisorDTO.class);
        nativeQuery.setParameter("companyId", companyId);
        nativeQuery.setParameter("supervisorId", supervisorId);
        List<SupervisorDTO> resultList = nativeQuery.getResultList();
        return resultList.stream().findFirst();
    }

    @Override
    public ResultList<EmployeeAssignmentDTO> getEmployees(SupervisorFilter filter) {
        StringBuilder sql = new StringBuilder("""
                    SELECT
                        u.id                                 AS id,
                        (u.first_name || ' ' || u.last_name) AS fullName,
                        CASE
                            WHEN se.supervisor_id = :supervisorId
                                THEN TRUE
                                ELSE FALSE
                        END                                    AS assigned
                """);

        StringBuilder countSql = new StringBuilder("SELECT COUNT(*)");

        String fromPart = """
                   FROM users u
                        LEFT JOIN supervisor_employees se
                            ON u.id = se.employee_id
                            AND (se.supervisor_id = :supervisorId OR se.supervisor_id IS NULL)
                        WHERE u.role IN ('ROLE_AGENT', 'ROLE_EXPEDITOR')
                          AND u.company_id = :companyId
                          AND u.branch_id = :customerBranchId
                          AND u.deleted = FALSE
                          AND (
                                se.supervisor_id IS NULL
                                OR se.supervisor_id = :supervisorId
                              )
                """;
        sql.append(fromPart);
        countSql.append(fromPart);
        sql.append(" ORDER BY id DESC");

        ResultList<EmployeeAssignmentDTO> resultList = new ResultList<>();
        Query query = entityManager.createNativeQuery(sql.toString(), EmployeeAssignmentDTO.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        query.setParameter("companyId", filter.getCompanyId());
        query.setParameter("supervisorId", filter.getSupervisorId());
        query.setParameter("customerBranchId", filter.getCustomerBranchId());

        Query countQuery = entityManager.createNativeQuery(countSql.toString(), Long.class);
        countQuery.setParameter("companyId", filter.getCompanyId());
        countQuery.setParameter("supervisorId", filter.getSupervisorId());
        countQuery.setParameter("customerBranchId", filter.getCustomerBranchId());

        Long count = (Long) countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResultList<AgentInfoDTO> getAssignedEmployees(SupervisorFilter filter) {
        StringBuilder sql = new StringBuilder("""
                SELECT u.id                               AS id,
                       u.first_name || ' ' || u.last_name AS fullName
                FROM supervisor_employees se
                         JOIN users u on u.id = se.employee_id
                WHERE se.supervisor_id = :supervisorId
                     AND u.role = :role
                """);

        String countSql = sql.toString()
                .replaceFirst("SELECT[\\s\\S]*?FROM", "SELECT COUNT(*) FROM");

        Query query = entityManager.createNativeQuery(sql.toString(), AgentInfoDTO.class);
        query.setParameter("supervisorId", filter.getSupervisorId());
        query.setParameter("role", filter.getRole());

        Query countQuery = entityManager.createNativeQuery(countSql, Long.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        countQuery.setParameter("supervisorId", filter.getSupervisorId());
        countQuery.setParameter("role", filter.getRole());

        ResultList<AgentInfoDTO> resultList = new ResultList<>();

        Long count = (Long) countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResultList<AgentPlanRouteInfoDTO> getAgentsWithPlanRoute(SupervisorFilter filter) {
        if (filter.getVisitPlanDate() == null) {
            filter.setVisitPlanDate(LocalDate.now());
        }
        StringBuilder sql = new StringBuilder("""
                SELECT u.id                                 AS id,
                       u.first_name || ' ' || u.last_name   AS fullName,
                       pr.id                                AS planRouteId,
                       pr.plan_route_number                 AS planRouteName
                FROM plan_route pr
                         JOIN plan_route_item pri ON pri.plan_route_id = pr.id
                         JOIN supervisor_employees se ON se.employee_id = pr.agent_id
                         JOIN users u ON u.id = se.employee_id
                WHERE se.supervisor_id = :supervisorId
                  AND pri.visit_plan_date = :visitPlanDate
                """);

        String countSql = sql.toString()
                .replaceFirst("SELECT[\\s\\S]*?FROM", "SELECT COUNT(*) FROM");

        Query query = entityManager.createNativeQuery(sql.toString(), AgentPlanRouteInfoDTO.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        query.setParameter("supervisorId", filter.getSupervisorId());
        query.setParameter("visitPlanDate", filter.getVisitPlanDate());

        Query countQuery = entityManager.createNativeQuery(countSql, Long.class);
        countQuery.setParameter("supervisorId", filter.getSupervisorId());
        countQuery.setParameter("visitPlanDate", filter.getVisitPlanDate());

        ResultList<AgentPlanRouteInfoDTO> resultList = new ResultList<>();

        Long count = (Long) countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }

    @Override
    public CommonStatisticsDTO getPlanRoutesStatistics(SupervisorFilter filter) {
        if (filter.getVisitPlanDate() == null) {
            filter.setVisitPlanDate(LocalDate.now());
        }
        String sql = """
                SELECT COUNT(prcb.id) FILTER (WHERE prcb.status in ('VISITED', 'VISITING')) AS countVisitedPlanRoutes,
                       COUNT(prcb.id)                                                       AS countAllPlanRoutes
                FROM plan_route pr
                         JOIN plan_route_item pri on pri.plan_route_id = pr.id
                         JOIN plan_route_customer_branch prcb on prcb.plan_route_item_id = pri.id
                         JOIN supervisor_employees se on se.employee_id = pr.agent_id
                         JOIN users u on u.id = se.employee_id
                WHERE se.supervisor_id = :supervisorId
                  AND pri.visit_plan_date = :visitPlanDate
                """;

        Query query = entityManager.createNativeQuery(sql, CommonStatisticsDTO.class);
        query.setParameter("supervisorId", filter.getSupervisorId());
        query.setParameter("visitPlanDate", filter.getVisitPlanDate());
        return (CommonStatisticsDTO) query.getSingleResult();
    }

    @Override
    public Long getCommonStatistics(SupervisorFilter filter) {
        if (filter.getVisitPlanDate() == null) {
            filter.setVisitPlanDate(LocalDate.now());
        }
        String sql = """
                SELECT coalesce(count(o.id), 0)
                FROM orders o
                         join supervisor_employees se on se.employee_id = o.agent_id
                WHERE se.supervisor_id = :supervisorId
                  AND o.order_date >= :from
                  AND o.created_date <= :to;
                """;
        Query query = entityManager.createNativeQuery(sql, Long.class);
        query.setParameter("supervisorId", filter.getSupervisorId());
        query.setParameter("from", filter.getFrom());
        query.setParameter("to", filter.getTo());
        return (Long) query.getSingleResult();
    }

    @Override
    public Long getExpeditorOnPlanRoute(SupervisorFilter filter) {
        StringBuilder sql = new StringBuilder("""
                SELECT coalesce(count(distinct pre.expeditor_id), 0)
                FROM plan_route pr
                         JOIN plan_route_item pri ON pr.id = pri.plan_route_id
                         JOIN plan_route_expeditor pre ON pre.plan_route_id = pr.id
                         JOIN supervisor_employees se ON se.employee_id = pr.expeditor_id
                WHERE se.supervisor_id = :supervisorId
                  AND pri.visit_plan_date = :visitPlanDate;
                """);
        if (filter.getExpeditorId() != null) {
            sql.append("AND pre.expeditor_id = :expeditorId");
        }
        Query query = entityManager.createNativeQuery(sql.toString(), Long.class);
        query.setParameter("supervisorId", filter.getSupervisorId());
        query.setParameter("visitPlanDate", filter.getVisitPlanDate());
        if (filter.getExpeditorId() != null) {
            query.setParameter("expeditorId", filter.getExpeditorId());
        }
        return (Long) query.getSingleResult();
    }

    @Override
    public ExpeditorCompletedStockOutDTO getExpeditorStockStatistics(SupervisorFilter filter) {
        StringBuilder sql = new StringBuilder("""
                SELECT
                    COUNT(s.id)                                       AS totalStocks,
                    COUNT(s.id) FILTER (WHERE s.status = 'COMPLETED') AS completedStocks,
                
                    -- all stock-outs
                    SUM(o.total_brutto)         AS totalBrutto,
                    SUM(o.total_netto)          AS totalNetto,
                    SUM(o.total_qty)            AS totalQty,
                    SUM(o.total_qty_in_package) AS totalQtyInPackage,
                
                    -- completed stock-out
                    SUM(CASE WHEN s.status = 'COMPLETED' THEN o.total_brutto ELSE 0 END)         AS completedTotalBrutto,
                    SUM(CASE WHEN s.status = 'COMPLETED' THEN o.total_netto ELSE 0 END)          AS completedTotalNetto,
                    SUM(CASE WHEN s.status = 'COMPLETED' THEN o.total_qty ELSE 0 END)            AS completedTotalQty,
                    SUM(CASE WHEN s.status = 'COMPLETED' THEN o.total_qty_in_package ELSE 0 END) AS completedTotalQtyInPackage
                FROM stock s
                         JOIN orders o ON s.order_id = o.id
                         JOIN supervisor_employees se ON o.expeditor_id = se.employee_id
                WHERE se.supervisor_id = :supervisorId
                    AND s.created_date BETWEEN :from AND :to
                    AND s.transfer_type = 'OUT'
                """);
        if (filter.getExpeditorId() != null) {
            sql.append("AND o.expeditor_id = :expeditorId");
        }
        Query query = entityManager.createNativeQuery(sql.toString(), ExpeditorCompletedStockOutDTO.class);
        query.setParameter("supervisorId", filter.getSupervisorId());
        query.setParameter("from", filter.getFrom());
        query.setParameter("to", filter.getTo());
        if (filter.getExpeditorId() != null) {
            query.setParameter("expeditorId", filter.getExpeditorId());
        }
        return (ExpeditorCompletedStockOutDTO) query.getSingleResult();
    }

    @Override
    public ExpeditorCancelledStockOutDTO getExpeditorStatistics(SupervisorExpeditorFilter filter) {
        StringBuilder sql = new StringBuilder("""
                  SELECT
                    COUNT(s.id)                                       AS totalStocks,
                    COUNT(s.id) FILTER (WHERE s.status = 'COMPLETED') AS completedStocks,
                
                    -- all stock-outs
                    SUM(o.total_brutto)         AS totalBrutto,
                    SUM(o.total_netto)          AS totalNetto,
                    SUM(o.total_qty)            AS totalQty,
                    SUM(o.total_qty_in_package) AS totalQtyInPackage,
                
                    -- completed stock-out
                    SUM(CASE WHEN s.status = 'CANCELLED' THEN o.total_brutto ELSE 0 END)         AS cancelledTotalBrutto,
                    SUM(CASE WHEN s.status = 'CANCELLED' THEN o.total_netto ELSE 0 END)          AS cancelledTotalNetto,
                    SUM(CASE WHEN s.status = 'CANCELLED' THEN o.total_qty ELSE 0 END)            AS cancelledTotalQty,
                    SUM(CASE WHEN s.status = 'CANCELLED' THEN o.total_qty_in_package ELSE 0 END) AS cancelledTotalQtyInPackage
                FROM stock s
                         JOIN orders o ON s.order_id = o.id
                         JOIN supervisor_employees se ON o.expeditor_id = se.employee_id
                WHERE se.supervisor_id = :supervisorId
                    AND s.created_date BETWEEN :from AND :to
                    AND s.transfer_type = 'OUT'
                """);
        if (filter.getExpeditorId() != null) {
            sql.append("AND o.expeditor_id = :expeditorId");
        }
        Query query = entityManager.createNativeQuery(sql.toString(), ExpeditorCancelledStockOutDTO.class);
        query.setParameter("supervisorId", filter.getSupervisorId());
        query.setParameter("from", filter.getFrom());
        query.setParameter("to", filter.getTo());
        if (filter.getExpeditorId() != null) {
            query.setParameter("expeditorId", filter.getExpeditorId());
        }
        return (ExpeditorCancelledStockOutDTO) query.getSingleResult();
    }

    @Override
    public ExpeditorOrderDTO getExpeditorOrderStatistics(SupervisorOrderFilter filter) {
        StringBuilder sql = new StringBuilder("""
                select COUNT(o.id)                                       AS countOrders,
                       COUNT(o.id) FILTER (WHERE o.status = 'COMPLETED') AS completedOrders
                from orders o
                         join supervisor_employees se on o.expeditor_id = se.employee_id
                where o.order_date between :from and :to
                  and se.supervisor_id = :supervisorId
                """);
        if (filter.getExpeditorIds() != null && !filter.getExpeditorIds().isEmpty()) {
            sql.append("AND o.expeditor_id in  (:expeditorIds)");
        }
        if (filter.getStatus() != null) {
            sql.append("AND o.status = :status");
        }
        Query query = entityManager.createNativeQuery(sql.toString(), ExpeditorOrderDTO.class);
        query.setParameter("supervisorId", filter.getSupervisorId());
        query.setParameter("from", filter.getFrom());
        query.setParameter("to", filter.getTo());
        if (filter.getExpeditorIds() != null && !filter.getExpeditorIds().isEmpty()) {
            query.setParameter("expeditorIds", filter.getExpeditorIds());
        }
        if (filter.getStatus() != null) {
            query.setParameter("status", filter.getStatus());
        }
        return (ExpeditorOrderDTO) query.getSingleResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResultList<Stock> getExpeditorStocks(SupervisorExpeditorFilter filter) {
        StringBuilder where = new StringBuilder("""
                FROM stock s
                    JOIN orders o ON s.order_id = o.id
                    JOIN supervisor_employees se ON o.expeditor_id = se.employee_id
                WHERE se.supervisor_id = :supervisorId
                    AND s.created_date BETWEEN :from AND :to
                    AND s.transfer_type = 'OUT'
                """);
        if (filter.getExpeditorId() != null) {
            where.append(" AND o.expeditor_id = :expeditorId ");
        }
        String sql = "SELECT s.* " + where + " ORDER BY s.created_date DESC";
        String countSql = "SELECT COUNT(*) " + where;

        Query query = entityManager.createNativeQuery(sql, Stock.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        Query countQuery = entityManager.createNativeQuery(countSql, Long.class);

        query.setParameter("supervisorId", filter.getSupervisorId());
        countQuery.setParameter("supervisorId", filter.getSupervisorId());

        query.setParameter("from", filter.getFrom());
        countQuery.setParameter("from", filter.getFrom());

        query.setParameter("to", filter.getTo());
        countQuery.setParameter("to", filter.getTo());

        if (filter.getExpeditorId() != null) {
            query.setParameter("expeditorId", filter.getExpeditorId());
            countQuery.setParameter("expeditorId", filter.getExpeditorId());
        }

        ResultList<Stock> resultList = new ResultList<>();
        Long count = (Long) countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }
}
