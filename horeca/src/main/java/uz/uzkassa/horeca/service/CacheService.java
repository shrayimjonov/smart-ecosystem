package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.horeca.constants.CacheConstants;
import uz.uzkassa.horeca.domain.auth.User;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class CacheService implements CacheConstants {

    CacheManager cacheManager;

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

    public void evictCache(String cacheName, String key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
        }
    }

    public void clearUserCache(User user) {
        if (user == null) {
            return;
        }
        this.clearUserCache(user.getLogin());
    }

    public void clearUserCache(String login) {
        Cache cache = cacheManager.getCache(USERS_BY_LOGIN_CACHE);
        if (cache != null) {
            cache.evict(login);
        }
    }
}
