package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.smartpossupply.domain.customerbranch.CustomerBranchProductHistory;
import uz.uzkassa.smartpossupply.filters.CustomerBranchProductHistoryFilter;
import uz.uzkassa.smartpossupply.repository.CustomerBranchProductHistoryRepositoryCustom;

@SuppressWarnings("unused")
public class CustomerBranchProductHistoryRepositoryImpl extends BaseFilterableRepositoryImpl<CustomerBranchProductHistory, CustomerBranchProductHistoryFilter> implements CustomerBranchProductHistoryRepositoryCustom {

    @Override
    protected Class<CustomerBranchProductHistory> getEntityClass() {
        return CustomerBranchProductHistory.class;
    }

    @Override
    protected String getEntityAlias() {
        return "cbph";
    }

    @Override
    public void applyFilter(CustomerBranchProductHistoryFilter filter) {
    }

    @Override
    public String getBaseQuery(CustomerBranchProductHistoryFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM CustomerBranchProductHistory cbph ");

        if (includeFetch) {
            sql.append("JOIN FETCH cbph.product p ")
                .append("JOIN FETCH cbph.planRouteCustomerBranch prcb ");
        } else {
            sql.append("JOIN cbph.product p ")
                .append("JOIN cbph.planRouteCustomerBranch prcb ");
        }

        sql.append("WHERE cbph.deleted = false ");
        sql.append("AND prcb.id = :planRouteCustomerBranchId ");

        appendCondition(sql, filter.getFrom() != null, "AND cbph.createdDate >= :fromDate ");
        appendCondition(sql, filter.getTo() != null, "AND cbph.createdDate <= :toDate ");

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, CustomerBranchProductHistoryFilter filter) {
        // Required parameters
        query.setParameter("planRouteCustomerBranchId", filter.getPlanRouteCustomerBranchId());

        // Optional parameters
        setParameterIfNotNull(query, "fromDate", filter.getFrom());
        setParameterIfNotNull(query, "toDate", filter.getTo());
    }
}
