package uz.uzkassa.horeca.config;

import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.hibernate.cache.jcache.ConfigSettings;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uz.uzkassa.horeca.constants.CacheConstants;
import uz.uzkassa.horeca.domain.ActivityType;
import uz.uzkassa.horeca.domain.Bank;
import uz.uzkassa.horeca.domain.address.Neighborhood;
import uz.uzkassa.horeca.domain.shift.Shift;
import uz.uzkassa.horeca.domain.unit.Unit;
import uz.uzkassa.horeca.domain.address.District;
import uz.uzkassa.horeca.domain.address.Region;
import uz.uzkassa.horeca.domain.auth.User;
import uz.uzkassa.horeca.domain.company.Company;
import uz.uzkassa.horeca.domain.factura.FacturaHost;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfiguration implements CacheConstants {
    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration() {
        jcacheConfiguration =
                Eh107Configuration.fromEhcacheCacheConfiguration(
                        CacheConfigurationBuilder
                                .newCacheConfigurationBuilder(Object.class, Object.class, ResourcePoolsBuilder.heap(10000))
                                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofDays(90)))
                                .build()
                );
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cacheManager) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cacheManager);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            createCache(cm, USERS_BY_LOGIN_CACHE);
            createCache(cm, DISTRICT_LIST_BY_REGION_ID_CACHE);
            createCache(cm, DISTRICT_BY_DISTRICT_ID_CACHE);
            createCache(cm, DISTRICT_BY_REGION_CODE_CACHE);
            createCache(cm, BANK_BY_MFO_CACHE);
            createCache(cm, REGION_BY_CODE_CACHE);
            createCache(cm, UNIT_BY_CODE_CACHE);
            createCache(cm, FACTURA_ACTIVE_HOST_CACHE);
            createCache(cm, TERMINAL_ROLE_CACHE);
            createCache(cm, TERMINAL_UNIT_CACHE);

            createCache(cm, User.class.getName());
            createCache(cm, ActivityType.class.getName());
            createCache(cm, Bank.class.getName());
            createCache(cm, Region.class.getName());
            createCache(cm, District.class.getName());
            createCache(cm, Neighborhood.class.getName());
            createCache(cm, Company.class.getName());
            createCache(cm, Unit.class.getName());
            createCache(cm, FacturaHost.class.getName());
            createCache(cm, Shift.class.getName());
        };
    }

    private void createCache(javax.cache.CacheManager cm, String cacheName) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        } else {
            cm.createCache(cacheName, jcacheConfiguration);
        }
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return new SimpleKeyGenerator();
    }
}
