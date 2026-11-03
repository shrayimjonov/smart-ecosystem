package uz.uzkassa.cts.service;

import uz.uzkassa.cts.service.impl.JdbcSpringManagerImpl;

public interface JdbcSpringManager {

    JdbcSpringManagerImpl getSimpleJdbcTemplate();

}
