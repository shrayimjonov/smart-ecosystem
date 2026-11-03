package uz.uzkassa.smartpossupply.config;

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
import uz.uzkassa.smartpossupply.constants.CacheConstants;
import uz.uzkassa.smartpossupply.domain.ActivityType;
import uz.uzkassa.smartpossupply.domain.Bank;
import uz.uzkassa.smartpossupply.domain.Unit;
import uz.uzkassa.smartpossupply.domain.XtraceToken;
import uz.uzkassa.smartpossupply.domain.address.Country;
import uz.uzkassa.smartpossupply.domain.address.District;
import uz.uzkassa.smartpossupply.domain.address.Region;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.factura.FacturaHost;
import uz.uzkassa.smartpossupply.domain.settings.CompanySettings;
import uz.uzkassa.smartpossupply.domain.settings.GenericSettings;

import java.time.Duration;

@Configuration(proxyBeanMethods = false)
@EnableCaching
public class CacheConfiguration implements CacheConstants {

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cacheManager) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cacheManager);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            createCache(cm, USER_BY_LOGIN_CACHE, 3600, 1000);
            createCache(cm, UNIT_LIST_CACHE, 3600, 1000);
            createCache(cm, UNIT_BY_CODE_CACHE, 86400, 100);
            createCache(cm, COMPANY_BY_TIN_CACHE, 86400, 500);
            createCache(cm, FACTURA_ACTIVE_HOST_CACHE, 86400, 100);
            createCache(cm, COUNTRY_LIST_CACHE, 86400, 10000);
            createCache(cm, COUNTRY_BY_CONTINENT_CACHE, 86400, 10000);
            createCache(cm, REGION_LIST_CACHE, 86400, 10000);
            createCache(cm, REGION_BY_CODE_CACHE, 86400, 10);
            createCache(cm, DISTRICT_BY_REGION_CODE_CACHE, 86400, 10);
            createCache(cm, DISTRICT_LIST_BY_REGION_ID_CACHE, 86400, 10);
            createCache(cm, BANK_BY_MFO_CACHE, 86400, 10);
            createCache(cm, ACTIVITY_TYPE_LIST_CACHE, 86400, 10);
            createCache(cm, ACTIVITY_TYPE_LIST_BY_PARENT_ID_CACHE, 86400, 10);
            createCache(cm, XTRACE_TOKEN_BY_COMPANY_ID_CACHE, 86400, 10000);
            createCache(cm, COMPANY_SETTINGS_BY_TYPE_CACHE, 86400, 10000);
            createCache(cm, GENERIC_SETTINGS_BY_TYPE_CACHE, 86400, 10000);

            createCache(cm, User.class.getName(), 3600, 1000);
            createCache(cm, ActivityType.class.getName(), 3600, 1000);
            createCache(cm, District.class.getName(), 3600, 1000);
            createCache(cm, Region.class.getName(), 3600, 1000);
            createCache(cm, Country.class.getName(), 3600, 1000);
            createCache(cm, Unit.class.getName(), 3600, 1000);
            createCache(cm, Bank.class.getName(), 3600, 1000);
            createCache(cm, Company.class.getName(), 3600, 1000);
            createCache(cm, FacturaHost.class.getName(), 3600, 1000);
            createCache(cm, CompanySettings.class.getName(), 86400, 10000);
            createCache(cm, XtraceToken.class.getName(), 86400, 10000);
            createCache(cm, GenericSettings.class.getName(), 86400, 10000);
        };
    }

    private void createCache(javax.cache.CacheManager cm, String cacheName, long seconds, long heapSize) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        } else {
            cm.createCache(cacheName, Eh107Configuration.fromEhcacheCacheConfiguration(
                CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class, ResourcePoolsBuilder.heap(heapSize))
                    .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(seconds))).build()));
        }
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return new SimpleKeyGenerator();
    }
}
