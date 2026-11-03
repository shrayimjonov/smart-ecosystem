package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.category.Category;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.category.CategoryTreeDTO;
import uz.uzkassa.smartpossupply.dto.category.MobileCategoryTreeDTO;
import uz.uzkassa.smartpossupply.dto.telegram.TelegramCategoryListDTO;
import uz.uzkassa.smartpossupply.dto.telegram.TelegramCategoryListMapperDTO;
import uz.uzkassa.smartpossupply.dto.telegram.TelegramProductListDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.EnvironmentType;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.filters.TelegramCategoryFilter;
import uz.uzkassa.smartpossupply.repository.CategoryRepositoryCustom;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CategoryRepositoryImpl
    extends BaseFilterableRepositoryImpl<Category, BaseFilter>
    implements CategoryRepositoryCustom {

    @Override
    protected Class<Category> getEntityClass() {
        return Category.class;
    }

    @Override
    protected String getEntityAlias() {
        return "c";
    }

    @Override
    public void applyFilter(BaseFilter filter) {
        // No custom filter logic needed for this repository
        // branchId and type are expected to be already set in the filter
    }

    @Override
    public String getBaseQuery(BaseFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM Category c ");

        // Add joins
        if (includeFetch) {
            sql.append("JOIN FETCH c.branch b ")
                .append("LEFT JOIN FETCH c.parent p ");
        } else {
            sql.append("JOIN c.branch b ")
                .append("LEFT JOIN c.parent p ");
        }

        // Base conditions
        sql.append("WHERE c.deleted = false ")
            .append("AND c.environmentType = :environmentType ")
            .append("AND c.name IS NOT NULL ")
            .append("AND b.id = :branchId ");

        // Dynamic conditions
        appendCondition(sql, filter.getParentId() != null, "AND p.id = :parentId ");

        // Search conditions
        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(c.name)").append(filter.getSearchKey())
                .append(") ");
        }

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, BaseFilter filter) {
        // Required parameters
        query.setParameter("branchId", filter.getBranchId());
        query.setParameter("environmentType", EnvironmentType.valueOf(filter.getType()));

        // Optional parameters
        setParameterIfNotNull(query, "parentId", filter.getParentId());

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }

    @Override
    public List<CategoryTreeDTO> treeList(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT c.id, c.name, c.parent_id AS parentId, ")
            .append(" COALESCE(COUNT(p.id) FILTER (WHERE p.deleted = FALSE AND p.branch_id =").append(filter.getBranchId()).append("), 0) AS productCount, ")
            .append(" c.status ")
            .append(" FROM category c ")
            .append(" LEFT JOIN product_category pc ON pc.category_id = c.id ")
            .append(" LEFT JOIN product p ON p.id = pc.product_id AND p.deleted = FALSE AND p.branch_id =").append(filter.getBranchId())
            .append(" WHERE c.deleted = FALSE AND c.environment_type = '").append(filter.getType()).append("' ")
            .append(" AND c.branch_id = ").append(filter.getBranchId());
        if (StringUtils.hasText(filter.getStatus())) {
            sql.append(" AND c.status = '").append(filter.getStatus()).append("' ");
        }
        sql.append(" GROUP BY c.id, c.name, c.parent_id, c.status ");
        sql.append(" ORDER BY c.name");
        return entityManager.createNativeQuery(sql.toString(), "CategoryTreeDTOMapper").getResultList();
    }

    @Override
    public List<CategoryTreeDTO> treeListSorted(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT c.id AS id, c.name AS name, c.parent_id AS parentId, ")
            .append(" COALESCE(COUNT(p.id) FILTER (WHERE p.deleted = FALSE), 0) AS productCount, ")
            .append(" c.status AS status, ")
            .append(" cs.sort_order AS sortOrder ")
            .append(" FROM category c ")
            .append(" LEFT JOIN product_category pc ON pc.category_id = c.id ")
            .append(" LEFT JOIN product p ON p.id = pc.product_id AND p.deleted = FALSE AND p.branch_id = c.branch_id ")
            .append(" LEFT JOIN category_sort cs ON cs.category_id = c.id ")
            .append(" WHERE c.deleted = FALSE AND c.environment_type = '").append(filter.getType()).append("' ")
            .append(" AND c.branch_id = ").append(filter.getBranchId());
        if (StringUtils.hasText(filter.getStatus())) {
            sql.append(" AND c.status = '").append(filter.getStatus()).append("' ");
        }
        sql.append(" GROUP BY c.id, c.name, c.parent_id, c.status, cs.sort_order ");
        sql.append(" ORDER BY cs.sort_order");

        return entityManager.createNativeQuery(sql.toString(), "CategoryTreeDTOMapper").getResultList();
    }

    @Override
    public List<MobileCategoryTreeDTO> treeListForMobile(BaseFilter filter) {
        boolean enabledSorting = filter.isEnabledSorting();
        filter.setBranchId(getBranchId(filter));
        StringBuilder sql = new StringBuilder("WITH products as (select count(p.id) as count, pc.category_id as categoryId from product p join product_category pc on p.id = pc.product_id join category c on c.id = pc.category_id and c.environment_type='");
        sql.append(EnvironmentType.WEB.getCode()).append("'\n");
        sql.append(" where p.deleted = false ");
        sql.append(" and p.branch_id = ").append(filter.getBranchId());
        sql.append(" group by pc.category_id) ");

        sql.append(" select distinct c.id as id, c.name as name, coalesce(p.count,0) as productCount, c.parent_id as parentId, c.branch_id as branchId ");
        if (enabledSorting) {
            sql.append(", cs.sort_order as sortOrder ");
        }
        sql.append(" from category c \n");
        sql.append(" left join products p ON p.categoryId = c.id \n");
        if (enabledSorting) {
            sql.append(" left join category_sort cs ON cs.category_id = c.id \n");
        }
        sql.append(" where c.deleted = false \n");
        sql.append(" and c.status='").append(CommonStatus.ACTIVE.getCode()).append("'");
        sql.append(" and c.environment_type = '").append(EnvironmentType.WEB.getCode()).append("'");
        sql.append(" and c.branch_id = ").append(filter.getBranchId());
        if (filter.getParentId() != null) {
            sql.append(" and c.parent_id =").append(filter.getParentId());
        } else {
            sql.append(" and c.parent_id is null ");
        }
        if (filter.getStatus() != null) {
            sql.append(" and c.status='").append(CommonStatus.valueOf(filter.getStatus())).append("'");
        }
        if (enabledSorting) {
            sql.append(" order by cs.sort_order ");
        } else {
            sql.append(" order by c.name ");
        }
        return entityManager.createNativeQuery(sql.toString(), "CategoryTreeMobileDTOMapper").getResultList();
    }

    @Override
    public List<TelegramCategoryListDTO> getListForTelegram(TelegramCategoryFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
            .append(" c.id as categoryId,")
            .append(" c.name as categoryName,")
            .append(" p.id as productId,")
            .append(" p.name as productName,")
            .append(" p.vat as vat,")
            .append(" p.price as price,")
            .append(" p.foreign_price as foreignPrice,")
            .append(" p.currency_type as currencyType, ")
            .append(" fu.url as photoUrl ")
            .append(" FROM category c")
            .append(" INNER JOIN product p ON c.id = p.category_id")
            .append(" LEFT JOIN product_photo pp ON pp.product_id = p.id and pp.is_main = true ")
            .append(" LEFT JOIN file_upload fu ON fu.id = pp.photo_id ")
            .append(" WHERE p.price is not null and p.branch_id =").append(filter.getBranchId())
            .append(" AND c.deleted = false and c.status = '").append(CommonStatus.ACTIVE.getCode()).append("'")
            .append(" AND p.deleted = false ")
            .append(" AND c.environment_type ='").append(EnvironmentType.WEB.getCode()).append("'")
            .append(" ORDER BY c.id, p.name");

        List<TelegramCategoryListMapperDTO> telegramCategoryList = entityManager.createNativeQuery(sql.toString(), "TelegramCategoryListMapper").getResultList();
        List<TelegramCategoryListDTO> result = new ArrayList<>();
        Set<Long> categorySet = new HashSet<>();
        for (TelegramCategoryListMapperDTO telegramCategoryListMapperDTO : telegramCategoryList) {
            TelegramCategoryListDTO telegramCategoryListDTO;
            if (categorySet.isEmpty() || !categorySet.contains(telegramCategoryListMapperDTO.getCategoryId())) {
                telegramCategoryListDTO = new TelegramCategoryListDTO();
                telegramCategoryListDTO.setCategory(new CommonDTO(telegramCategoryListMapperDTO.getCategoryId(), telegramCategoryListMapperDTO.getCategoryName()));
                result.add(telegramCategoryListDTO);
            } else {
                telegramCategoryListDTO = result.getLast();
            }
            TelegramProductListDTO telegramProductListDTO = new TelegramProductListDTO();
            telegramProductListDTO.setId(telegramCategoryListMapperDTO.getProductId());
            telegramProductListDTO.setName(telegramCategoryListMapperDTO.getProductName());
            telegramProductListDTO.setVat(telegramCategoryListMapperDTO.getVat());
            telegramProductListDTO.setPrice(telegramCategoryListMapperDTO.getPrice());
            telegramProductListDTO.setForeignPrice(telegramCategoryListMapperDTO.getForeignPrice());
            telegramProductListDTO.setCurrencyType(telegramCategoryListMapperDTO.getCurrencyType());
            telegramProductListDTO.setPhotoUrl(telegramCategoryListMapperDTO.getPhotoUrl());
            telegramCategoryListDTO.addProduct(telegramProductListDTO);

            categorySet.add(telegramCategoryListMapperDTO.getCategoryId());
        }
        return result;
    }
}
