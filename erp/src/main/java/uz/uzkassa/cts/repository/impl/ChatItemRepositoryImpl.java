package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.cts.domain.hrms.ChatItem;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.custom.ChatItemRepositoryCustom;

/**
 * Created date time: 13.02.2025 17:00
 */

@SuppressWarnings("unused")
public class ChatItemRepositoryImpl extends BaseRepositoryImpl implements ChatItemRepositoryCustom {

    @Override
    public ResultList<ChatItem> getResultList(BaseFilter filter) {
        ResultList<ChatItem> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select ci from ChatItem ci");
        sql.append(" join ci.chat c");
        sql.append(" where ci.deleted = false");

        if (filter.getEntityId() != null) {
            sql.append(" and c.entityId = :entityId");
        }
        if (filter.getEntityType() != null) {
            sql.append(" and c.entityType = :entityType");
        }
        if (filter.getCreatorId() != null) {
            sql.append(" and ci.creatorId = :creatorId");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(ci.message)").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select ci", " select count(ci.id)");
        sql.append(" order by ci.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<ChatItem> query = entityManager
                .createQuery(sql.toString(), ChatItem.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (filter.getEntityId() != null) {
            query.setParameter("entityId", filter.getEntityId());
            countQuery.setParameter("entityId", filter.getEntityId());
        }
        if (filter.getEntityType() != null) {
            query.setParameter("entityType", filter.getEntityType());
            countQuery.setParameter("entityType", filter.getEntityType());
        }
        if (filter.getCreatorId() != null) {
            query.setParameter("creatorId", filter.getCreatorId());
            countQuery.setParameter("creatorId", filter.getCreatorId());
        }
        if (filter.isSearchNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchFor());
            countQuery.setParameter("searchKey", filter.getSearchFor());
        }

        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }
}
