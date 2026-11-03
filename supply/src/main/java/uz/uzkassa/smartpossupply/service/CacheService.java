package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.constants.CacheConstants;
import uz.uzkassa.smartpossupply.domain.ActivityType;
import uz.uzkassa.smartpossupply.domain.Bank;
import uz.uzkassa.smartpossupply.domain.XtraceToken;
import uz.uzkassa.smartpossupply.domain.address.Country;
import uz.uzkassa.smartpossupply.domain.Unit;
import uz.uzkassa.smartpossupply.domain.address.District;
import uz.uzkassa.smartpossupply.domain.address.Region;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.factura.FacturaHost;
import uz.uzkassa.smartpossupply.domain.settings.CompanySettings;
import uz.uzkassa.smartpossupply.domain.settings.GenericSettings;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;


@Service
@Transactional
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class CacheService implements CacheConstants {

    CacheManager cacheManager;

    public List<String> getList() {
        return new ArrayList<>(cacheManager.getCacheNames());
    }

    public void clear(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }

    public void clearBatch(String... cacheNames) {
        List.of(cacheNames).forEach(this::clear);
    }

    public void evict(String cacheName, Object key) {
        if (key != null) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.evict(key);
            }
        }
    }

    public void evictUser(String login) {
        this.evict(USER_BY_LOGIN_CACHE, login);
    }

    public void evictCompany(String tin) {
        this.evict(COMPANY_BY_TIN_CACHE, tin);
    }

    public void clearByEntityType(EntityTypeEnum entityType) {
        switch (entityType) {
            case USER -> clearBatch(USER_BY_LOGIN_CACHE, User.class.getName(), User.class.getName().concat(".authorities"));
            case UNIT -> clearBatch(UNIT_LIST_CACHE, UNIT_BY_CODE_CACHE, Unit.class.getName());
            case COMPANY -> clearBatch(COMPANY_BY_TIN_CACHE, Company.class.getName());
            case FACTURA_HOST -> clearBatch(FACTURA_ACTIVE_HOST_CACHE, FacturaHost.class.getName());
            case COUNTRY -> clearBatch(COUNTRY_LIST_CACHE, COUNTRY_BY_CONTINENT_CACHE, Country.class.getName());
            case REGION -> clearBatch(REGION_LIST_CACHE, REGION_BY_CODE_CACHE, Region.class.getName());
            case DISTRICT -> clearBatch(DISTRICT_BY_REGION_CODE_CACHE, DISTRICT_LIST_BY_REGION_ID_CACHE, District.class.getName());
            case BANK -> clearBatch(BANK_BY_MFO_CACHE, Bank.class.getName());
            case ACTIVITY_TYPE -> clearBatch(ACTIVITY_TYPE_LIST_CACHE, ACTIVITY_TYPE_LIST_BY_PARENT_ID_CACHE, ActivityType.class.getName());
            case XTRACE_TOKEN -> clearBatch(XTRACE_TOKEN_BY_COMPANY_ID_CACHE, XtraceToken.class.getName());
            case COMPANY_SETTINGS -> clearBatch(COMPANY_SETTINGS_BY_TYPE_CACHE, CompanySettings.class.getName());
            case GENERIC_SETTINGS -> clearBatch(GENERIC_SETTINGS_BY_TYPE_CACHE, GenericSettings.class.getName());
        }
    }
}
