package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.cts.criteriahelper.BaseQuery;
import uz.uzkassa.cts.domain.inventory.Product;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.ProductFilter;
import uz.uzkassa.cts.repository.custom.ProductRepositoryCustom;

@SuppressWarnings("unused")
public class ProductRepositoryImpl extends BaseRepositoryImpl implements ProductRepositoryCustom {

    @Override
    public ResultList<Product> getResultList(ProductFilter filter) {
        ResultList<Product> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select p from Product p ");
        sql.append(" join p.category cat ");
        sql.append(" where p.deleted = false ");
        sql.append(" and cat.companyId =:companyId ");

        if (filter.getBranchId() != null) {
            sql.append(" and p.brandId =:brandId ");
        }
        if (filter.getCategoryId() != null) {
            sql.append(" and p.categoryId =:categoryId ");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(p.nameUzLatin) ").append(filter.getLikeSearch());
            sql.append(" or lower(p.nameUzCyrillic) ").append(filter.getLikeSearch());
            sql.append(" or lower(p.nameRu) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select p", " select count(p.id)");
        sql.append(" order by p.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Product> query = entityManager
                .createQuery(sql.toString(), Product.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("companyId", filter.getCompanyId());
        countQuery.setParameter("companyId", filter.getCompanyId());

        if (filter.getBrandId() != null) {
            query.setParameter("brandId", filter.getBrandId());
            countQuery.setParameter("brandId", filter.getBrandId());
        }
        if (filter.getCategoryId() != null) {
            query.setParameter("categoryId", filter.getCategoryId());
            countQuery.setParameter("categoryId", filter.getCategoryId());
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
