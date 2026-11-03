package uz.uzkassa.smartpos.config.clickhouse;

import com.clickhouse.jdbc.ClickHouseDriver;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import liquibase.integration.spring.SpringLiquibase;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.simple.JdbcClient;

import javax.sql.DataSource;

@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class ClickhouseConfig {
    @Value("${spring.clickhouse.url}")
    private String clickhouseUrl;
    @Value("${spring.clickhouse.username}")
    private String clickhouseUsername;
    private final Environment environment;

    @Bean("clickhouseDataSource")
    public HikariDataSource clickHouseDataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(clickhouseUrl);
        hikariConfig.setUsername(clickhouseUsername);
        hikariConfig.setDriverClassName(ClickHouseDriver.class.getName());
        hikariConfig.setMinimumIdle(20);
        hikariConfig.setMaximumPoolSize(200);
        return new HikariDataSource(hikariConfig);
    }

    @Bean
    public JdbcClient clickhouseJdbcClient(@Qualifier("clickhouseDataSource") HikariDataSource clickhouseDataSource) {
        return JdbcClient.create(clickhouseDataSource);
    }

    @Bean
    public SpringLiquibase clickhouseLiquibase(@Qualifier("clickhouseDataSource") DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog("classpath:clickhouse/changelog/db.changelog-master.xml");
        liquibase.setContexts(String.join(",", environment.getActiveProfiles()));
        liquibase.setDataSource(dataSource);
        return liquibase;
    }
}
