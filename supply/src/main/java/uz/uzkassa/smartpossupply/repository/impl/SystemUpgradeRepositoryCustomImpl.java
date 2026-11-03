package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.SystemUpgrade;
import uz.uzkassa.smartpossupply.enums.PlatformType;
import uz.uzkassa.smartpossupply.filters.SystemUpgradeFilter;
import uz.uzkassa.smartpossupply.repository.SystemUpgradeRepositoryCustom;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 26.11.2024
 */
public class SystemUpgradeRepositoryCustomImpl
    extends BaseFilterableRepositoryImpl<SystemUpgrade, SystemUpgradeFilter>
    implements SystemUpgradeRepositoryCustom {

    @Override
    protected Class<SystemUpgrade> getEntityClass() {
        return SystemUpgrade.class;
    }

    @Override
    protected String getEntityAlias() {
        return "su";
    }

    @Override
    public void applyFilter(SystemUpgradeFilter filter) {
        // No custom filter logic needed for this repository
    }

    @Override
    public String getBaseQuery(SystemUpgradeFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM SystemUpgrade su ");

        // Base condition
        sql.append("WHERE 1=1 ");

        // Dynamic conditions
        appendCondition(sql, filter.getAvailable() != null, "AND su.available = :available ");
        appendCondition(sql, StringUtils.hasText(filter.getPlatformType()), "AND su.platformType = :platformType ");
        appendCondition(sql, filter.getFrom() != null, "AND su.releaseDate >= :fromDate ");
        appendCondition(sql, filter.getTo() != null, "AND su.releaseDate <= :toDate ");

        // Search conditions
        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(su.version)").append(filter.getSearchKey())
                .append(") ");
        }

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, SystemUpgradeFilter filter) {
        // Optional parameters
        setParameterIfNotNull(query, "available", filter.getAvailable());

        if (StringUtils.hasText(filter.getPlatformType())) {
            query.setParameter("platformType", PlatformType.valueOf(filter.getPlatformType()));
        }

        setParameterIfNotNull(query, "fromDate", filter.getFrom());
        setParameterIfNotNull(query, "toDate", filter.getTo());

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }
}
