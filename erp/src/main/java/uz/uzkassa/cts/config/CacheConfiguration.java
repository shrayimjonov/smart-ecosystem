package uz.uzkassa.cts.config;

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
import uz.uzkassa.cts.constants.CacheConstants;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.domain.address.District;
import uz.uzkassa.cts.domain.address.Neighborhood;
import uz.uzkassa.cts.domain.address.Region;
import uz.uzkassa.cts.domain.hrms.Position;
import uz.uzkassa.cts.domain.permission.Authority;

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
            createCache(cm, USER_BY_LOGIN_CACHE);

            createCache(cm, POSITION_ENTITY_LIST_CACHE);
            createCache(cm, POSITION_ENTITY_LIST_BY_STATUS_CACHE);
            createCache(cm, POSITION_ITEMS_CACHE);
            createCache(cm, POSITION_BY_CODE_CACHE);
            createCache(cm, POSITION_LIST_CACHE);

            createCache(cm, BANK_LOOKUP_CACHE);
            createCache(cm, BANK_LIST_CACHE);
            createCache(cm, BANK_ENTITY_BY_CODE_CACHE);

            createCache(cm, MODEL_BY_CODE_CACHE);
            createCache(cm, MODEL_LIST_CACHE);

            createCache(cm, FISCAL_MODULE_BY_SERIAL_NUMBER);
            createCache(cm, DEVICE_BY_SERIAL_NUMBER);
            createCache(cm, FISCALIZATION_CHECK_BY_HASH);

            createCache(cm, REGION_ENTITY_BY_CODE_CACHE);
            createCache(cm, REGION_ENTITY_LIST_CACHE);
            createCache(cm, REGION_ITEMS_CACHE);

            createCache(cm, DISTRICT_BY_REGION_ID_CACHE);
            createCache(cm, DISTRICT_ENTITY_BY_CODE_AND_REGION_ID_CACHE);
            createCache(cm, DISTRICT_ENTITY_LIST_BY_REGION_ID_CACHE);

            createCache(cm, ACTIVITY_ENTITY_PARENT_LIST_CACHE);
            createCache(cm, ACTIVITY_ENTITY_LIST_BY_PARENT_ID_CACHE);

            createCache(cm, REFERENCE_ENTITY_BY_TYPE_CACHE);
            createCache(cm, REFERENCE_ENTITY_BY_TYPE_AND_CODE_CACHE);
            createCache(cm, REFERENCE_ITEMS_BY_TYPE_CACHE);

            createCache(cm, VAT_ENTITY_LIST_CACHE);
            createCache(cm, VAT_ENTITY_LIST_BY_STATUS_CACHE);
            createCache(cm, VAT_LIST_CACHE);
            createCache(cm, VAT_ITEMS_CACHE);

            createCache(cm, INTEGRATION_BY_TYPE_CACHE);

            createCache(cm, ActivityType.class.getName());
            createCache(cm, Authority.class.getName());
            createCache(cm, User.class.getName() + ".authorities");
            createCache(cm, Bank.class.getName());
            createCache(cm, Position.class.getName());
            createCache(cm, Vat.class.getName());
            createCache(cm, Reference.class.getName());
            createCache(cm, Integration.class.getName());

            createCache(cm, District.class.getName());
            createCache(cm, Region.class.getName());
            createCache(cm, Neighborhood.class.getName());

            createCache(cm, User.class.getName());
            createCache(cm, Device.class.getName());
            createCache(cm, DeviceModel.class.getName());
            createCache(cm, Fiscalization.class.getName());
            createCache(cm, FiscalModule.class.getName());
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
