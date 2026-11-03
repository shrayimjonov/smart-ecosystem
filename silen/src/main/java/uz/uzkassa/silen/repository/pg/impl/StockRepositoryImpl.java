package uz.uzkassa.silen.repository.pg.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import uz.uzkassa.silen.dto.filter.WarehouseFilter;
import uz.uzkassa.silen.dto.warehouse.StockStatsDTO;
import uz.uzkassa.silen.repository.pg.StockRepositoryCustom;

public class StockRepositoryImpl implements StockRepositoryCustom {

    @PersistenceContext(unitName = "slavePU")
    private EntityManager entityManager;

    @Override
    public Page<StockStatsDTO> findAllByFilter(WarehouseFilter filter) {
        final boolean hasSort = StringUtils.isNotEmpty(filter.getOrderBy());

        StringBuilder sql = new StringBuilder("select s.product_id as productId,");
        sql.append(" p.visible_name as name, s.amount, p.price * p.vat_rate * 0.01 + p.price  as priceWithVat,");
        sql.append(" (p.price * p.vat_rate * 0.01 + p.price ) * s.amount as sumWithVat,");
        sql.append(" p.price * p.vat_rate * 0.01 * s.amount as sumVat,");
        sql.append(" p.price * s.amount as sumPriceWithoutVat");
        sql.append(" from stock s");
        sql.append(" inner join product p on s.product_id=p.id and p.deleted is not true");
        sql.append(" where s.deleted is not true");
        sql.append(" and s.organization_id=:organizationId");

        StringBuilder countSql = new StringBuilder("select count(s.id) from stock s");
        countSql.append(" where s.deleted is not true");
        countSql.append(" and s.organization_id=:organizationId");

        if (filter.getProductId() != null) {
            sql.append(" and s.product_id=:productId");
            countSql.append(" and s.product_id=:productId");
        }

        sql.append(" order by");
        if (hasSort) {
            sql.append(" s.").append(filter.getOrderBy())
                .append(" ").append(filter.getSortOrder().getName());
        } else {
            sql.append(" p.name asc");
        }

        Query query = this.entityManager.createNativeQuery(sql.toString(), "StockMapper")
            .setFirstResult(filter.getStart())
            .setMaxResults(filter.getSize())
            .setParameter("organizationId", filter.getOrganizationId());
        Query countQuery = entityManager.createNativeQuery(countSql.toString()).setParameter("organizationId", filter.getOrganizationId());

        if (filter.getProductId() != null) {
            query.setParameter("productId", filter.getProductId());
            countQuery.setParameter("productId", filter.getProductId());
        }
        return new PageImpl<StockStatsDTO>(query.getResultList(), filter.getPageable(), (Long) countQuery.getSingleResult());
    }

    @Override
    public StockStatsDTO stats(WarehouseFilter filter) {
        StringBuilder sql = new StringBuilder("select ");
        sql.append(" sum(s.amount) as amount,");
        sql.append(" sum((p.price * p.vat_rate * 0.01 + p.price ) * s.amount) as sumWithVat,");
        sql.append(" sum(p.price * p.vat_rate * 0.01 * s.amount) as sumVat,");
        sql.append(" sum(p.price * s.amount) as sumPriceWithoutVat");
        sql.append(" from stock s");
        sql.append(" inner join product p on s.product_id=p.id");
        sql.append(" where s.deleted is not true");
        sql.append(" and s.organization_id=:organizationId");

        if (filter.getProductId() != null) {
            sql.append(" and s.product_id=:productId");
        }

        Query query = this.entityManager.createNativeQuery(sql.toString(), "StockStatsMapper")
            .setParameter("organizationId", filter.getOrganizationId());

        if (filter.getProductId() != null) {
            query.setParameter("productId", filter.getProductId());
        }

        return (StockStatsDTO) query.getSingleResult();
    }
}
