package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.mailing.ScheduleMessage;
import uz.uzkassa.smartpossupply.enums.ScheduleMessageStatus;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.repository.ScheduleMessageRepositoryCustom;


@SuppressWarnings("unused")
public class ScheduleMessageRepositoryImpl extends BaseFilterableRepositoryImpl<ScheduleMessage, BaseFilter> implements ScheduleMessageRepositoryCustom {

    @Override
    protected Class<ScheduleMessage> getEntityClass() {
        return ScheduleMessage.class;
    }

    @Override
    protected String getEntityAlias() {
        return "sm";
    }

    @Override
    public void applyFilter(BaseFilter filter) {
        // No custom filter logic needed for this repository
    }

    @Override
    public String getBaseQuery(BaseFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM ScheduleMessage sm ");

        // Base condition
        sql.append("WHERE sm.deleted = false ");

        // Dynamic conditions
        appendCondition(sql, StringUtils.hasText(filter.getStatus()), "AND sm.status = :status ");
        appendCondition(sql, filter.getFrom() != null, "AND sm.scheduleDate >= :fromDate ");
        appendCondition(sql, filter.getTo() != null, "AND sm.scheduleDate <= :toDate ");

        // Search conditions
        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(sm.subject)").append(filter.getSearchKey())
                .append(") ");
        }

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, BaseFilter filter) {
        // Optional parameters
        if (StringUtils.hasText(filter.getStatus())) {
            query.setParameter("status", ScheduleMessageStatus.valueOf(filter.getStatus()));
        }

        setParameterIfNotNull(query, "fromDate", filter.getFrom());
        setParameterIfNotNull(query, "toDate", filter.getTo());

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }
}
