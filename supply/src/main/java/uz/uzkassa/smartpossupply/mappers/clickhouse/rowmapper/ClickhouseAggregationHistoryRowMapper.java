package uz.uzkassa.smartpossupply.mappers.clickhouse.rowmapper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;
import uz.uzkassa.smartpossupply.dto.aggregation.AggregationHistoryListDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductCommonDTO;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.utils.DateUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ClickhouseAggregationHistoryRowMapper {

    public static class ClickhouseAggregationHistoryHeaderMapper implements RowMapper<AggregationHistoryListDTO> {
        @Override
        public AggregationHistoryListDTO mapRow(ResultSet rs, int rowNum) throws SQLException {

            AggregationHistoryListDTO aggregationHistoryListDTO = new AggregationHistoryListDTO();
            aggregationHistoryListDTO.setId(rs.getLong("id"));
            aggregationHistoryListDTO.setCode(rs.getString("code"));
            if (rs.getLong("productId") != 0) {
                ProductCommonDTO product = new ProductCommonDTO();
                product.setId(rs.getLong("productId"));
                product.setName(rs.getString("productName"));
                product.setBarcode(rs.getString("productBarcode"));
                product.setVatBarcode(rs.getString("productVatBarcode"));
                aggregationHistoryListDTO.setProduct(product);
            }
            aggregationHistoryListDTO.setEntityId(rs.getLong("entityId"));
            if (StringUtils.isNotEmpty(rs.getString("entityNumber"))) {
                aggregationHistoryListDTO.setEntityNumber(rs.getString("entityNumber"));
            }
            if (StringUtils.isNotEmpty(rs.getString("entityType"))) {
                aggregationHistoryListDTO.setEntityType(EntityTypeEnum.valueOf(rs.getString("entityType")));
            }
            if (rs.getString("transferType") != null) {
                aggregationHistoryListDTO.setTransferType(rs.getString("transferType"));
            }
            if (rs.getString("codeType") != null) {
                aggregationHistoryListDTO.setCodeType(rs.getString("codeType"));
            }
            aggregationHistoryListDTO.setCreateDate(LocalDateTime.parse(rs.getString("createdDate"), DateUtils.DATE_TIME_HYPHEN_FORMATTER));
            if (rs.getLong("creatorId") != 0) {
                aggregationHistoryListDTO.setCreator(rs.getString("creatorName"));
            }
            return aggregationHistoryListDTO;
        }
    }
}
