package uz.uzkassa.cts.mappings.mysql;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class PostfixDomainRowMapper implements RowMapper<CommonDTO> {
    @Override
    public CommonDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new CommonDTO(
                ThreadLocalRandom.current().nextLong(1L, 1001L),
                rs.getString("domain")
        );
    }
}