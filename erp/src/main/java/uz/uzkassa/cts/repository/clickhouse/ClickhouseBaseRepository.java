package uz.uzkassa.cts.repository.clickhouse;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.interfaces.ClickhouseTable;

@Component
public abstract class ClickhouseBaseRepository implements ClickhouseTable {

    @Autowired
    @Qualifier("clickhouseJdbcTemplate")
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected ObjectMapper objectMapper;

    public void insert(String insertQuery) {
        jdbcTemplate.update("INSERT INTO " + getTableName() + " FORMAT JSONEachRow " + insertQuery);
    }
}
