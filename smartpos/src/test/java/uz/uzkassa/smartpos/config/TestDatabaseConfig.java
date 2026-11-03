package uz.uzkassa.smartpos.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import liquibase.integration.spring.SpringLiquibase;
import lombok.RequiredArgsConstructor;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"uz.uzkassa.smartpos.repository.postgres"})
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
public class TestDatabaseConfig {
    private final Environment environment;

    @Bean
    @Primary
    public DataSource proxyDataSource() {
        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(environment.getProperty("spring.datasource.hikari.jdbc-url"));
        hikariConfig.setUsername(environment.getProperty("spring.datasource.hikari.username"));
        hikariConfig.setPassword(environment.getProperty("spring.datasource.hikari.password"));
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setMinimumIdle(5);
        hikariConfig.setAutoCommit(false);
        hikariConfig.setConnectionTimeout(30000);
        hikariConfig.setIdleTimeout(60000);
        var dataSource = new HikariDataSource(hikariConfig);
        return ProxyDataSourceBuilder.create(dataSource)
                .logQueryToSysOut()
                .countQuery()
                .build();
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    @Primary
    public JdbcClient jdbcClient(@Qualifier("proxyDataSource") DataSource dataSource) {
        return JdbcClient.create(dataSource);
    }


    @Bean
    public SpringLiquibase primaryLiquibase(@Qualifier("proxyDataSource") DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog("classpath:db/changelog/db.changelog-master.xml");
        liquibase.setContexts(String.join(",", environment.getActiveProfiles()));
        liquibase.setDataSource(dataSource);
        return liquibase;
    }
}
