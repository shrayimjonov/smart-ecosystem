package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.pricelist.PriceList;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.filters.PriceListFilter;
import uz.uzkassa.smartpossupply.repository.PriceListRepositoryCustom;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 04.02.2023 13:46
 */
public class PriceListRepositoryImpl
    extends BaseFilterableRepositoryImpl<PriceList, PriceListFilter>
    implements PriceListRepositoryCustom {

    @Override
    protected Class<PriceList> getEntityClass() {
        return PriceList.class;
    }

    @Override
    protected String getEntityAlias() {
        return "pl";
    }

    @Override
    public void applyFilter(PriceListFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
        filter.setBranchId(getBranchId(filter, false));
    }

    @Override
    public String getBaseQuery(PriceListFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM PriceList pl ");

        // Base join
        if (includeFetch) {
            sql.append("JOIN FETCH pl.branch b ");
        } else {
            sql.append("JOIN pl.branch b ");
        }

        // Conditional joins (never use FETCH for these filter-based joins)
        if (filter.getCustomerId() != null) {
            sql.append("LEFT JOIN pl.customers c ");
        }
        if (filter.getProductIds() != null) {
            sql.append("LEFT JOIN pl.items i ");
        }

        // Base conditions
        sql.append("WHERE pl.deleted = false ")
            .append("AND b.companyId = :companyId ");

        // Dynamic conditions
        appendCondition(sql, filter.getBranchId() != null, "AND b.id = :branchId ");

        if (filter.getCustomerId() != null) {
            sql.append("AND ((:customerId IN (c.id)) OR pl.applyToAllCustomers = true) ");
        }

        appendCondition(sql, filter.getProductIds() != null, "AND i.productId IN (:productIds) ");
        appendCondition(sql, StringUtils.hasText(filter.getStatus()), "AND pl.status = :status ");
        appendCondition(sql, filter.getFrom() != null, "AND pl.expireDate >= :fromDate ");
        appendCondition(sql, filter.getTo() != null, "AND pl.expireDate <= :toDate ");

        // Search conditions
        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(pl.name)").append(filter.getSearchKey())
                .append(" OR LOWER(pl.priceListNumber)").append(filter.getSearchKey())
                .append(" OR LOWER(pl.description)").append(filter.getSearchKey())
                .append(") ");
        }

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, PriceListFilter filter) {
        // Required parameter
        query.setParameter("companyId", filter.getCompanyId());

        // Optional parameters
        setParameterIfNotNull(query, "branchId", filter.getBranchId());
        setParameterIfNotNull(query, "customerId", filter.getCustomerId());
        setParameterIfNotNull(query, "productIds", filter.getProductIds());

        if (StringUtils.hasText(filter.getStatus())) {
            query.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
        }

        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom().toLocalDate());
        }

        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo().toLocalDate());
        }

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }
}
