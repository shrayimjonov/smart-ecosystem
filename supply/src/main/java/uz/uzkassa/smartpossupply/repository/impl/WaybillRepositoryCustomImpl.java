package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.waybill.Waybill;
import uz.uzkassa.smartpossupply.enums.CompanyStatus;
import uz.uzkassa.smartpossupply.enums.WaybillStatus;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.filters.WaybillFilter;
import uz.uzkassa.smartpossupply.repository.WaybillRepositoryCustom;
import uz.uzkassa.smartpossupply.utils.DateUtils;

import java.util.Optional;

public class WaybillRepositoryCustomImpl extends BaseFilterableRepositoryImpl<Waybill, WaybillFilter> implements WaybillRepositoryCustom {

    @Override
    protected Class<Waybill> getEntityClass() {
        return Waybill.class;
    }

    @Override
    protected String getEntityAlias() {
        return "wb";
    }

    @Override
    public void applyFilter(WaybillFilter filter) {

    }

    @Override
    public String getBaseQuery(WaybillFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM Waybill wb ");
        if (includeFetch) {
            sql.append(" LEFT JOIN FETCH wb.factura f ");
            sql.append(" LEFT JOIN FETCH f.branch b ");
            sql.append(" LEFT JOIN FETCH wb.stockTransfer st ");
            sql.append(" LEFT JOIN FETCH wb.shipment sh ");
            if (filter.getWarehouseId() != null) {
                sql.append(" JOIN FETCH f.facturaDetails fd")
                    .append(" JOIN FETCH fd.products p");
            }
        } else {
            sql.append(" LEFT JOIN wb.factura f ");
            sql.append(" LEFT JOIN f.branch b ");
            sql.append(" LEFT JOIN wb.stockTransfer st ");
            sql.append(" LEFT JOIN wb.shipment sh ");
            if (filter.getWarehouseId() != null) {
                sql.append(" JOIN f.facturaDetails fd")
                    .append(" JOIN fd.products p");
            }
        }
        sql.append(" WHERE wb.deleted = false ");

        appendCondition(sql, filter.getBranchId() != null, "AND f.branchId = :branchId ");
        appendCondition(sql, filter.getWarehouseId() != null, " AND p.warehouseId = :warehouseId ");
        appendCondition(sql, StringUtils.hasText(filter.getConsignorTin()), "AND wb.consignorTinOrPinfl = :consignorTin ");
        appendCondition(sql, StringUtils.hasText(filter.getConsigneeTin()), "AND wb.consigneeTinOrPinfl = :consigneeTin ");
        appendCondition(sql, StringUtils.hasText(filter.getStatus()), "AND wb.status = :status ");
        appendCondition(sql, StringUtils.hasText(filter.getType()), "AND wb.waybillType = :waybillType ");
        appendCondition(sql, filter.getFrom() != null, "AND wb.createdDate >= :fromDate ");
        appendCondition(sql, filter.getTo() != null, "AND wb.createdDate <= :toDate ");

        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(wb.waybillNo) ").append(filter.getSearchKey())
                .append(" OR LOWER(wb.consigneeName) ").append(filter.getSearchKey())
                .append(" OR LOWER(wb.consignorTinOrPinfl) ").append(filter.getSearchKey())
                .append(" OR LOWER(wb.consigneeTinOrPinfl) ").append(filter.getSearchKey())
                .append(") ");
        }
        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, WaybillFilter filter) {
        setParameterIfNotNull(query, "branchId", filter.getBranchId());
        setParameterIfNotNull(query, "warehouseId", filter.getWarehouseId());
        setParameterIfNotNull(query, "consignorTin", filter.getConsignorTin());
        setParameterIfNotNull(query, "consigneeTin", filter.getConsigneeTin());

        if (StringUtils.hasText(filter.getStatus())) {
            query.setParameter("status", WaybillStatus.valueOf(filter.getStatus()));
        }
        setParameterIfNotNull(query, "waybillType", filter.getType());
        setParameterIfNotNull(query, "fromDate", filter.getFrom());
        setParameterIfNotNull(query, "toDate", filter.getTo());

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }

    @Override
    public Long getMonthStatistics(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(w.id) FROM Waybill w ");
        sql.append(" WHERE (w.createdDate between :fromDate AND :toDate) ");
        sql.append(" AND w.status !=:status  ");
        sql.append(" AND w.companyId in(select c.id FROM Company c WHERE c.ownerId IS NOT NULL and c.status =:companyStatus) ");

        TypedQuery<Long> query = entityManager.createQuery(sql.toString(), Long.class);
        query.setParameter("fromDate", Optional.ofNullable(filter.getFrom()).orElseGet(DateUtils::getStartOfMonth));
        query.setParameter("toDate", Optional.ofNullable(filter.getTo()).orElseGet(DateUtils::getEndOfMonth));
        query.setParameter("status", WaybillStatus.DRAFT);
        query.setParameter("companyStatus", CompanyStatus.ACTIVE);
        return query.getSingleResult();
    }
}
