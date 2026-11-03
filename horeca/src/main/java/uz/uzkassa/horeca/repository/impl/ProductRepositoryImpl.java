package uz.uzkassa.horeca.repository.impl;

import jakarta.persistence.TypedQuery;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.horeca.domain.product.Product;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.enums.CategoryType;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.filters.ProductFilter;
import uz.uzkassa.horeca.repository.ProductRepositoryCustom;


@SuppressWarnings("unused")
public class ProductRepositoryImpl extends BaseRepositoryImpl implements ProductRepositoryCustom {

    @Override
    public ResultList<Product> getResultList(ProductFilter filter) {
        ResultList<Product> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select p from Product p ");
        sql.append(" join p.category c ");
        sql.append(" where p.deleted = false and c.deleted = false ");
        sql.append(" and p.branchId =:branchId");

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and p.status=:status ");
        }
        if (filter.getCategoryId() != null) {
            sql.append(" and (p.categoryId=:categoryId ");
            sql.append(" or p.category.parentId in (select id from Category c where c.parentId=:categoryId)");
            sql.append(" or p.category.parentId in (select id from Category c where c.id=:categoryId))");
        }
        if (StringUtils.isNotEmpty(filter.getCategoryType())) {
            sql.append(" and c.categoryType=:categoryType ");
        } else if (!CollectionUtils.isEmpty(filter.getCategoryTypes())) {
            sql.append(" and c.categoryType in(:categoryTypes) ");
        }
        if (filter.getDepartmentId() != null) {
            sql.append(" and p.departmentId=:departmentId ");
        }
        if (filter.getMenuIncluded() != null) {
            sql.append(" and p.menuIncluded=:menuIncluded ");
        }
        if (filter.hasSearch()) {
            sql.append(" and (");
            sql.append(" lower(p.name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(p.catalogCode)").append(filter.getLikeSearchKey());
            sql.append(" or lower(p.catalogName)").append(filter.getLikeSearchKey());
            sql.append(" or lower(p.barcode)").append(filter.getLikeSearchKey());
            sql.append(" or lower(p.brand)").append(filter.getLikeSearchKey());
            sql.append(" or lower(p.description)").append(filter.getLikeSearchKey());
            sql.append(" or lower(p.sku)").append(filter.getLikeSearchKey());
            sql.append(")");
        }

        String countSql = sql.toString().replaceFirst("select p", "select count(p.id)");
        sql.append(" order by p.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Product> query = entityManager
                .createQuery(sql.toString(), Product.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("branchId", filter.getBranchId());
        countQuery.setParameter("branchId", filter.getBranchId());

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
        }
        if (filter.getCategoryId() != null) {
            query.setParameter("categoryId", filter.getCategoryId());
            countQuery.setParameter("categoryId", filter.getCategoryId());
        }
        if (filter.getMenuIncluded() != null) {
            query.setParameter("menuIncluded", filter.getMenuIncluded());
            countQuery.setParameter("menuIncluded", filter.getMenuIncluded());
        }
        if (StringUtils.isNotEmpty(filter.getCategoryType())) {
            query.setParameter("categoryType", CategoryType.valueOf(filter.getCategoryType()));
            countQuery.setParameter("categoryType", CategoryType.valueOf(filter.getCategoryType()));
        } else if (!CollectionUtils.isEmpty(filter.getCategoryTypes())) {
            List<CategoryType> categoryTypes = filter.getCategoryTypes().stream().map(CategoryType::valueOf).toList();
            query.setParameter("categoryTypes", categoryTypes);
            countQuery.setParameter("categoryTypes", categoryTypes);
        }
        if (filter.getDepartmentId() != null) {
            query.setParameter("departmentId", filter.getDepartmentId());
            countQuery.setParameter("departmentId", filter.getDepartmentId());
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
