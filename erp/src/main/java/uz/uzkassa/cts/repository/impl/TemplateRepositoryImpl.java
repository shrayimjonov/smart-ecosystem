package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.domain.Template;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.ChannelType;
import uz.uzkassa.cts.enums.TemplateType;
import uz.uzkassa.cts.filter.TemplateFilter;
import uz.uzkassa.cts.repository.custom.TemplateRepositoryCustom;

@SuppressWarnings("unused")
public class TemplateRepositoryImpl extends BaseRepositoryImpl implements TemplateRepositoryCustom {

    @Override
    public ResultList<Template> getResultList(TemplateFilter filter) {
        ResultList<Template> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select t from Template t ");
        sql.append(" where t.deleted = false ");
        if (StringUtils.isNotEmpty(filter.getType())) {
            sql.append(" and t.type =:type ");
        }
        if (StringUtils.isNotEmpty(filter.getChannelType())) {
            sql.append(" and t.channelType =:channelType ");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(t.nameUzLatin) ").append(filter.getLikeSearch());
            sql.append(" or lower(t.nameUzCyrillic) ").append(filter.getLikeSearch());
            sql.append(" or lower(t.nameRu) ").append(filter.getLikeSearch());
            sql.append(" or lower(t.code)").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select t", " select count(t.id)");
        sql.append(" order by t.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Template> query = entityManager
                .createQuery(sql.toString(), Template.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (StringUtils.isNotEmpty(filter.getType())) {
            query.setParameter("type", TemplateType.valueOf(filter.getType()));
            countQuery.setParameter("type", TemplateType.valueOf(filter.getType()));
        }
        if (StringUtils.isNotEmpty(filter.getChannelType())) {
            query.setParameter("channelType", ChannelType.valueOf(filter.getChannelType()));
            countQuery.setParameter("channelType", ChannelType.valueOf(filter.getChannelType()));
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
