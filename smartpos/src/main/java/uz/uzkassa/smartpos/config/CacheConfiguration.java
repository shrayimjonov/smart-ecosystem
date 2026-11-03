package uz.uzkassa.smartpos.config;

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
import uz.uzkassa.smartpos.constants.CacheConstants;
import uz.uzkassa.smartpos.domain.postgres.common.Bank;
import uz.uzkassa.smartpos.domain.postgres.common.Excise;
import uz.uzkassa.smartpos.domain.postgres.common.Region;
import uz.uzkassa.smartpos.domain.postgres.common.Unit;
import uz.uzkassa.smartpos.domain.postgres.company.ActivityType;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.domain.postgres.company.BranchGroup;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.domain.postgres.receipt.Receipt;
import uz.uzkassa.smartpos.domain.postgres.user.Authority;
import uz.uzkassa.smartpos.domain.postgres.user.User;

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
            createCache(cm, USERS_BY_LOGIN_CACHE, 3600, 1000);
            createCache(cm, USERS_WITH_AUTHORITY_CACHE, 3600, 1000);
            createCache(cm, REGION_CACHE, 86400, 100);
            createCache(cm, CITY_CACHE, 86400, 500);
            createCache(cm, UNIT_CACHE, 86400, 100);
            createCache(cm, ADMIN_COMPANY_CACHE, 86400, 10);
            createCache(cm, ADMIN_COMPANY_MAIN_BRANCH_CACHE, 86400, 10);
            createCache(cm, User.class.getName(), 3600, 1000);
            createCache(cm, Authority.class.getName(), 86400, 1000);
            createCache(cm, User.class.getName() + ".authorities", 3600, 1000);
            createCache(cm, ActivityType.class.getName(), 3600, 1000);
            createCache(cm, Branch.class.getName(), 3600, 1000);
            createCache(cm, BranchGroup.class.getName(), 3600, 1000);
            createCache(cm, Company.class.getName(), 3600, 1000);
            createCache(cm, Receipt.class.getName(), 3600, 1000);
            createCache(cm, Region.class.getName(), 86400, 1000);
            createCache(cm, Excise.class.getName(), 86400, 100);
            createCache(cm, Unit.class.getName(), 86400, 100);
            createCache(cm, Bank.class.getName(), 86400, 1000);
            createCache(cm, CacheConstants.EPS_PROVIDERS_LOOKUP, 864000, 10000);
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
