package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.cts.domain.Note;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.custom.NoteRepositoryCustom;

@SuppressWarnings("unused")
public class NoteRepositoryImpl extends BaseRepositoryImpl implements NoteRepositoryCustom {

    @Override
    public ResultList<Note> getResultList(BaseFilter filter) {
        ResultList<Note> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select n from Note n ");
        sql.append(" where n.deleted = false ");

        if (filter.getEntityId() != null) {
            sql.append(" and n.entityId =:entityId ");
        }
        if (filter.getEntityType() != null) {
            sql.append(" and n.entityType =:entityType ");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(n.description) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select n", " select count(n.id)");
        sql.append(" order by n.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Note> query = entityManager
                .createQuery(sql.toString(), Note.class)
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
