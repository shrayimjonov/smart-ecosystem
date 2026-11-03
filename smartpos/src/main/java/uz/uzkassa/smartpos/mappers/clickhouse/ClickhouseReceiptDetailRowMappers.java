package uz.uzkassa.smartpos.mappers.clickhouse;

import org.springframework.jdbc.core.RowMapper;
import uz.uzkassa.smartpos.dto.clickhouse.ClickhouseReceiptDetail;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ClickhouseReceiptDetailRowMappers {
    public static final RowMapper<ClickhouseReceiptDetail> CLICKHOUSE_RECEIPT_DETAIL = (rs, rowNum) -> {
        ClickhouseReceiptDetail receiptDetail = new ClickhouseReceiptDetail();
        receiptDetail.setId(rs.getLong("id"));
        receiptDetail.setReceiptId(rs.getLong("receiptId"));
        receiptDetail.setCategoryId(rs.getLong("categoryId"));
        receiptDetail.setCategoryName(rs.getString("categoryName"));
        receiptDetail.setProductId(rs.getLong("productId"));
        receiptDetail.setUnitId(rs.getLong("unitId"));
        receiptDetail.setUnitName(rs.getString("unitName"));
        receiptDetail.setAmount(rs.getBigDecimal("amount"));
        receiptDetail.setDiscount(rs.getBigDecimal("discount"));
        receiptDetail.setDiscountPercent(rs.getBigDecimal("discountPercent"));
        receiptDetail.setExcise(rs.getBigDecimal("excise"));
        receiptDetail.setExciseRate(rs.getBigDecimal("exciseRate"));
        receiptDetail.setMarks(Arrays.stream(((String[]) rs.getArray("marks").getArray())).collect(Collectors.toSet()));
        receiptDetail.setNds(rs.getBigDecimal("nds"));
        receiptDetail.setNdsPercent(rs.getBigDecimal("ndsPercent"));
        receiptDetail.setQty(rs.getBigDecimal("qty"));
        receiptDetail.setPrice(rs.getBigDecimal("price"));
        receiptDetail.setStatus(rs.getString("status"));
        receiptDetail.setProductBarcode(rs.getString("productBarcode"));
        receiptDetail.setVatBarcode(rs.getString("vatBarcode"));
        receiptDetail.setProductName(rs.getString("productName"));
        receiptDetail.setCommittentTin(rs.getString("committentTin"));
        receiptDetail.setExtraDetail(rs.getString("extraDetail"));
        return receiptDetail;
    };
}
