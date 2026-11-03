package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.smartpossupply.domain.mailing.MailingListItem;
import uz.uzkassa.smartpossupply.filters.MailingListItemFilter;
import uz.uzkassa.smartpossupply.repository.MailingListItemRepositoryCustom;

@SuppressWarnings("unused")
public class MailingListItemRepositoryImpl extends BaseFilterableRepositoryImpl<MailingListItem, MailingListItemFilter> implements MailingListItemRepositoryCustom {

    @Override
    protected Class<MailingListItem> getEntityClass() {
        return MailingListItem.class;
    }

    @Override
    protected String getEntityAlias() {
        return "mli";
    }

    @Override
    public void applyFilter(MailingListItemFilter filter) {
    }

    @Override
    public String getBaseQuery(MailingListItemFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM MailingListItem mli ");

        if (includeFetch) {
            sql.append("JOIN FETCH mli.mailingList ml ")
                .append("JOIN FETCH mli.customer cus ");
        } else {
            sql.append("JOIN mli.mailingList ml ")
                .append("JOIN mli.customer cus ");
        }
        if (filter.getCompanyId() != null) {
            sql.append("WHERE ml.company.id = :companyId ");
        }

        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(cus.phone)").append(filter.getSearchKey())
                .append(" OR LOWER(cus.tin)").append(filter.getSearchKey())
                .append(" OR LOWER(cus.name)").append(filter.getSearchKey())
                .append(") ");
        }

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, MailingListItemFilter filter) {
        if (filter.getCompanyId() != null) {
            query.setParameter("companyId", filter.getCompanyId());
        }
        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }
}
