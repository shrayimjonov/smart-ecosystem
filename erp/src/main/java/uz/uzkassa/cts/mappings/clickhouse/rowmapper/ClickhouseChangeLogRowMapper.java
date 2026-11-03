package uz.uzkassa.cts.mappings.clickhouse.rowmapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.changelog.ChangeLogListDTO;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.utils.DateUtil;
import uz.uzkassa.cts.validator.LocalizationMessage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Component
public class ClickhouseChangeLogRowMapper {

    private static LocalizationMessage localizationMessage;

    @Autowired
    public void setLocalizationServiceIn(LocalizationMessage localizationMessage) {
        ClickhouseChangeLogRowMapper.localizationMessage = localizationMessage;
    }

    public static class ClickhouseChangeLogMapper implements RowMapper<ChangeLogListDTO> {

        @Override
        public ChangeLogListDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            ChangeLogListDTO changeLogListDTO = new ChangeLogListDTO();
            changeLogListDTO.setId(rs.getString("id"));
            changeLogListDTO.setEntityId(Long.valueOf(rs.getString("entityId")));
            changeLogListDTO.setEntityType(EntityTypeEnum.valueOf(rs.getString("entityType")));
            changeLogListDTO.setField(localizationMessage.localize(rs.getString("field"), rs.getString("field")));
            changeLogListDTO.setOldValue(rs.getString("oldValue"));
            changeLogListDTO.setEntityName(rs.getString("entityName"));
            changeLogListDTO.setNewValue(rs.getString("newValue"));
            changeLogListDTO.setUpdatedDate(LocalDateTime.parse(rs.getString("updatedDate"), DateUtil.LONG_FORMATTER));
            changeLogListDTO.setUpdater(new CommonDTO(rs.getLong("updaterId"), rs.getString("updaterName")));
            return changeLogListDTO;
        }
    }
}
