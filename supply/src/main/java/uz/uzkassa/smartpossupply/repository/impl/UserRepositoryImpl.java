package uz.uzkassa.smartpossupply.repository.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.TypedQuery;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.dto.billingpermission.BillingCompanyItem;
import uz.uzkassa.smartpossupply.dto.branch.CommonStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.employee.EmployeeCountByRoleDTO;
import uz.uzkassa.smartpossupply.enums.Role;
import uz.uzkassa.smartpossupply.enums.UserStatus;
import uz.uzkassa.smartpossupply.filters.UserFilter;
import uz.uzkassa.smartpossupply.repository.UserRepositoryCustom;
import uz.uzkassa.smartpossupply.utils.QueryUtils;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class UserRepositoryImpl extends BaseFilterableRepositoryImpl<User, UserFilter> implements UserRepositoryCustom {

    @Override
    protected Class<User> getEntityClass() {
        return User.class;
    }

    @Override
    protected String getEntityAlias() {
        return "u";
    }

    @Override
    public void applyFilter(UserFilter filter) {
        if (!SecurityUtils.isFromAdmin()) {
            filter.setCompanyId(getCompanyId(filter, false));
            filter.setBranchId(getBranchId(filter, false));
            filter.setWarehouseId(getWarehouseId(filter, false));
        }
    }

    @Override
    public String getBaseQuery(UserFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM User u ");

        // Add joins
        if (includeFetch) {
            sql.append("LEFT JOIN FETCH u.company c ")
                .append("LEFT JOIN FETCH u.branch b ")
                .append("LEFT JOIN FETCH u.warehouse w ");
        } else {
            sql.append("LEFT JOIN u.company c ")
                .append("LEFT JOIN u.branch b ")
                .append("LEFT JOIN u.warehouse w ");
        }

        // Base condition
        sql.append("WHERE u.deleted = false ");

        // Dynamic conditions
        appendCondition(sql, filter.getCompanyId() != null, "AND c.id = :companyId ");
        appendCondition(sql, filter.getBranchId() != null, "AND b.id = :branchId ");
        appendCondition(sql, filter.getWarehouseId() != null, "AND w.id = :warehouseId ");
        appendCondition(sql, filter.getSupervisorId() != null, "AND u.supervisorId = :supervisorId ");
        appendCondition(sql, filter.getManagerId() != null, "AND u.id = :managerId ");

        // Boolean condition for supervisor
        if (filter.getOnlyHasSupervisor() != null) {
            if (filter.getOnlyHasSupervisor()) {
                sql.append("AND u.supervisorId IS NOT NULL ");
            } else {
                sql.append("AND u.supervisorId IS NULL ");
            }
        }

        appendCondition(sql, filter.getValidatePinfl() != null && filter.getValidatePinfl(), "AND u.pinfl IS NOT NULL ");

        // Role conditions (single role OR multiple roles)
        if (StringUtils.hasText(filter.getRole())) {
            sql.append("AND u.role = :role ");
        } else if (!CollectionUtils.isEmpty(filter.getRoles())) {
            sql.append("AND u.role IN (:roles) ");
        }

        appendCondition(sql, filter.getStatus() != null, "AND u.status = :status ");
        appendCondition(sql, filter.getFrom() != null, "AND u.createdDate >= :fromDate ");
        appendCondition(sql, filter.getTo() != null, "AND u.createdDate <= :toDate ");

        // Search conditions
        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(u.firstName)").append(filter.getSearchKey())
                .append(" OR LOWER(u.lastName)").append(filter.getSearchKey())
                .append(" OR LOWER(u.patronymic)").append(filter.getSearchKey())
                .append(" OR LOWER(u.login)").append(filter.getSearchKey())
                .append(") ");
        }

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, UserFilter filter) {
        // Optional parameters
        setParameterIfNotNull(query, "companyId", filter.getCompanyId());
        setParameterIfNotNull(query, "branchId", filter.getBranchId());
        setParameterIfNotNull(query, "warehouseId", filter.getWarehouseId());
        setParameterIfNotNull(query, "supervisorId", filter.getSupervisorId());
        setParameterIfNotNull(query, "managerId", filter.getManagerId());

        // Role parameters (mutually exclusive)
        if (StringUtils.hasText(filter.getRole())) {
            query.setParameter("role", Role.getByCode(filter.getRole()));
        } else if (!CollectionUtils.isEmpty(filter.getRoles())) {
            query.setParameter("roles", filter.getRoles());
        }

        if (StringUtils.hasText(filter.getStatus())) {
            query.setParameter("status", UserStatus.valueOf(filter.getStatus()));
        }

        setParameterIfNotNull(query, "fromDate", filter.getFrom());
        setParameterIfNotNull(query, "toDate", filter.getTo());

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }

    @Override
    public CommonStatisticsDTO getStatistics(UserFilter filter) {
        applyFilter(filter);

        String sql = "SELECT " +
            "(CASE WHEN u.status = 'ACTIVE' THEN COUNT(DISTINCT u.id) ELSE 0 END) AS activeTotal, " +
            "(CASE WHEN u.status = 'IN_ACTIVE' THEN COUNT(DISTINCT u.id) ELSE 0 END) AS inActiveTotal " +
            getBaseQuery(filter, false) +
            " GROUP BY u.status";

        TypedQuery<Object[]> query = entityManager.createQuery(sql, Object[].class);
        setParameters(query, filter);

        List<Object[]> results = query.getResultList();
        Long activeTotal = 0L;
        Long inActiveTotal = 0L;

        for (Object[] row : results) {
            activeTotal += (Long) row[0];
            inActiveTotal += (Long) row[1];
        }

        return new CommonStatisticsDTO(activeTotal, inActiveTotal);
    }

    @Override
    public List<EmployeeCountByRoleDTO> getCountByRole(String tin, Long companyId) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT e.role, COUNT(e.id)")
                .append(" FROM User e")
                .append(" JOIN Company c on e.companyId = c.id")
                .append(" WHERE e.deleted IS FALSE")
                .append(" AND e.status = 'ACTIVE'")
                .append(" AND e.role in (").append(QueryUtils.getSqlFormat(List.of(Role.ROLE_AGENT.getCode(), Role.ROLE_EXPEDITOR.getCode(), Role.ROLE_CASHIER.getCode()))).append(")");
        if (companyId != null) {
            sql.append(" AND c.id = ").append(companyId);
        } else {
            sql.append(" AND c.tin = '").append(tin).append("'");
        }
        sql.append(" GROUP BY e.role");

        TypedQuery<Object[]> query = entityManager.createQuery(sql.toString(), Object[].class);

        List<Object[]> results = query.getResultList();
        List<EmployeeCountByRoleDTO> resultList = new ArrayList<>();
        for (Object[] row : results) {
            EmployeeCountByRoleDTO employeeCountByRoleDTO = new EmployeeCountByRoleDTO();
            employeeCountByRoleDTO.setRole((Role) row[0]);
            employeeCountByRoleDTO.setCount((Long) row[1]);
            resultList.add(employeeCountByRoleDTO);
        }

        long onlineKKMCount = resultList.stream()
                .filter(dto -> dto.getRole() == Role.ROLE_EXPEDITOR || dto.getRole() == Role.ROLE_CASHIER)
                .mapToLong(EmployeeCountByRoleDTO::getCount)
                .sum();

        EmployeeCountByRoleDTO onlineKKM = new EmployeeCountByRoleDTO();
        onlineKKM.setRole(Role.ONLINE_KKM);
        onlineKKM.setCount(onlineKKMCount);
        resultList.add(onlineKKM);

        return resultList;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<BillingCompanyItem> getCompanyItems(String tin, Long companyId) {
        String sql = """
                WITH root AS (SELECT id, name, tin
                              FROM company
                              WHERE tin = :tin),
                     companies AS (SELECT c.id, c.name, c.tin
                                   FROM company c
                                   WHERE (c.id = (SELECT id FROM root) OR c.parent_id = (SELECT id FROM root))
                                     AND c.status = 'ACTIVE'
                                     AND c.deleted = FALSE
                                     AND c.tin IS NOT NULL),
                     branch_counts AS (SELECT b.company_id,
                                              json_build_object(
                                                      'count', count(id),
                                                      'branches', json_agg(json_build_object('id', b.id, 'name', b.name))
                                              ) AS activeBranches
                                       FROM branch b
                                       WHERE b.company_id IN (SELECT id FROM companies)
                                         AND b.deleted = FALSE
                                         AND b.status = 'ACTIVE'
                                       GROUP BY b.company_id),
                     user_roles AS (SELECT u.company_id,
                                           json_agg(
                                                   json_build_object(
                                                           'role', u.role,
                                                           'count', u.count,
                                                           'users', u.users
                                                   )
                                           ) AS activeUsers
                                    FROM (SELECT u.company_id,
                                                 u.role,
                                                 COUNT(u.id) AS count,
                                                 json_agg(json_build_object(
                                                         'id', id,
                                                         'fullName', u.first_name || ' ' || u.last_name
                                                          )
                                                 ) AS users
                                          FROM users u
                                          WHERE u.company_id IN (SELECT id FROM companies)
                                            AND u.status = 'ACTIVE'
                                            AND u.role IS NOT NULL
                                            AND u.deleted = FALSE
                                            AND u.role IN (:roles)
                                          GROUP BY u.company_id, u.role) u
                                    GROUP BY u.company_id),
                     terminal_info AS (SELECT t.company_id,
                                              json_build_array(json_build_object(
                                                      'count', COUNT(t.id),
                                                      'serialNumbers', json_agg(t.serial_number)
                                                    )
                                              ) AS activeOnlineKKMs
                                       FROM terminal t
                                       WHERE t.company_id IN (SELECT id FROM companies)
                                         AND t.status = 'ACTIVE'
                                         AND t.deleted = FALSE
                                       GROUP BY t.company_id)
                SELECT c.id                                AS id,
                       c.name                              AS name,
                       c.tin                               AS tin,
                       COALESCE(bc.activeBranches, '{}'::json)   AS activeBranches,
                       COALESCE(ur.activeUsers, '[]'::json)      AS activeUsers,
                       COALESCE(ti.activeOnlineKKMs, '[]'::json) AS activeOnlineKKMs
                FROM companies c
                         LEFT JOIN branch_counts bc ON bc.company_id = c.id
                         LEFT JOIN user_roles ur ON ur.company_id = c.id
                         LEFT JOIN terminal_info ti ON ti.company_id = c.id;
                """;

        List<String> roles = List.of(
                Role.ROLE_AGENT.getCode(),
                Role.ROLE_EXPEDITOR.getCode(),
                Role.ROLE_CASHIER.getCode()
        );

        List<Object[]> rows = entityManager.createNativeQuery(sql)
                .setParameter("tin", tin)
                .setParameter("roles", roles)
                .getResultList();

        ObjectMapper objectMapper = new ObjectMapper();

        return rows.stream().map(row -> {
            try {
                return new BillingCompanyItem(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        (String) row[2],
                        objectMapper.readTree(row[3].toString()),
                        objectMapper.readTree(row[4].toString()),
                        objectMapper.readTree(row[5].toString()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to parse roles JSON", e);
            }
        }).toList();
    }
}
