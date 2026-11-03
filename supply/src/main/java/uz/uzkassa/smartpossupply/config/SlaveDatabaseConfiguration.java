package uz.uzkassa.smartpossupply.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;

import static org.hibernate.cfg.AvailableSettings.*;
import static org.hibernate.tool.schema.Action.ACTION_NONE;

@Slf4j
@Configuration
@EnableJpaRepositories(entityManagerFactoryRef = "slaveEntityManagerFactory",
        transactionManagerRef = "slaveTransactionManager")
public class SlaveDatabaseConfiguration {

    @Autowired
    private Environment env;

    @Bean("slaveTransactionManager")
    public PlatformTransactionManager slaveTransactionManager() {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(slaveEntityManagerFactory().getObject());
        return transactionManager;
    }

    @Bean(name = "slaveDataSource", destroyMethod = "close")
    public DataSource slaveDataSource() {
        log.debug("Configuring Slave Datasource");
        if (env.getProperty("spring.slave.datasource.url") == null) {
            log.error("Your slave database connection pool configuration is incorrect! The application" +
                            " cannot start. Please check your Spring profile, current profiles are: {}",
                    Arrays.toString(env.getActiveProfiles()));

            throw new ApplicationContextException("Slave database connection pool is not configured correctly");
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(env.getProperty("spring.slave.datasource.url"));
        config.setUsername(env.getProperty("spring.slave.datasource.username"));
        config.setPassword(env.getProperty("spring.slave.datasource.password"));
        config.setDriverClassName(env.getProperty("spring.slave.datasource.hikari.driver_class_Name", String.class, "org.postgresql.Driver"));
        config.setPoolName(env.getProperty("spring.slave.datasource.hikari.pool_name", String.class, "hikari-erp-slave-pool"));
        config.setMaximumPoolSize(env.getProperty("spring.slave.datasource.hikari.maximum_pool_size", Integer.class, 200));
        config.setMinimumIdle(env.getProperty("spring.slave.datasource.hikari.minimum_idle", Integer.class, 50));
        config.setConnectionTimeout(env.getProperty("spring.slave.datasource.hikari.connection_timeout", Integer.class, 30000));
        config.setIdleTimeout(env.getProperty("spring.slave.datasource.hikari.idle_timeout", Integer.class, 600000));
        config.setMaxLifetime(env.getProperty("spring.slave.datasource.hikari.max_lifetime", Integer.class, 1800000));
        config.setLeakDetectionThreshold(env.getProperty("spring.slave.datasource.hikari.leak_detection_threshold", Integer.class, 60000));
        config.setAutoCommit(env.getProperty("spring.slave.datasource.hikari.auto_commit", Boolean.class, false));
        return new HikariDataSource(config);
    }

    @Bean("slaveEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean slaveEntityManagerFactory() {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setPersistenceUnitName("slavePU");
        em.setDataSource(slaveDataSource());
        em.setPackagesToScan("uz.uzkassa.smartpossupply.domain");

        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        final HashMap<String, Object> properties = new HashMap<>();

        //Mapping Settings
        properties.put(IMPLICIT_NAMING_STRATEGY, org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy.class.getName());
        properties.put(PHYSICAL_NAMING_STRATEGY, org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy.class.getName());
        properties.put(PREFERRED_INSTANT_JDBC_TYPE, env.getProperty("spring.mapping.settings.hibernate.type.preferred_instant_jdbc_type", String.class, "TIMESTAMP"));
        properties.put(DEFAULT_SCHEMA, env.getProperty("spring.mapping.settings.hibernate.default.schema", String.class, "supply"));

        //Jdbc Settings
        properties.put(SHOW_SQL, env.getProperty("spring.jdbc.settings.hibernate.show_sql", Boolean.class, false));
        properties.put(FORMAT_SQL, env.getProperty("spring.jdbc.settings.hibernate.format_sql", Boolean.class, false));
        properties.put(STATEMENT_FETCH_SIZE, env.getProperty("spring.jdbc.settings.hibernate.jdbc.fetch_size", Integer.class, 50));
        properties.put(DEFAULT_BATCH_FETCH_SIZE, env.getProperty("spring.jdbc.settings.hibernate.jdbc.default_batch_fetch_size", Integer.class, 100));
        properties.put(LOG_SLOW_QUERY, env.getProperty("spring.jdbc.settings.hibernate.log_slow_query", Long.class, 60000L));
        properties.put(LOG_JDBC_WARNINGS, env.getProperty("spring.jdbc.settings.hibernate.jdbc.log.warnings", Boolean.class, false));
        properties.put(JDBC_TIME_ZONE, env.getProperty("spring.jdbc.settings.hibernate.jdbc.time_zone", String.class, "Asia/Tashkent"));
        properties.put(CONNECTION_PROVIDER_DISABLES_AUTOCOMMIT, env.getProperty("spring.jdbc.settings.hibernate.connection.provider_disables_autocommit", Boolean.class, true));
        properties.put(AUTOCOMMIT, env.getProperty("spring.jdbc.settings.hibernate.connection.autocommit", Boolean.class, false));

        //Transaction Settings
        properties.put(ENABLE_LAZY_LOAD_NO_TRANS, env.getProperty("spring.transaction.settings.hibernate.enable_lazy_load_no_trans", Boolean.class, true));

        //Cache Settings
        properties.put(USE_SECOND_LEVEL_CACHE, env.getProperty("spring.cache.settings.hibernate.cache.use_second_level_cache", Boolean.class, true));
        properties.put(USE_QUERY_CACHE, env.getProperty("spring.cache.settings.hibernate.cache.use_query_cache", Boolean.class, true));

        //Schema Tooling Settings
        properties.put(HBM2DDL_AUTO, env.getProperty("spring.schema.tooling.settings.hibernate.hbm2ddl.auto", String.class, ACTION_NONE));

        //Batch Settings
        properties.put(STATEMENT_BATCH_SIZE, env.getProperty("spring.batch.settings.hibernate.jdbc.batch_size", Integer.class, 50));
        properties.put(ORDER_UPDATES, env.getProperty("spring.batch.settings.hibernate.order_updates", Boolean.class, true));
        properties.put(ORDER_INSERTS, env.getProperty("spring.batch.settings.hibernate.order_inserts", Boolean.class, true));

        //Query Settings
        properties.put(FAIL_ON_PAGINATION_OVER_COLLECTION_FETCH, env.getProperty("spring.query.settings.fail_on_pagination_over_collection_fetch", Boolean.class, true));
        properties.put(IN_CLAUSE_PARAMETER_PADDING, env.getProperty("spring.query.settings.in_clause_parameter_padding", Boolean.class, true));

        //Statistics Settings
        properties.put(GENERATE_STATISTICS, env.getProperty("spring.statistics.settings.hibernate.generate_statistics", Boolean.class, false));

        properties.put("hibernate.ejb.entitymanager_factory_name", "slaveEmf");

        em.setJpaPropertyMap(properties);
        return em;
    }
}
