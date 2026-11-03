package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import uz.uzkassa.smartpossupply.dto.stock.StockProductQtyDetailListDTO;
import uz.uzkassa.smartpossupply.enums.StockTransferStatus;
import uz.uzkassa.smartpossupply.enums.TransferType;
import uz.uzkassa.smartpossupply.filters.StockFilter;
import uz.uzkassa.smartpossupply.repository.StockTransferProductRepositoryCustom;

import java.util.ArrayList;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 12.12.2023
 */
public class StockTransferProductRepositoryImpl extends BaseRepositoryImpl implements StockTransferProductRepositoryCustom {

    @Override
    public Page<StockProductQtyDetailListDTO> getQtyDetails(StockFilter filter) {
        filter.setWarehouseId(getWarehouseId(filter));
        StringBuilder sql = new StringBuilder();
        sql.append(" select s.id as id, s.transfer_number as stockNumber, s.transfer_date as stockDate, s.completed_date as stockCompletedDate, sp.qty as qty, fw.id as fromWarehouseId, fw.name as fromWarehouseName, tw.id as toWarehouseId, tw.name as toWarehouseName, c.id as companyId, c.name as companyName, c.tin as companyTin");
        sql.append(getBaseSql(filter));
        StringBuilder countSql = new StringBuilder();
        countSql.append("select count(s.id) ");
        countSql.append(getBaseSql(filter));
        sql.append(" order by s.id desc");
        Query query = entityManager
            .createNativeQuery(sql.toString(), "StockTransferProductQtyDetailListMapper")
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
        sql.append(" from stock_transfer_product sp ");
        sql.append(" inner join stock_transfer s on s.id = sp.stock_transfer_id ");
        sql.append(" inner join company c on s.company_id = c.id ");
        sql.append(" inner join product fp on fp.id = sp.from_product_id");
        sql.append(" inner join product tp on tp.id = sp.to_product_id ");
        sql.append(" inner join warehouse fw on s.from_warehouse_id = fw.id ");
        sql.append(" inner join warehouse tw on s.to_warehouse_id = tw.id ");
        sql.append(" where sp.deleted =false ");
        sql.append(" and s.status='").append(StockTransferStatus.APPROVED.getCode()).append("'");
        if (TransferType.TRANSFER_TO.equals(filter.getTransferType())) {
            sql.append(" and tw.id=").append(filter.getWarehouseId());
            sql.append(" and tp.id = ").append(filter.getProductId());
        } else if (TransferType.TRANSFER_FROM.equals(filter.getTransferType())) {
            sql.append(" and fw.id = ").append(filter.getWarehouseId());
            sql.append(" and fp.id = ").append(filter.getProductId());
        }
        if (filter.getProductSerialId() != null) {
            sql.append(" and sp.product_serial_id=").append(filter.getProductSerialId());
        }
        if (filter.getFrom() != null) {
            sql.append(" and s.transfer_date>=:fromDate ");
        }
        if (filter.getTo() != null) {
            sql.append(" and s.transfer_date<=:toDate ");
        }
        return sql;
    }
}
