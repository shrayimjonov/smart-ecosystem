package uz.uzkassa.cts.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.service.JdbcSpringManager;

import javax.sql.DataSource;


@Repository("jdbcSpringManager")
public class JdbcSpringManagerImpl extends NamedParameterJdbcTemplate implements JdbcSpringManager {

    @Autowired
    public JdbcSpringManagerImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public JdbcSpringManagerImpl getSimpleJdbcTemplate() {
        return this;
    }
}
