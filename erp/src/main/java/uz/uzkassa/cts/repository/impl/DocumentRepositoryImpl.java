package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.cts.domain.Document;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.DocumentFilter;
import uz.uzkassa.cts.repository.custom.DocumentRepositoryCustom;

import java.util.List;

@SuppressWarnings("unused")
public class DocumentRepositoryImpl extends BaseRepositoryImpl implements DocumentRepositoryCustom {

    @Override
    public ResultList<Document> getResultList(DocumentFilter filter) {
        ResultList<Document> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select d from Document d ");
        sql.append(" where d.deleted = false ");
        if (filter.getFolderId() != null) {
            sql.append(" and d.folderId =:folderId ");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(d.name) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select d", "select count(d.id)");

        sql.append(" order by d.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Document> query = entityManager.createQuery(sql.toString(), Document.class)
            .setFirstResult(filter.getStart())
            .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class).setMaxResults(1);

        if (filter.getFolderId() != null) {
            query.setParameter("folderId", filter.getFolderId());
            countQuery.setParameter("folderId", filter.getFolderId());
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
