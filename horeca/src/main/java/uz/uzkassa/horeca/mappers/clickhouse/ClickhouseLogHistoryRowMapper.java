package uz.uzkassa.horeca.mappers.clickhouse;

import org.springframework.jdbc.core.RowMapper;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.company.CompanyCommonDTO;
import uz.uzkassa.horeca.dto.loghistory.LogHistoryListDTO;
import uz.uzkassa.horeca.enums.EntityTypeEnum;
import uz.uzkassa.horeca.utils.DateUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ClickhouseLogHistoryRowMapper {

    public static class ClickhouseLogHistoryHeaderMapper implements RowMapper<LogHistoryListDTO> {

        @Override
        public LogHistoryListDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            LogHistoryListDTO logHistoryListDTO = new LogHistoryListDTO();
            logHistoryListDTO.setId(rs.getString("id"));
            logHistoryListDTO.setEntityType(EntityTypeEnum.valueOf(rs.getString("entityType")));
            logHistoryListDTO.setField(rs.getString("field"));
            if (rs.getString("fromStringValue") != null) {
                logHistoryListDTO.setFromValue(rs.getString("fromStringValue"));
            } else if (rs.getBigDecimal("fromNumberValue") != null) {
                logHistoryListDTO.setFromValue(rs.getBigDecimal("fromNumberValue").toString());
            } else if (rs.getString("fromDateValue") != null) {
                logHistoryListDTO.setFromValue(
                    LocalDateTime.parse(rs.getString("fromDateValue"), DateUtils.dateTimeHyphenFormatter).toString()
                );
            }
            if (rs.getString("toStringValue") != null) {
                logHistoryListDTO.setToValue(rs.getString("toStringValue"));
            } else if (rs.getBigDecimal("toNumberValue") != null) {
                logHistoryListDTO.setToValue(rs.getBigDecimal("toNumberValue").toString());
            } else if (rs.getString("toDateValue") != null) {
                logHistoryListDTO.setToValue(
                    LocalDateTime.parse(rs.getString("toDateValue"), DateUtils.dateTimeHyphenFormatter).toString()
                );
            }
            logHistoryListDTO.setUpdateDate(LocalDateTime.parse(rs.getString("updatedDate"), DateUtils.dateTimeHyphenFormatter));
            logHistoryListDTO.setUpdater(new CommonDTO(Long.valueOf(rs.getString("updaterId")), rs.getString("updaterName")));
            logHistoryListDTO.setCompany(
                new CompanyCommonDTO(Long.valueOf(rs.getString("companyId")), rs.getString("companyName"), rs.getString("companyTin"))
            );
            return logHistoryListDTO;
        }
    }
}
