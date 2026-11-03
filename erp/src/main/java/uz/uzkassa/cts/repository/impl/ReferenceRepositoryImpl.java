package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.domain.Reference;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.ReferenceType;
import uz.uzkassa.cts.filter.ReferenceFilter;
import uz.uzkassa.cts.repository.custom.ReferenceRepositoryCustom;
import uz.uzkassa.cts.security.SecurityUtils;

@SuppressWarnings("unused")
public class ReferenceRepositoryImpl extends BaseRepositoryImpl implements ReferenceRepositoryCustom {

    @Override
    public ResultList<Reference> getResultList(ReferenceFilter filter) {
        ResultList<Reference> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select r from Reference r ");
        sql.append(" where r.deleted = false ");

        if (filter.getOnlyParents() != null && filter.getOnlyParents()) {
            sql.append(" and r.parentId is null ");
        } else if (filter.getParentId() != null) {
            sql.append(" and r.parentId =:parentId ");
        }
        if (StringUtils.isNotEmpty(filter.getType())) {
            sql.append(" and r.type =:type ");
        }
        if (StringUtils.isNotEmpty(filter.getCode())) {
            sql.append(" and r.code =:code ");
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and r.status =:status ");
        }
        if (filter.getExternalId() != null) {
            sql.append(" and r.externalId =:externalId ");
        }
        if (StringUtils.isNotEmpty(filter.getExternalCode())) {
            sql.append(" and r.externalCode =:externalCode ");
        }

        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(r.nameUzLatin) ").append(filter.getLikeSearch());
            sql.append(" or lower(r.nameUzCyrillic) ").append(filter.getLikeSearch());
            sql.append(" or lower(r.nameRu) ").append(filter.getLikeSearch());
            sql.append(" or lower(r.code) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select r", " select count(r.id)");
        sql.append(" order by ");
        switch (SecurityUtils.getCurrentRequestLanguageEnum()) {
            case uz_latin -> sql.append(" r.nameUzLatin ");
            case uz_cyrillic -> sql.append(" r.nameUzCyrillic ");
            case ru -> sql.append(" r.nameRu ");
            default -> sql.append(" r.id ");
        }
        sql.append(filter.getSortOrder().getName());

        TypedQuery<Reference> query = entityManager.createQuery(sql.toString(), Reference.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (filter.getParentId() != null) {
            sql.append(" and r.parentId =:parentId ");
        }
        if (StringUtils.isNotEmpty(filter.getType())) {
            query.setParameter("type", ReferenceType.valueOf(filter.getType()));
            countQuery.setParameter("type", ReferenceType.valueOf(filter.getType()));
        }
        if (StringUtils.isNotEmpty(filter.getCode())) {
            query.setParameter("code", filter.getCode());
            countQuery.setParameter("code", filter.getCode());
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
        }
        if (filter.getExternalId() != null) {
            query.setParameter("externalId", filter.getExternalId());
            countQuery.setParameter("externalId", filter.getExternalId());
        }
        if (StringUtils.isNotEmpty(filter.getExternalCode())) {
            query.setParameter("externalCode", filter.getExternalCode());
            countQuery.setParameter("externalCode", filter.getExternalCode());
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
