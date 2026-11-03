package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.maillinglist.MailingListItemListDTO;
import uz.uzkassa.cts.filter.MailingListItemFilter;
import uz.uzkassa.cts.repository.custom.MailingListItemRepositoryCustom;

import java.util.List;

@SuppressWarnings("unused")
public class MailingListItemRepositoryImpl extends BaseRepositoryImpl implements MailingListItemRepositoryCustom {

    @Override
    public ResultList<MailingListItemListDTO> getResultList(MailingListItemFilter filter) {
        ResultList<MailingListItemListDTO> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select mli.id, ml.id mailingListId, ml.name as mailingListName,");
        sql.append(" mli.status status, mli.phone phone, ");
        sql.append(" mli.entity_id entityId, mli.entity_type entityType,");
        sql.append(" c.name as customerName, l.name as leadName ");
        sql.append(getBaseSql(filter));

        StringBuilder countSql = new StringBuilder();
        countSql.append("select count(mli.id) ");
        countSql.append(getBaseSql(filter));

        sql.append(" order by mli.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());
        sql.append(" offset ").append(filter.getStart()).append(" limit ").append(filter.getSize());

        Query countQuery = entityManager.createNativeQuery(countSql.toString());
        long count = ((Long) countQuery.getSingleResult());
        if (count > 0) {
            Query query = entityManager.createNativeQuery(sql.toString(), "MailingListItemMapper");
            resultList.setList((List<MailingListItemListDTO>) query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }

    private StringBuilder getBaseSql(MailingListItemFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from mailing_list_item mli ");
        sql.append(" inner join mailing_list ml on ml.id = mli.mailing_list_id ");
        sql.append(" left join customer c on mli.entity_id = c.id ");
        sql.append(" left join lead l on mli.entity_id = l.id ");
        sql.append(" where 1=1 ");

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and mli.status ='").append(filter.getStatus()).append("'");
        }
        if (filter.getMailingListId() != null) {
            sql.append(" and mli.mailing_list_id = ").append(filter.getMailingListId());
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" lower(mli.phone) ").append(filter.getLikeSearch());
        }
        return sql;
    }
}
