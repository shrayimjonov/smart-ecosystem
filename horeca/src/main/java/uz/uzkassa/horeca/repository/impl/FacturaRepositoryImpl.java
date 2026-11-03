package uz.uzkassa.horeca.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.horeca.domain.factura.Factura;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.dto.factura.Status;
import uz.uzkassa.horeca.filters.FacturaFilter;
import uz.uzkassa.horeca.repository.FacturaRepositoryCustom;

@Repository
public class FacturaRepositoryImpl extends BaseRepositoryImpl implements FacturaRepositoryCustom {

    @Override
    public ResultList<Factura> getResultList(FacturaFilter filter) {
        ResultList<Factura> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select f ");
        sql.append(getBaseSql(filter));

        String countSql = sql.toString().replaceFirst("select f", "select count(f.id)");

        sql.append(" order by f.facturaDate desc");

        TypedQuery<Factura> query = entityManager
            .createQuery(sql.toString(), Factura.class)
            .setFirstResult(filter.getStart())
            .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (filter.getBuyerId() != null) {
            query.setParameter("buyerId", filter.getBuyerId());
            countQuery.setParameter("buyerId", filter.getBuyerId());
        }
        if (filter.getSellerId() != null) {
            query.setParameter("sellerId", filter.getSellerId());
            countQuery.setParameter("sellerId", filter.getSellerId());
        }
        if (filter.getBuyerId() == null && filter.getSellerId() == null) {
            query.setParameter("outgoing", !filter.isIncoming());
            countQuery.setParameter("outgoing", !filter.isIncoming());
        }
        if (filter.getFacturaType() != null) {
            query.setParameter("facturaType", filter.getFacturaType());
            countQuery.setParameter("facturaType", filter.getFacturaType());
        }
        if (!CollectionUtils.isEmpty(filter.getFacturaTypes())) {
            query.setParameter("facturaTypes", filter.getFacturaTypes());
            countQuery.setParameter("facturaTypes", filter.getFacturaTypes());
        }
        if (StringUtils.isNotBlank(filter.getStatus())) {
            query.setParameter("status", Status.valueOf(filter.getStatus()));
            countQuery.setParameter("status", Status.valueOf(filter.getStatus()));
        }
        if (StringUtils.isNotBlank(filter.getExcludeStatus())) {
            query.setParameter("excludeStatus", Status.valueOf(filter.getExcludeStatus()));
            countQuery.setParameter("excludeStatus", Status.valueOf(filter.getExcludeStatus()));
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom().toLocalDate());
            countQuery.setParameter("fromDate", filter.getFrom().toLocalDate());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo().toLocalDate());
            countQuery.setParameter("toDate", filter.getTo().toLocalDate());
        }
        if (filter.hasSearch()) {
            query.setParameter("searchKey", filter.getLikeSearchKey());
            countQuery.setParameter("searchKey", filter.getLikeSearchKey());
        }

        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }

        return resultList;
    }

    private StringBuilder getBaseSql(FacturaFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from Factura f ");
        sql.append(" join f.buyer b ");
        sql.append(" join f.seller s ");
        sql.append(" where f.deleted =false");
        if (filter.getBuyerId() != null) {
            sql.append(" and b.id=:buyerId ");
        }
        if (filter.getSellerId() != null) {
            sql.append(" and s.id=:sellerId ");
        }
        if (filter.getSellerId() == null && filter.getBuyerId() == null) {
            sql.append(" and f.outgoing=:outgoing");
        }
        if (filter.getFacturaType() != null) {
            sql.append(" and f.facturaType=:facturaType ");
        }
        if (!CollectionUtils.isEmpty(filter.getFacturaTypes())) {
            sql.append(" and f.facturaType in(:facturaTypes) ");
        }
        if (StringUtils.isNotBlank(filter.getStatus())) {
            sql.append(" and f.status=:status ");
        }
        if (StringUtils.isNotBlank(filter.getExcludeStatus())) {
            sql.append(" and f.status!=:excludeStatus ");
        }
        if (filter.getFrom() != null) {
            sql.append(" and f.facturaDate>=:fromDate ");
        }
        if (filter.getTo() != null) {
            sql.append(" and f.facturaDate<=:toDate ");
        }
        if (filter.hasSearch()) {
            sql.append(" and (");
            sql.append(" lower(f.facturaNo)").append(filter.getLikeSearchKey());
            sql.append(" or lower(b.tin)").append(filter.getLikeSearchKey());
            sql.append(" or lower(b.name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(s.tin)").append(filter.getLikeSearchKey());
            sql.append(" or lower(s.name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(f.facturaId)").append(filter.getLikeSearchKey());
            sql.append(")");
        }
        return sql;
    }
}
