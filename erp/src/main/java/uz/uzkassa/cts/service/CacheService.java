package uz.uzkassa.cts.service;

import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.util.List;

public interface CacheService {

    void clearBatch();

    void clear(String cacheName);

    void evict(String cacheName, String key);

    void putCache(String cacheName, String key, Object value);

    List<String> getList();

    void clearByEntityType(EntityTypeEnum entityType);

    void evictFiscalCache(String key);
}
