package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.smartpossupply.domain.mark.MarkOrderParty;
import uz.uzkassa.smartpossupply.filters.MarkOrderPartyFilter;
import uz.uzkassa.smartpossupply.repository.MarkOrderPartyRepositoryCustom;

public class MarkOrderPartyRepositoryImpl extends BaseFilterableRepositoryImpl<MarkOrderParty, MarkOrderPartyFilter> implements MarkOrderPartyRepositoryCustom {

    @Override
    protected Class<MarkOrderParty> getEntityClass() {
        return MarkOrderParty.class;
    }

    @Override
    protected String getEntityAlias() {
        return "party";
    }

    @Override
    public void applyFilter(MarkOrderPartyFilter filter) {

    }

    @Override
    public String getBaseQuery(MarkOrderPartyFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM MarkOrderParty party ");

        // Add joins
        if (includeFetch) {
            sql.append("JOIN FETCH party.markOrderProduct mop ")
                .append("JOIN FETCH mop.product p ")
                .append("JOIN FETCH mop.markOrder mo ");
        } else {
            sql.append("JOIN party.markOrderProduct mop ")
                .append("JOIN mop.product p ")
                .append("JOIN mop.markOrder mo ");
        }

        // Base conditions
        sql.append("WHERE mop.id=:markOrderProductId ")
            .append("AND party.deleted = false ");

        // Search conditions
        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(p.name)").append(filter.getSearchKey())
                .append(" OR LOWER(p.barcode)").append(filter.getSearchKey())
                .append(" OR LOWER(p.vatBarcode)").append(filter.getSearchKey())
                .append(") ");
        }

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, MarkOrderPartyFilter filter) {
        // Required parameter
        query.setParameter("markOrderProductId", filter.getMarkOrderProductId());

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }
}
