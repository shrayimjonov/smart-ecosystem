package uz.uzkassa.smartpossupply.mappers.clickhouse.rowmapper;

import org.springframework.jdbc.core.RowMapper;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.dto.loghistory.LogHistoryListDTO;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.service.LocalizationService;
import uz.uzkassa.smartpossupply.utils.BeanUtils;
import uz.uzkassa.smartpossupply.utils.DateUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ClickhouseLogHistoryRowMapper {

    public static class ClickhouseLogHistoryHeaderMapper implements RowMapper<LogHistoryListDTO> {
        LocalizationService localizationService = BeanUtils.getBean(LocalizationService.class);

        @Override
        public LogHistoryListDTO mapRow(ResultSet rs, int rowNum) throws SQLException {

            LogHistoryListDTO logHistoryListDTO = new LogHistoryListDTO();
            logHistoryListDTO.setId(rs.getString("id"));
            logHistoryListDTO.setEntityType(EntityTypeEnum.valueOf(rs.getString("entityType")));

            String universalNumber = null;
            switch (logHistoryListDTO.getEntityType()) {
                case ORDER -> universalNumber = safeGet(rs, "orderNumber");
                case STOCK, STOCK_IN, STOCK_OUT, STOCK_RETURN -> universalNumber = safeGet(rs, "stockNumber");
                case STOCK_ADJUSTMENT -> universalNumber = safeGet(rs, "adjustmentNumber");
                case STOCK_TRANSFER -> universalNumber = safeGet(rs, "transferNumber");
                case PRODUCT -> {
                    String name = safeGet(rs, "name");
                    String barcode = safeGet(rs, "barcode");
                    universalNumber = name + (barcode != null ? " (" + barcode + ")" : "");
                }
            }

            logHistoryListDTO.setUniversalNumber(universalNumber);
            logHistoryListDTO.setField(localizationService.localize(rs.getString("field"), rs.getString("field")));
            if (rs.getString("fromStringValue") != null) {
                logHistoryListDTO.setFromValue(rs.getString("fromStringValue"));
            } else if (rs.getBigDecimal("fromNumberValue") != null) {
                logHistoryListDTO.setFromValue(rs.getBigDecimal("fromNumberValue").toString());
            } else if (rs.getString("fromDateValue") != null) {
                logHistoryListDTO.setFromValue(LocalDateTime.parse(rs.getString("fromDateValue"), DateUtils.DATE_TIME_HYPHEN_FORMATTER).toString());
            }
            if (rs.getString("toStringValue") != null) {
                logHistoryListDTO.setToValue(rs.getString("toStringValue"));
            } else if (rs.getBigDecimal("toNumberValue") != null) {
                logHistoryListDTO.setToValue(rs.getBigDecimal("toNumberValue").toString());
            } else if (rs.getString("toDateValue") != null) {
                logHistoryListDTO.setToValue(LocalDateTime.parse(rs.getString("toDateValue"), DateUtils.DATE_TIME_HYPHEN_FORMATTER).toString());
            }
            logHistoryListDTO.setUpdateDate(LocalDateTime.parse(rs.getString("updatedDate"), DateUtils.DATE_TIME_HYPHEN_FORMATTER));
            logHistoryListDTO.setUpdater(new CommonDTO(rs.getLong("updaterId"), rs.getString("updaterName")));
            logHistoryListDTO.setCompany(new CompanyBaseDTO(rs.getLong("companyId"), rs.getString("companyName"), rs.getString("companyTin")));
            return logHistoryListDTO;
        }


        private String safeGet(ResultSet rs, String columnName) {
            try {
                return rs.getString(columnName);
            } catch (Exception e) {
                return null;
            }
        }

    }
}
