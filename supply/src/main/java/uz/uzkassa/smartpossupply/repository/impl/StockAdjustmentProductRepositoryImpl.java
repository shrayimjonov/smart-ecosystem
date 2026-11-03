package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import uz.uzkassa.smartpossupply.dto.stock.StockProductQtyDetailListDTO;
import uz.uzkassa.smartpossupply.enums.StockStatus;
import uz.uzkassa.smartpossupply.filters.StockFilter;
import uz.uzkassa.smartpossupply.repository.StockAdjustmentProductRepositoryCustom;

import java.util.ArrayList;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 12.12.2023
 */
public class StockAdjustmentProductRepositoryImpl extends BaseRepositoryImpl implements StockAdjustmentProductRepositoryCustom {

    @Override
    public Page<StockProductQtyDetailListDTO> getQtyDetails(StockFilter filter) {
        filter.setWarehouseId(getWarehouseId(filter));

        StringBuilder sql = new StringBuilder();
        sql.append("select s.id as id, s.adjustment_number as stockNumber, s.adjustment_date as stockDate, s.completed_date as stockCompletedDate, sp.qty as qty, c.id as companyId, c.name as companyName, c.tin as companyTin");
        sql.append(getBaseSql(filter));
        StringBuilder countSql = new StringBuilder();
        countSql.append("select count(s.id) ");
        countSql.append(getBaseSql(filter));
        sql.append(" order by s.id desc");
        Query query = entityManager
                .createNativeQuery(sql.toString(), "StockProductQtyDetailListMapper")
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());
        Query countQuery = entityManager.createNativeQuery(countSql.toString());
        if (filter.getFrom() != null) {
            countQuery.setParameter("fromDate", filter.getFrom());
            query.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            countQuery.setParameter("toDate", filter.getTo());
            query.setParameter("toDate", filter.getTo());
        }
        long count = (long) countQuery.getSingleResult();
        if (count > 0) {
            return new PageImpl<>(query.getResultList(), filter.getSortedPageable(), count);
        }
        return new PageImpl<>(new ArrayList<>(), filter.getSortedPageable(), count);
    }

    private StringBuilder getBaseSql(StockFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from stock_adjustment_product sp ");
        sql.append(" inner join stock_adjustment s on s.id = sp.stock_adjustment_id ");
        sql.append(" inner join company c on s.company_id = c.id ");
        sql.append(" inner join product p on p.id = sp.product_id ");
        sql.append(" where sp.deleted =false and p.deleted =false ");
        sql.append(" and p.id = ").append(filter.getProductId());
        sql.append(" and s.status='").append(StockStatus.COMPLETED.getCode()).append("'");
        sql.append(" and s.warehouse_id=").append(filter.getWarehouseId());
        if (filter.getProductSerialId() != null) {
            sql.append(" and sp.product_serial_id=").append(filter.getProductSerialId());
        }
        if (filter.getFrom() != null) {
            sql.append(" and s.adjustment_date>=:fromDate ");
        }
        if (filter.getTo() != null) {
            sql.append(" and s.adjustment_date<=:toDate ");
        }
        return sql;
    }

}
