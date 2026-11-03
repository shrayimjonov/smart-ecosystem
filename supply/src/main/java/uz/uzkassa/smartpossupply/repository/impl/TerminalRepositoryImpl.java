package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.Terminal;
import uz.uzkassa.smartpossupply.dto.branch.CommonStatisticsDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.LauncherStatus;
import uz.uzkassa.smartpossupply.filters.TerminalFilter;
import uz.uzkassa.smartpossupply.repository.TerminalRepositoryCustom;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 03.12.2024
 */

public class TerminalRepositoryImpl extends BaseFilterableRepositoryImpl<Terminal, TerminalFilter> implements TerminalRepositoryCustom {

    @Override
    protected Class<Terminal> getEntityClass() {
        return Terminal.class;
    }

    @Override
    protected String getEntityAlias() {
        return "t";
    }

    @Override
    public void applyFilter(TerminalFilter filter) {
    }

    @Override
    public String getBaseQuery(TerminalFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM Terminal t ");
        if (includeFetch) {
            sql.append("LEFT JOIN FETCH t.branch b ");
        } else {
            sql.append("LEFT JOIN t.branch b ");
        }
        sql.append("WHERE t.deleted = false ");

        appendCondition(sql, filter.getCompanyId() != null, "AND t.companyId = :companyId ");
        appendCondition(sql, filter.getBranchId() != null, "AND t.branchId = :branchId ");
        appendCondition(sql, StringUtils.hasText(filter.getStatus()), "AND t.status = :status ");
        appendCondition(sql, StringUtils.hasText(filter.getLauncherStatus()), "AND t.launcherStatus = :launcherStatus ");

        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(t.serialNumber)").append(filter.getSearchKey())
                .append(" OR LOWER(t.fiscalModuleSerialNumber)").append(filter.getSearchKey())
                .append(") ");
        }

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, TerminalFilter filter) {
        setParameterIfNotNull(query, "companyId", filter.getCompanyId());
        setParameterIfNotNull(query, "branchId", filter.getBranchId());
        if (StringUtils.hasText(filter.getStatus())) {
            query.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
        }
        if (StringUtils.hasText(filter.getLauncherStatus())) {
            query.setParameter("launcherStatus", LauncherStatus.valueOf(filter.getLauncherStatus()));
        }
        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }

    @Override
    public CommonStatisticsDTO getStatistics(TerminalFilter filter) {
        applyFilter(filter);

        String sql = "SELECT " +
            "SUM(CASE WHEN t.status = 'ACTIVE' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN t.status = 'IN_ACTIVE' THEN 1 ELSE 0 END) " +
            getBaseQuery(filter, false);

        TypedQuery<Object[]> query = entityManager.createQuery(sql, Object[].class);
        setParameters(query, filter);

        Object[] result = query.getSingleResult();
        Long activeTotal = ((Number) result[0]).longValue();
        Long inActiveTotal = ((Number) result[1]).longValue();

        return new CommonStatisticsDTO(activeTotal, inActiveTotal);
    }
}