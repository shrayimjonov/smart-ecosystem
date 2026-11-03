package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.cts.domain.inventory.ProductVariation;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.ProductVariationFilter;
import uz.uzkassa.cts.repository.custom.ProductVariationRepositoryCustom;

@SuppressWarnings("unused")
public class ProductVariationRepositoryImpl extends BaseRepositoryImpl implements ProductVariationRepositoryCustom {

    @Override
    public ResultList<ProductVariation> getResultList(ProductVariationFilter filter) {
        ResultList<ProductVariation> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select pv from ProductVariation pv ");
        sql.append(" where pv.deleted = false ");
        if (filter.getProductId() != null) {
            sql.append(" and pv.productId =:productId ");
        }
        if (filter.getVariationOptionId() != null) {
            sql.append(" and pv.variationOptionId =:variationOptionId ");
        }

        String countSql = sql.toString().replaceFirst("select pv", " select count(pv.id)");
        sql.append(" order by pv.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<ProductVariation> query = entityManager
                .createQuery(sql.toString(), ProductVariation.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (filter.getProductId() != null) {
            query.setParameter("productId", filter.getProductId());
            countQuery.setParameter("productId", filter.getProductId());
        }
        if (filter.getVariationOptionId() != null) {
            query.setParameter("variationOptionId", filter.getVariationOptionId());
            countQuery.setParameter("variationOptionId", filter.getVariationOptionId());
        }
        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }
}
