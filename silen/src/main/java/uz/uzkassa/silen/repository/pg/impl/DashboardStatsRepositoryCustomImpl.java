package uz.uzkassa.silen.repository.pg.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.silen.dto.dashboard.DashboardStatsDTO;
import uz.uzkassa.silen.dto.filter.TopFilter;
import uz.uzkassa.silen.repository.pg.DashboardStatsRepositoryCustom;

import java.util.List;


public class DashboardStatsRepositoryCustomImpl implements DashboardStatsRepositoryCustom {

    @PersistenceContext(unitName = "slavePU")
    private EntityManager entityManager;

    @Override
    public List<DashboardStatsDTO> findAllByFilter(TopFilter filter) {
        final boolean hasSort = StringUtils.isNotEmpty(filter.getOrderBy());
        final String organizationId = filter.getOrganizationId();

        StringBuilder sql = new StringBuilder("select p.id as productId, p.visible_name as productName, o.unit, sum(o.quantity) as quantity from dashboard_stats o");
        sql.append(" inner join product p on p.id=o.product_id");
        sql.append(" where o.deleted is not true");
        sql.append(" and o.organization_id=:organizationId");
        if (filter.getUnit() != null) {
            sql.append(" and o.unit=:unit");
        }
        sql.append(" and o.operation_date>=:from");
        sql.append(" and o.operation_date<=:to");
        sql.append(" and o.type=:type");
        sql.append(" group by productId, unit");

        sql.append(" order by");
        if (hasSort) {
            sql.append(" ").append(filter.getOrderBy());
        } else {
            sql.append(" quantity");
        }
        sql.append(" ").append(filter.getSortOrder().getName());

        Query query = this.entityManager.createNativeQuery(sql.toString(), "DashboardStatsMapper")
                .setMaxResults(filter.getSize());

        query.setParameter("organizationId", organizationId);
        query.setParameter("type", filter.getType().name());
        query.setParameter("from", filter.getFrom());
        query.setParameter("to", filter.getTo());
        if (filter.getUnit() != null) {
            query.setParameter("unit", filter.getUnit().getCode());
        }

        return query.getResultList();
    }
}
