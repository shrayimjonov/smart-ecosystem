package uz.uzkassa.smartpos.repository.postgres.catalog.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductItem;
import uz.uzkassa.smartpos.dto.filter.ProductFilter;
import uz.uzkassa.smartpos.repository.postgres.catalog.ProductItemRepositoryCustom;

@SuppressWarnings("unused")
public class ProductItemRepositoryImpl implements ProductItemRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<ProductItem> findAllByFilter(ProductFilter filter) {
        final boolean hasSearch = StringUtils.isNotEmpty(filter.getSearch());

        StringBuilder sql = new StringBuilder("select pb from ProductItem pb \n");
        queryByFilter(filter, hasSearch, sql);

        String countSql = sql.toString().replace("select pb", "select count(pb)");

        sql.append("order by pb.name");

        TypedQuery<ProductItem> query = entityManager.createQuery(sql.toString(), ProductItem.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize())
                .setParameter("cityId", filter.getCityId())
                .setParameter("branchId", filter.getBranchId());
        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class)
                .setMaxResults(1)
                .setParameter("cityId", filter.getCityId())
                .setParameter("branchId", filter.getBranchId());

        if (filter.getProductId() != null) {
            query.setParameter("productId", filter.getProductId());
            countQuery.setParameter("productId", filter.getProductId());
        }
        if (filter.getFromDateTime() != null) {
            query.setParameter("from", filter.getFromDateTime());
            countQuery.setParameter("from", filter.getFromDateTime());
        }
        if (filter.getTo() != null) {
            query.setParameter("to", filter.getTo());
        }
        if (hasSearch) {
            query.setParameter("searchKey", filter.getSearchForQuery());
            countQuery.setParameter("searchKey", filter.getSearchForQuery());
        }

        return new PageImpl<>(query.getResultList(), filter.getPageable(), countQuery.getSingleResult());
    }

    private void queryByFilter(ProductFilter filter, boolean hasSearch, StringBuilder sql) {
        sql.append("where pb.cityId=:cityId and pb.branchId=:branchId \n");
        if (filter.getProductId() != null) {
            sql.append("and pb.productId=:productId \n");
        }
        if (filter.getFrom() != null) {
            sql.append("and pb.lastModifiedDate>=:from \n");
        } else {
            sql.append(" and pb.deleted is false \n");
        }
        if (filter.getTo() != null) {
            sql.append(" and pb.lastModifiedDate<=:to \n");
        }

        if (hasSearch) {
            sql.append("and ( \n")
                    .append("lower(pb.name) like :searchKey \n")
                    .append("OR lower(pb.barcode) like :searchKey \n")
                    .append(") \n");
        }
    }
}
