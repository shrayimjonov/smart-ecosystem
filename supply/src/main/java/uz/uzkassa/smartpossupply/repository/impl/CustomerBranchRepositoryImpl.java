package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.customerbranch.CustomerBranch;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.filters.CustomerBranchFilter;
import uz.uzkassa.smartpossupply.repository.CustomerBranchRepositoryCustom;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 19.01.2023 15:07
 */

public class CustomerBranchRepositoryImpl
    extends BaseFilterableRepositoryImpl<CustomerBranch, CustomerBranchFilter>
    implements CustomerBranchRepositoryCustom {

    @Override
    protected Class<CustomerBranch> getEntityClass() {
        return CustomerBranch.class;
    }

    @Override
    protected String getEntityAlias() {
        return "cb";
    }

    @Override
    public void applyFilter(CustomerBranchFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
    }

    @Override
    public String getBaseQuery(CustomerBranchFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM CustomerBranch cb ");

        // Add joins
        if (includeFetch) {
            sql.append("JOIN FETCH cb.customer c ")
                .append("LEFT JOIN FETCH cb.branch b ")
                .append("LEFT JOIN FETCH cb.warehouse w ")
                .append("LEFT JOIN FETCH cb.address a ");
        } else {
            sql.append("JOIN cb.customer c ")
                .append("LEFT JOIN cb.branch b ")
                .append("LEFT JOIN cb.warehouse w ")
                .append("LEFT JOIN cb.address a ");

        }

        // Inner join with Partners (never use FETCH for this)
        sql.append("INNER JOIN Partners p ON p.customerId = c.id AND p.supplierId= :companyId ");

        // Base conditions
        sql.append("WHERE cb.deleted = false ")
            .append("AND (c.status = 'ACTIVE' OR c.status IS NULL) ");

        // Dynamic conditions
        appendCondition(sql, filter.getCompanyId() != null, "AND p.companyId = :companyId ");
        appendCondition(sql, filter.getCustomerId() != null, "AND cb.customerId = :customerId ");
        appendCondition(sql, filter.getBranchId() != null, "AND cb.branchId = :branchId ");
        appendCondition(sql, filter.getWarehouseId() != null, "AND cb.warehouseId = :warehouseId ");
        appendCondition(sql, StringUtils.hasText(filter.getStatus()), "AND cb.status = :status ");
        appendCondition(sql, filter.getRegionId() != null, "AND cb.address.regionId = :regionId ");
        appendCondition(sql, filter.getDistrictId() != null, "AND cb.address.districtId = :districtId ");
        appendCondition(sql, filter.getTin() != null, "AND cb.customer.tin = :tin ");

        // Boolean condition for validateCode
        if (filter.getValidateCode() != null && filter.getValidateCode()) {
            sql.append("AND cb.customerBranchNumber IS NOT NULL ");
        }

        appendCondition(sql, filter.getLastSyncDate() != null, "AND cb.updatedDate >= :lastSyncDate ");

        // Search conditions
        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(cb.name)").append(filter.getSearchKey())
                .append(" OR LOWER(cb.customerBranchNumber)").append(filter.getSearchKey())
                .append(" OR LOWER(c.name)").append(filter.getSearchKey())
                .append(" OR LOWER(c.tin)").append(filter.getSearchKey())
                .append(") ");
        }

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, CustomerBranchFilter filter) {
        // Optional parameters
        setParameterIfNotNull(query, "companyId", filter.getCompanyId());
        setParameterIfNotNull(query, "customerId", filter.getCustomerId());
        setParameterIfNotNull(query, "branchId", filter.getBranchId());
        setParameterIfNotNull(query, "warehouseId", filter.getWarehouseId());
        setParameterIfNotNull(query, "regionId", filter.getRegionId());
        setParameterIfNotNull(query, "districtId", filter.getDistrictId());
        setParameterIfNotNull(query, "tin", filter.getTin());

        if (StringUtils.hasText(filter.getStatus())) {
            query.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
        }

        setParameterIfNotNull(query, "lastSyncDate", filter.getLastSyncDate());

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }

    }

    /**
     * Override to use DISTINCT in queries
     */
    @Override
    public ResultList<CustomerBranch> getResultList(CustomerBranchFilter filter) {
        applyFilter(filter);

        // Build count query with DISTINCT
        String countSql = "SELECT COUNT(DISTINCT cb.id) " + getBaseQuery(filter, false);
        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);
        setParameters(countQuery, filter);

        Long count = countQuery.getSingleResult();
        if (count == 0) {
            return new ResultList<>();
        }

        // Build data query with DISTINCT
        String sql = "SELECT DISTINCT cb " + getBaseQuery(filter, true) +
            String.format(" ORDER BY cb.%s %s", filter.getOrderBy(), filter.getSortOrder().getName());

        TypedQuery<CustomerBranch> query = entityManager
            .createQuery(sql, CustomerBranch.class)
            .setFirstResult(filter.getStart())
            .setMaxResults(filter.getSize());

        setParameters(query, filter);

        ResultList<CustomerBranch> resultList = new ResultList<>();
        resultList.setCount(count);
        resultList.setList(query.getResultList());

        return resultList;
    }
}
