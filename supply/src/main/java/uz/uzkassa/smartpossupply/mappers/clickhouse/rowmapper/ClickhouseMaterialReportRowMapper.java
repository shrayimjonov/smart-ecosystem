package uz.uzkassa.smartpossupply.mappers.clickhouse.rowmapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.RowMapper;
import uz.uzkassa.smartpossupply.dto.materialreport.MaterialReportDTO;
import uz.uzkassa.smartpossupply.dto.materialreport.MaterialReportItemDTO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created time: 12.05.2025 18:01
 */

public class ClickhouseMaterialReportRowMapper {

    public static class ClickhouseMaterialReportMapper implements RowMapper<MaterialReportDTO> {
        @Override
        public MaterialReportDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
            MaterialReportDTO materialReportDTO = new MaterialReportDTO();

            materialReportDTO.setBranchId(rs.getLong("branchId"));
            materialReportDTO.setBranchName(rs.getString("branchName"));
            materialReportDTO.setTotalInitialQty(rs.getBigDecimal("totalInitialQty"));
            materialReportDTO.setTotalInitialPrice(rs.getBigDecimal("totalInitialPrice"));
            materialReportDTO.setTotalLastQty(rs.getBigDecimal("totalLastQty"));
            materialReportDTO.setTotalLastPrice(rs.getBigDecimal("totalLastPrice"));
            materialReportDTO.setTotalStockInQty(rs.getBigDecimal("totalStockInQty"));
            materialReportDTO.setTotalStockInPrice(rs.getBigDecimal("totalStockInPrice"));
            materialReportDTO.setTotalStockOutQty(rs.getBigDecimal("totalStockOutQty"));
            materialReportDTO.setTotalStockOutPrice(rs.getBigDecimal("totalStockOutPrice"));
            List<MaterialReportItemDTO> items = new ArrayList<>();
            for (Object item : (Object[]) rs.getArray("items").getArray()) {
                try {
                    items.add(objectMapper.readValue(item.toString(), MaterialReportItemDTO.class));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            materialReportDTO.setItems(items);
            return materialReportDTO;
        }
    }
}
