package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.acceptancetransferact.AcceptanceTransferAct;
import uz.uzkassa.smartpossupply.enums.AcceptanceTransferActStatus;
import uz.uzkassa.smartpossupply.filters.AcceptanceTransferActFilter;
import uz.uzkassa.smartpossupply.repository.AcceptanceTransferActRepositoryCustom;

public class AcceptanceTransferActRepositoryImpl extends BaseFilterableRepositoryImpl<AcceptanceTransferAct, AcceptanceTransferActFilter> implements AcceptanceTransferActRepositoryCustom {

    @Override
    protected Class<AcceptanceTransferAct> getEntityClass() {
        return AcceptanceTransferAct.class;
    }

    @Override
    protected String getEntityAlias() {
        return "ata";
    }

    @Override
    public void applyFilter(AcceptanceTransferActFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
        filter.setBranchId(getBranchId(filter, false));
    }

    @Override
    public String getBaseQuery(AcceptanceTransferActFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM AcceptanceTransferAct ata ");

        sql.append("JOIN ata.company c ");

        if (includeFetch) {
            sql.append("JOIN FETCH ata.buyer buyer ")
                .append("JOIN FETCH ata.seller seller ")
                .append("LEFT JOIN FETCH ata.branch b ");
        } else {
            sql.append("JOIN ata.buyer buyer ")
                .append("JOIN ata.seller seller ")
                .append("LEFT JOIN ata.branch b ");
        }

        // Base conditions
        sql.append("WHERE ata.deleted = false ")
            .append("AND c.id = :companyId ")
            .append("AND ata.outgoing = :outgoing ");

        // Dynamic conditions
        appendCondition(sql, filter.getSellerId() != null, "AND ata.sellerId = :sellerId ");
        appendCondition(sql, filter.getBranchId() != null, "AND ata.branchId = :branchId ");
        appendCondition(sql, filter.getBuyerId() != null, "AND ata.buyerId = :buyerId ");
        appendCondition(sql, StringUtils.hasText(filter.getStatus()), "AND ata.status = :status ");
        appendCondition(sql, filter.getFrom() != null, "AND ata.actDate >= :fromDate ");
        appendCondition(sql, filter.getTo() != null, "AND ata.actDate <= :toDate ");

        // Search conditions
        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(ata.actId) ").append(filter.getSearchKey())
                .append(" OR LOWER(ata.actNo) ").append(filter.getSearchKey())
                .append(" OR LOWER(ata.sellerPinfl) ").append(filter.getSearchKey())
                .append(" OR LOWER(ata.buyerTinOrPinfl) ").append(filter.getSearchKey())
                .append(" OR LOWER(c.tin) ").append(filter.getSearchKey())
                .append(" OR LOWER(c.name) ").append(filter.getSearchKey())
                .append(") ");
        }

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, AcceptanceTransferActFilter filter) {

        query.setParameter("companyId", filter.getCompanyId());
        query.setParameter("outgoing", !filter.isIncoming());

        setParameterIfNotNull(query, "sellerId", filter.getSellerId());
        setParameterIfNotNull(query, "branchId", filter.getBranchId());
        setParameterIfNotNull(query, "buyerId", filter.getBuyerId());

        if (StringUtils.hasText(filter.getStatus())) {
            query.setParameter("status", AcceptanceTransferActStatus.valueOf(filter.getStatus()));
        }

        setParameterIfNotNull(query, "fromDate", filter.getFrom());
        setParameterIfNotNull(query, "toDate", filter.getTo());

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }
}
