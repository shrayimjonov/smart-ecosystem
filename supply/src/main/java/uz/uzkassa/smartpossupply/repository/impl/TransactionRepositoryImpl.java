package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.transaction.Transaction;
import uz.uzkassa.smartpossupply.filters.TransactionFilter;
import uz.uzkassa.smartpossupply.repository.TransactionRepositoryCustom;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date:08.02.2023 12:35
 */
public class TransactionRepositoryImpl
    extends BaseFilterableRepositoryImpl<Transaction, TransactionFilter>
    implements TransactionRepositoryCustom {

    @Override
    protected Class<Transaction> getEntityClass() {
        return Transaction.class;
    }

    @Override
    protected String getEntityAlias() {
        return "t";
    }

    @Override
    public void applyFilter(TransactionFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
    }

    @Override
    public String getBaseQuery(TransactionFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM Transaction t ");

        // Base condition
        sql.append("WHERE t.companyId = :companyId ");

        // Dynamic conditions
        appendCondition(sql, StringUtils.hasText(filter.getStatus()), "AND t.status = :status ");
        appendCondition(sql, filter.getFrom() != null, "AND t.createdDate >= :fromDate ");
        appendCondition(sql, filter.getTo() != null, "AND t.createdDate <= :toDate ");
        appendCondition(sql, filter.getEntityType() != null, "AND t.entityType = :entityType ");

        // Search conditions
        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(t.transactionNumber)").append(filter.getSearchKey())
                .append(" OR LOWER(t.description)").append(filter.getSearchKey())
                .append(") ");
        }

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, TransactionFilter filter) {
        // Required parameter
        query.setParameter("companyId", filter.getCompanyId());

        // Optional parameters
        if (StringUtils.hasText(filter.getStatus())) {
            query.setParameter("status", filter.getStatus());
        }

        setParameterIfNotNull(query, "fromDate", filter.getFrom());
        setParameterIfNotNull(query, "toDate", filter.getTo());

        if (filter.getEntityType() != null) {
            query.setParameter("entityType", filter.getEntityType().name());
        }

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }
}
