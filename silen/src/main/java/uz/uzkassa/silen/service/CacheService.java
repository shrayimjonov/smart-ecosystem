package uz.uzkassa.silen.service;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import uz.uzkassa.silen.config.CacheConstants;
import uz.uzkassa.silen.domain.Organization;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class CacheService implements CacheConstants {

    CacheManager cacheManager;
    EntityManagerFactory entityManagerFactory;

    public void clearAll() {
        cacheManager
            .getCacheNames()
            .forEach(cacheName -> {
                try {
                    Cache cache = cacheManager.getCache(cacheName);
                    if (cache != null) {
                        cache.clear();
                    }
                } catch (Exception e) {
                    log.error("Delete cache {} error {}: ", cacheName, e.getMessage());
                }
            });
    }

    public List<String> getList() {
        return new ArrayList<>(cacheManager.getCacheNames());
    }

    public void clear(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }

    public void evict(String cacheName, Object key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
        }
    }

    public void evictCompany(String tin) {
        this.evict(COMPANY_BY_TIN_CACHE, tin);
    }

    public void evictCompany(Organization organization) {
        if (organization != null) {
            this.evictCompany(organization.getTin());
        }
    }

    public void evictCollectionData(String cacheCollectionName, Serializable id) {
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        sessionFactory.getCache().evictCollectionData(cacheCollectionName, id);
    }

    public void evictCollectionData(String cacheCollectionName) {
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        sessionFactory.getCache().evictCollectionData(cacheCollectionName);
    }
}
