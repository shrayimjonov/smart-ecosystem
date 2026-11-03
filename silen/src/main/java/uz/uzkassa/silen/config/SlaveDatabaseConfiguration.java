package uz.uzkassa.silen.config;

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
        config.setDriverClassName("org.postgresql.Driver");
        config.setUsername(env.getProperty("spring.slave.datasource.username"));
        config.setPassword(env.getProperty("spring.slave.datasource.password"));
        config.setMaximumPoolSize(env.getProperty("spring.slave.datasource.maximumPoolSize", Integer.class, 200));
        config.setMinimumIdle(env.getProperty("spring.slave.datasource.minimumIdle", Integer.class, 20));
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(60000);
        return new HikariDataSource(config);
    }

    @Bean("slaveEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean slaveEntityManagerFactory() {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setPersistenceUnitName("slavePU");
        em.setDataSource(slaveDataSource());
        em.setPackagesToScan("uz.uzkassa.silen.domain");

        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        final HashMap<String, Object> properties = new HashMap<>();
        properties.put(IMPLICIT_NAMING_STRATEGY, org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy.class.getName());
        properties.put(PHYSICAL_NAMING_STRATEGY, org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy.class.getName());
        properties.put("spring.jpa.hibernate.show_sql", env.getProperty("spring.slave.jpa.show_sql"));
        properties.put(ENABLE_LAZY_LOAD_NO_TRANS, true);

        properties.put(USE_SECOND_LEVEL_CACHE, env.getProperty("spring.slave.jpa.properties.hibernate.cache.use_second_level_cache"));
        properties.put(STATEMENT_BATCH_SIZE, env.getProperty("spring.slave.jpa.properties.hibernate.jdbc.batch_size"));
        properties.put(SHOW_SQL, env.getProperty("spring.slave.jpa.show_sql"));
        properties.put(HBM2DDL_AUTO, "none");
        properties.put("hibernate.ejb.entitymanager_factory_name", "slaveEmf");

        em.setJpaPropertyMap(properties);
        return em;
    }
}
