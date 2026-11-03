package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.cts.domain.inventory.Category;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.category.admin.CategoryTreeAdminDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.custom.CategoryRepositoryCustom;
import uz.uzkassa.cts.security.SecurityUtils;

import java.util.List;

public class CategoryRepositoryImpl extends BaseRepositoryImpl implements CategoryRepositoryCustom {

    @Override
    public ResultList<Category> getResultList(BaseFilter filter) {
        ResultList<Category> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select c from Category c ");
        sql.append(" where c.deleted = false ");
        sql.append(" and c.companyId=:companyId ");

        if (filter.getParentId() != null) {
            sql.append(" and c.parentId =:parentId ");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(c.nameUzLatin) ").append(filter.getLikeSearch());
            sql.append(" or lower(c.nameUzCyrillic) ").append(filter.getLikeSearch());
            sql.append(" or lower(c.nameRu) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select c", " select count(c.id)");
        sql.append(" order by c.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Category> query = entityManager
                .createQuery(sql.toString(), Category.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("companyId", filter.getCompanyId());
        countQuery.setParameter("companyId", filter.getCompanyId());

        if (filter.getParentId() != null) {
            query.setParameter("parentId", filter.getParentId());
            countQuery.setParameter("parentId", filter.getParentId());
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

    @Override
    public List<CategoryTreeAdminDTO> treeList(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("WITH products as (select count(p.id) as count, p.category_id as categoryId from product p ");
        sql.append(" inner join category cat on cat.id = p.category_id ");
        sql.append(" where p.deleted = false ");
        sql.append(" and cat.company_id =").append(filter.getCompanyId());
        sql.append(" group by p.category_id) ");

        sql.append(" select distinct c.id as id,");
        switch (SecurityUtils.getCurrentRequestLanguageEnum()) {
            case uz_latin -> sql.append(" c.name_uz_latin ");
            case uz_cyrillic -> sql.append(" c.name_uz_cyrillic ");
            default -> sql.append(" c.name_ru ");
        }
        sql.append(" as name, ");
        sql.append(" c.parent_id as parentId, coalesce(p.count,0) as productCount ");
        sql.append(" from category c ");
        sql.append(" left join products p ON p.categoryId = c.id ");
        sql.append(" where c.deleted = false ");
        sql.append(" and c.company_id =").append(filter.getCompanyId());
        sql.append(" order by ");
        switch (SecurityUtils.getCurrentRequestLanguageEnum()) {
            case uz_latin -> sql.append(" c.name_uz_latin ");
            case uz_cyrillic -> sql.append(" c.name_uz_cyrillic ");
            default -> sql.append(" c.name_ru ");
        }
        return (List<CategoryTreeAdminDTO>) entityManager.createNativeQuery(sql.toString(), "CategoryTreeDTOMapper").getResultList();
    }
}
