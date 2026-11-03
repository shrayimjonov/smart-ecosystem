package uz.uzkassa.cts.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.constants.CacheConstants;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.domain.address.District;
import uz.uzkassa.cts.domain.address.Neighborhood;
import uz.uzkassa.cts.domain.address.Region;
import uz.uzkassa.cts.domain.hrms.Position;
import uz.uzkassa.cts.domain.permission.Authority;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.service.CacheService;

import java.util.List;

import static uz.uzkassa.cts.service.impl.FiscalizationServiceImpl.fiscalizationsMap;

@Transactional
@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService, CacheConstants {

    CacheManager cacheManager;

    @Override
    public void clearBatch() {
        cacheManager.getCacheNames().forEach(this::clear);
    }

    @Override
    public void clear(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }

    public void clearBatch(String... cacheNames) {
        List.of(cacheNames).forEach(this::clear);
    }

    @Override
    public void evict(String cacheName, String key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
        }
    }

    @Override
    public void putCache(String cacheName, String key, Object value) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.put(key, value);
        }
    }

    @Override
    public List<String> getList() {
        return cacheManager.getCacheNames().stream().toList();
    }

    @Override
    public void clearByEntityType(EntityTypeEnum entityType) {
        switch (entityType) {
            case USER,
                 EMPLOYEE ->
                    clearBatch(USER_BY_LOGIN_CACHE, User.class.getName(), User.class.getName().concat(".authorities"));
            case ROLE ->
                    clearBatch(Authority.class.getName(), User.class.getName().concat(".authorities"));
            case POSITION ->
                    clearBatch(POSITION_ENTITY_LIST_CACHE, POSITION_ENTITY_LIST_BY_STATUS_CACHE, POSITION_LIST_CACHE, POSITION_ITEMS_CACHE, POSITION_BY_CODE_CACHE, Position.class.getName());
            case DEVICE_MODEL ->
                    clearBatch(MODEL_BY_CODE_CACHE, MODEL_LIST_CACHE, DeviceModel.class.getName());
            case FISCAL_MODULE ->
                    clearBatch(FISCAL_MODULE_BY_SERIAL_NUMBER, FiscalModule.class.getName());
            case DEVICE ->
                    clearBatch(DEVICE_BY_SERIAL_NUMBER, Device.class.getName());
            case FISCALIZATION ->
                    clearBatch(FISCALIZATION_CHECK_BY_HASH, Fiscalization.class.getName());
            case REGION ->
                    clearBatch(REGION_ENTITY_BY_CODE_CACHE, REGION_ENTITY_LIST_CACHE, REGION_ITEMS_CACHE, Region.class.getName());
            case DISTRICT ->
                    clearBatch(DISTRICT_BY_REGION_ID_CACHE, DISTRICT_ENTITY_LIST_BY_REGION_ID_CACHE, DISTRICT_ENTITY_BY_CODE_AND_REGION_ID_CACHE, District.class.getName());
            case BANK ->
                    clearBatch(BANK_LIST_CACHE, BANK_LOOKUP_CACHE, BANK_ENTITY_BY_CODE_CACHE, Bank.class.getName());
            case REFERENCE ->
                    clearBatch(REFERENCE_ENTITY_BY_TYPE_CACHE, REFERENCE_ENTITY_BY_TYPE_AND_CODE_CACHE, REFERENCE_ITEMS_BY_TYPE_CACHE, Reference.class.getName());
            case VAT ->
                    clearBatch(VAT_ENTITY_LIST_CACHE, VAT_ENTITY_LIST_BY_STATUS_CACHE, VAT_LIST_CACHE, VAT_ITEMS_CACHE, Vat.class.getName());
            case ACTIVITY_TYPE ->
                    clearBatch(ACTIVITY_ENTITY_PARENT_LIST_CACHE, ACTIVITY_ENTITY_LIST_BY_PARENT_ID_CACHE, ActivityType.class.getName());
            case NEIGHBORHOOD ->
                    clearBatch(Neighborhood.class.getName());
            case INTEGRATION ->
                    clearBatch(INTEGRATION_BY_TYPE_CACHE, Integration.class.getName());
        }
    }

    @Override
    public void evictFiscalCache(String key) {
        this.evict(FISCALIZATION_CHECK_BY_HASH,key);
        fiscalizationsMap.remove(key);
    }
}
