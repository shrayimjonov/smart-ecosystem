package uz.uzkassa.smartpossupply.repository.clickhouse;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.materialreport.MaterialReportDTO;
import uz.uzkassa.smartpossupply.filters.MaterialReportFilter;
import uz.uzkassa.smartpossupply.mappers.clickhouse.rowmapper.ClickhouseMaterialReportRowMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Created time: 05.05.2025 11:48
 */

@Component
public class ClickhouseMaterialReportRepository extends ClickhouseBaseRepository {

    public List<MaterialReportDTO> getReport(MaterialReportFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("""
                SELECT
                    branchId,
                    any(branchName) AS branchName,
                    sum(initialQty)    AS totalInitialQty,
                    sum(initialPrice)  AS totalInitialPrice,
                    sum(lastQty)       AS totalLastQty,
                    sum(lastPrice)     AS totalLastPrice,
                    sum(stockInQty)    AS totalStockInQty,
                    sum(stockInPrice)  AS totalStockInPrice,
                    sum(stockOutQty)   AS totalStockOutQty,
                    sum(stockOutPrice) AS totalStockOutPrice,
                    groupArray(concat(
                                              '{',
                                              '"warehouseId":', toString(warehouseId), ',',
                                              '"warehouseName":"', replaceAll(warehouseName, '"', '\\\\"'), '",',
                                              '"productId":', toString(productId), ',',
                                              '"productName":"', replaceAll(productName, '"', '\\\\"'), '",',
                                              '"productBarcode":"', toString(ifNull(productBarcode, '')), '",',
                                              '"productSerialNumber":"', toString(ifNull(productSerialNumber, '')), '",',
                                              '"initialQty":', toString(initialQty), ',',
                                              '"initialPrice":', toString(initialPrice), ',',
                                              '"lastQty":', toString(lastQty), ',',
                                              '"lastPrice":', toString(lastPrice), ',',
                                              '"stockInQty":', toString(stockInQty), ',',
                                              '"stockInPrice":', toString(stockInPrice), ',',
                                              '"stockOutQty":', toString(stockOutQty), ',',
                                              '"stockOutPrice":', toString(stockOutPrice),
                                              '}'
                                          ))          AS items""");
        sql.append(" FROM (");
        sql.append("""
                SELECT
                    branchId AS branchId,
                    any(branchName) AS branchName,
                    warehouseId AS warehouseId,
                    any(warehouseName) AS warehouseName,
                    productId AS productId,
                    any(productName) AS productName,
                    any(productBarcode) AS productBarcode,
                    any(productSerialNumber) AS productSerialNumber,""");
        sql.append(" argMin(if(date = '").append((Optional.ofNullable(filter.getFrom().toLocalDate()).orElse(LocalDate.now()).minusDays(1))).append("', qty, 0), date) AS initialQty,\n");
        sql.append(" argMin(if(date = '").append((Optional.ofNullable(filter.getFrom().toLocalDate()).orElse(LocalDate.now()).minusDays(1))).append("', price, 0), date) AS initialPrice,\n");
        sql.append(" argMax(if(date >= '").append((Optional.ofNullable(filter.getFrom().toLocalDate()).orElse(LocalDate.now()))).append("' AND date <= '").append(Optional.ofNullable(filter.getTo().toLocalDate()).orElse(LocalDate.now())).append("', qty, 0), date) AS lastQty,\n");
        sql.append(" argMax(if(date >= '").append((Optional.ofNullable(filter.getFrom().toLocalDate()).orElse(LocalDate.now()))).append("' AND date <= '").append(Optional.ofNullable(filter.getTo().toLocalDate()).orElse(LocalDate.now())).append("', price, 0), date) AS lastPrice,\n");
        sql.append(" sum(if(date >= '").append((Optional.ofNullable(filter.getFrom().toLocalDate()).orElse(LocalDate.now()))).append("' AND date <= '").append(Optional.ofNullable(filter.getTo().toLocalDate()).orElse(LocalDate.now())).append("', stockInQty, 0)) AS stockInQty,\n");
        sql.append(" sum(if(date >= '").append((Optional.ofNullable(filter.getFrom().toLocalDate()).orElse(LocalDate.now()))).append("' AND date <= '").append(Optional.ofNullable(filter.getTo().toLocalDate()).orElse(LocalDate.now())).append("', stockInPrice, 0)) AS stockInPrice,\n");
        sql.append(" sum(if(date >= '").append((Optional.ofNullable(filter.getFrom().toLocalDate()).orElse(LocalDate.now()))).append("' AND date <= '").append(Optional.ofNullable(filter.getTo().toLocalDate()).orElse(LocalDate.now())).append("', stockOutQty, 0)) AS stockOutQty,\n");
        sql.append(" sum(if(date >= '").append((Optional.ofNullable(filter.getFrom().toLocalDate()).orElse(LocalDate.now()))).append("' AND date <= '").append(Optional.ofNullable(filter.getTo().toLocalDate()).orElse(LocalDate.now())).append("', stockOutPrice, 0)) AS stockOutPrice\n");
        sql.append(" FROM material_report mr");
        sql.append(" WHERE 1=1");
        sql.append(" AND (mr.date = '").append(Optional.ofNullable(filter.getFrom().toLocalDate()).orElse(LocalDate.now()).minusDays(1)).append("'");
        sql.append(" OR mr.date >= '").append(Optional.ofNullable(filter.getFrom().toLocalDate()).orElse(LocalDate.now())).append("'");
        sql.append(" AND mr.date <= '").append(Optional.ofNullable(filter.getTo().toLocalDate()).orElse(LocalDate.now())).append("')");
        if (CollectionUtils.isNotEmpty(filter.getBranchIds())) {
            sql.append(" AND mr.branchId IN (").append(convertLongListToString(filter.getBranchIds())).append(")");
        }
        if (CollectionUtils.isNotEmpty(filter.getWarehouseIds())) {
            sql.append(" AND mr.warehouseId IN (").append(convertLongListToString(filter.getWarehouseIds())).append(")");
        }
        if (filter.isNotEmpty()) {
            sql.append(" AND (");
            sql.append(" lowerUTF8(mr.productName)").append(filter.getSearchValueClickhouse());
            sql.append(" OR lowerUTF8(mr.productBarcode)").append(filter.getSearchValueClickhouse());
            sql.append(" OR lowerUTF8(mr.productSerialNumber)").append(filter.getSearchValueClickhouse());
            sql.append(")");
        }
        sql.append(" GROUP BY branchId, warehouseId, productId ) AS temp");
        sql.append(" GROUP BY branchId");

        return jdbcTemplate.query(sql.toString(), new ClickhouseMaterialReportRowMapper.ClickhouseMaterialReportMapper());
    }

    @Override
    public String getTableName() {
        return "material_report";
    }
}
