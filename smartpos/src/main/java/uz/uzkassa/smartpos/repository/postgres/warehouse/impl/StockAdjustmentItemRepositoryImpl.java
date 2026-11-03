package uz.uzkassa.smartpos.repository.postgres.warehouse.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import uz.uzkassa.smartpos.domain.postgres.warehouse.StockAdjustmentItem;
import uz.uzkassa.smartpos.dto.filter.StockAdjustmentFilter;
import uz.uzkassa.smartpos.repository.postgres.warehouse.StockAdjustmentItemRepositoryCustom;

@SuppressWarnings("unused")
public class StockAdjustmentItemRepositoryImpl implements StockAdjustmentItemRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<StockAdjustmentItem> findAllByFilter(StockAdjustmentFilter filter) {
        final boolean hasSearch = StringUtils.isNotEmpty(filter.getSearch());
        final boolean hasSort = StringUtils.isNotEmpty(filter.getOrderBy());

        StringBuilder sql = new StringBuilder("select sai from StockAdjustmentItem sai inner join sai.stockAdjustment sa");
        sql.append(" where");
        if (CollectionUtils.isNotEmpty(filter.getBranchIdsWithBranchId())) {
            sql.append(" sa.branchId in (:branchIds)");
        } else {
            sql.append(" sa.branch.companyId=:companyId");
        }
        if (filter.getProductId() != null) {
            sql.append(" and sai.productId=:productId");
        }
        if (filter.getReason() != null) {
            sql.append(" and sa.reason=:reason");
        }
        if (filter.getFrom() != null) {
            sql.append(" and sa.createdDate>=:fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and sa.createdDate<=:toDate");
        }

        if (hasSearch) {
            sql.append(" or lower(sai.product.name) like :searchKey");
            sql.append(")");
        }
        String countSql = sql.toString().replaceFirst("select sai", "select count(sai)");
        sql.append(" order by");
        if (hasSort) {
            switch (filter.getOrderBy()) {
                case "product":
                    sql.append(" sai.product.name");
                    break;
                case "qty":
                    sql.append(" sai.qty");
                    break;
                case "unit":
                    sql.append(" sai.unit.name");
                    break;
                case "fullName":
                    sql.append(" sa.user.lastName");
                    break;
                case "createdDate":
                    sql.append(" sa.createdDate");
                    break;
                case "reason":
                    sql.append(" sa.reason");
                    break;
                default:
                    sql.append(" sa.id");
                    break;

            }
        } else {
            sql.append(" sa.createdDate");
        }
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<StockAdjustmentItem> query = entityManager.createQuery(sql.toString(), StockAdjustmentItem.class)
            .setFirstResult(filter.getStart()).setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (CollectionUtils.isNotEmpty(filter.getBranchIdsWithBranchId())) {
            query.setParameter("branchIds", filter.getBranchIdsWithBranchId());
            countQuery.setParameter("branchIds", filter.getBranchIdsWithBranchId());
        } else {
            query.setParameter("companyId", filter.getCompanyId());
            countQuery.setParameter("companyId", filter.getCompanyId());
        }
        if (filter.getProductId() != null) {
            query.setParameter("productId", filter.getProductId());
            countQuery.setParameter("productId", filter.getProductId());
        }
        if (filter.getReason() != null) {
            query.setParameter("reason", filter.getReason());
            countQuery.setParameter("reason", filter.getReason());
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
            countQuery.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
            countQuery.setParameter("toDate", filter.getTo());
        }
        if (hasSearch) {
            query.setParameter("searchKey", filter.getSearchForQuery());
            countQuery.setParameter("searchKey", filter.getSearchForQuery());
        }
        return new PageImpl<>(query.getResultList(), filter.getPageable(), countQuery.getSingleResult());
    }
}
