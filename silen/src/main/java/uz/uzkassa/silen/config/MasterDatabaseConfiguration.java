package uz.uzkassa.silen.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;

import static org.hibernate.cfg.AvailableSettings.*;

@Slf4j
@Configuration
@EnableJpaRepositories(basePackages = "uz.uzkassa.silen.repository.pg", entityManagerFactoryRef = "masterEntityManagerFactory",
    transactionManagerRef = "masterTransactionManager")
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
@EnableTransactionManagement
public class MasterDatabaseConfiguration {

    @Autowired
    private Environment env;

    @Bean(destroyMethod = "close")
    public DataSource masterDataSource() {
        log.debug("Configuring Master Datasource");
        if (env.getProperty("spring.master.datasource.url") == null) {
            log.error("Your database connection pool configuration is incorrect! The application" +
                    " cannot start. Please check your Spring profile, current profiles are: {}",
                Arrays.toString(env.getActiveProfiles()));
            throw new ApplicationContextException("Database connection pool is not configured correctly");
        }

        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.postgresql.Driver");
        config.setJdbcUrl(env.getProperty("spring.master.datasource.url"));
        config.setUsername(env.getProperty("spring.master.datasource.username"));
        config.setPassword(env.getProperty("spring.master.datasource.password"));
        config.setMaximumPoolSize(env.getProperty("spring.master.datasource.maximumPoolSize", Integer.class, 200));
        config.setMinimumIdle(env.getProperty("spring.master.datasource.minimumIdle", Integer.class, 20));
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(60000);
        config.setAutoCommit(false);
        return new HikariDataSource(config);
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean masterEntityManagerFactory() {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setPersistenceUnitName("masterPU");
        em.setDataSource(masterDataSource());
        em.setPackagesToScan("uz.uzkassa.silen.domain");

        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        final HashMap<String, Object> properties = new HashMap<String, Object>();
        properties.put(IMPLICIT_NAMING_STRATEGY, org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy.class.getName());
        properties.put(PHYSICAL_NAMING_STRATEGY, org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy.class.getName());
        properties.put(SHOW_SQL, env.getProperty("spring.master.jpa.show_sql"));
        properties.put(ENABLE_LAZY_LOAD_NO_TRANS, true);

        properties.put(USE_SECOND_LEVEL_CACHE, env.getProperty("spring.master.jpa.properties.hibernate.cache.use_second_level_cache"));
        properties.put(HBM2DDL_AUTO, env.getProperty("spring.master.jpa.properties.hibernate.hbm2ddl.auto"));
        properties.put(STATEMENT_BATCH_SIZE, env.getProperty("spring.master.jpa.properties.hibernate.jdbc.batch_size"));
        properties.put("hibernate.ejb.entitymanager_factory_name", "masterEmf");
        properties.put(CONNECTION_PROVIDER_DISABLES_AUTOCOMMIT, true);
        em.setJpaPropertyMap(properties);
        return em;
    }

    @Primary
    @Bean("masterTransactionManager")
    public PlatformTransactionManager masterTransactionManager() {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(masterEntityManagerFactory().getObject());
        return transactionManager;
    }
}
