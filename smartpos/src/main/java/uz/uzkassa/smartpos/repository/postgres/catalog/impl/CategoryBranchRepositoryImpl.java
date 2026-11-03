package uz.uzkassa.smartpos.repository.postgres.catalog.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.simple.JdbcClient;
import uz.uzkassa.smartpos.dto.CategoryDTO;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.dto.catalog.CategoryResult;
import uz.uzkassa.smartpos.dto.catalog.CategoryTreeDto;
import uz.uzkassa.smartpos.dto.filter.BaseFilter;
import uz.uzkassa.smartpos.repository.postgres.catalog.CategoryBranchRepositoryCustom;

import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
@RequiredArgsConstructor
public class CategoryBranchRepositoryImpl implements CategoryBranchRepositoryCustom {
    @PersistenceContext
    private final EntityManager entityManager;
    private final JdbcClient jdbcClient;

    @Override
    public List<CategoryResult> findAll(Long branchId, Long cityId) {
        return jdbcClient.sql("""
                        select distinct cb.category_id as                              id,
                                        cb.name        as                              name,
                                        cb.parent_id   as                              parentId,
                                        cb.mxik_code   as                              mxikCode,
                                        count(p.product_id)    as                              productCount,
                                        count(p.product_id) filter ( where p.advance_enabled ) advanceCount,
                                        count(p.product_id) filter ( where p.credit_enabled )  creditCount
                        from category_branch cb
                                 left join product_branch p
                                           ON p.city_id = :cityId and p.branch_id = cb.branch_id and p.category_id = cb.category_id and
                                              p.deleted is not true
                        where cb.enabled is true
                          and cb.branch_id = :branchId
                        group by cb.category_id, cb.branch_id""")
                .param("branchId", branchId)
                .param("cityId", cityId)
                .query(CategoryResult.class)
                .list();
    }


    @Override
    public List<CategoryTreeDto> findAllWithoutProductsCount(Long branchId) {
        return jdbcClient.sql("""
                        select distinct cb.category_id as id,
                                        cb.name        as name,
                                        cb.parent_id   as parentId
                        from category_branch cb
                        where cb.enabled is true
                          and cb.branch_id = :branchId
                        group by cb.category_id, cb.branch_id""")
                .param("branchId", branchId)
                .query(CategoryTreeDto.class)
                .list();
    }

    @Override
    public List<CategoryDTO> findEnabledCategories(Long branchId, Long cityId) {
        return jdbcClient.sql("""
                        select distinct cb.category_id as                              id,
                                        cb.name        as                              name,
                                        cb.parent_id   as                              parentId,
                                        true           as                              enabled,
                                        count(p.product_id)    as                              productCount,
                                        count(p.product_id) filter ( where p.advance_enabled ) advanceCount,
                                        count(p.product_id) filter ( where p.credit_enabled )  creditCount
                        from category_branch cb
                                 left join product_branch p
                                           ON p.city_id = :cityId and p.branch_id = cb.branch_id and p.category_id = cb.category_id and
                                              p.deleted is not true
                        where cb.enabled is true
                          and cb.branch_id = :branchId
                        group by cb.category_id, cb.branch_id""")
                .param("cityId", cityId)
                .param("branchId", branchId)
                .query(CategoryDTO.class)
                .list();
    }

    @Override
    public List<CommonDTO> choose(BaseFilter filter) {
        StringBuilder sql = new StringBuilder("""
                select distinct cb.category_id, cb.name
                from category_branch cb
                         inner join branch b ON b.id = cb.branch_id
                where cb.enabled is true
                  and b.company_id = :companyId""");
        var params = new HashMap<String, Object>();
        params.put("companyId", filter.getCompanyId());
        if (filter.getBranchId() != null) {
            sql.append(" and cb.branch_id = :branchId");
            params.put("branchId", filter.getBranchId());
        }
        if (StringUtils.isNotBlank(filter.getSearch())) {
            sql.append(" and (lower(cb.name) like :searchKey)");
            params.put("searchKey", filter.getSearchForQuery());
        }
        sql.append(" order by cb.name");
        return jdbcClient.sql(sql.toString())
                .params(params)
                .query(CommonDTO.class)
                .list();
    }

}
