package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.domain.cases.CaseXizmat;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.CaseType;
import uz.uzkassa.cts.enums.CaseXizmatType;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.CaseXizmatFilter;
import uz.uzkassa.cts.repository.custom.CaseXizmatRepositoryCustom;

@SuppressWarnings("unused")
public class CaseXizmatRepositoryImpl extends BaseRepositoryImpl implements CaseXizmatRepositoryCustom {

    @Override
    public ResultList<CaseXizmat> getResultList(CaseXizmatFilter filter) {
        ResultList<CaseXizmat> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select cx from CaseXizmat cx ");
        sql.append(" where cx.deleted = false ");

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and cx.status =:status ");
        }
        if (StringUtils.isNotEmpty(filter.getType())) {
            sql.append(" and cx.type =:type ");
        }
        if (StringUtils.isNotEmpty(filter.getCaseType())) {
            sql.append(" and cx.caseType =:caseType ");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(cx.name) ").append(filter.getLikeSearch());
            sql.append(" or lower(cx.xizmatName) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select cx", " select count(cx.id)");
        sql.append(" order by cx.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<CaseXizmat> query = entityManager
                .createQuery(sql.toString(), CaseXizmat.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
        }
        if (StringUtils.isNotEmpty(filter.getType())) {
            query.setParameter("type", CaseXizmatType.valueOf(filter.getType()));
            countQuery.setParameter("type", CaseXizmatType.valueOf(filter.getType()));
        }
        if (StringUtils.isNotEmpty(filter.getCaseType())) {
            query.setParameter("caseType", CaseType.valueOf(filter.getCaseType()));
            countQuery.setParameter("caseType", CaseType.valueOf(filter.getCaseType()));
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
