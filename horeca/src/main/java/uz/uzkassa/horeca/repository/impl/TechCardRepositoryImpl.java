package uz.uzkassa.horeca.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.horeca.domain.techcard.TechCard;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.repository.TechCardRepositoryCustom;


@SuppressWarnings("unused")
public class TechCardRepositoryImpl extends BaseRepositoryImpl implements TechCardRepositoryCustom {

    @Override
    public ResultList<TechCard> getResultList(BaseFilter filter) {
        ResultList<TechCard> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select tc from TechCard tc ");
        sql.append(" join tc.product p ");
        sql.append(" where tc.deleted = false and p.deleted = false ");
        sql.append(" and p.branchId =:branchId");
        if (filter.getProductId() != null) {
            sql.append(" and p.id=:productId ");
        }
        if (filter.hasSearch()) {
            sql.append(" and (");
            sql.append(" lower(p.name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(p.catalogCode)").append(filter.getLikeSearchKey());
            sql.append(" or lower(p.catalogName)").append(filter.getLikeSearchKey());
            sql.append(" or lower(p.barcode)").append(filter.getLikeSearchKey());
            sql.append(" or lower(p.brand)").append(filter.getLikeSearchKey());
            sql.append(" or lower(p.sku)").append(filter.getLikeSearchKey());
            sql.append(" or lower(tc.description)").append(filter.getLikeSearchKey());
            sql.append(")");
        }

        String countSql = sql.toString().replaceFirst("select tc", "select count(tc.id)");
        sql.append(" order by tc.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<TechCard> query = entityManager
            .createQuery(sql.toString(), TechCard.class)
            .setFirstResult(filter.getStart())
            .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("branchId", filter.getBranchId());
        countQuery.setParameter("branchId", filter.getBranchId());

        if (filter.getProductId() != null) {
            query.setParameter("productId", filter.getProductId());
            countQuery.setParameter("productId", filter.getProductId());
        }
        if (filter.hasSearch()) {
            query.setParameter("searchKey", filter.getSearchForQuery());
            countQuery.setParameter("searchKey", filter.getSearchForQuery());
        }

        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }
}
