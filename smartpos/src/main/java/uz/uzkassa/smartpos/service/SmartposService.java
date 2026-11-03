package uz.uzkassa.smartpos.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpos.config.ApplicationProperties;
import uz.uzkassa.smartpos.constants.CacheConstants;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.domain.redis.BranchCityHash;
import uz.uzkassa.smartpos.enums.HeaderKeys;
import uz.uzkassa.smartpos.integration.ofd.OfdClient;
import uz.uzkassa.smartpos.repository.postgres.company.BranchRepository;
import uz.uzkassa.smartpos.repository.postgres.company.CompanyRepository;
import uz.uzkassa.smartpos.repository.redis.BranchCityRepository;
import uz.uzkassa.smartpos.security.AuthoritiesConstants;
import uz.uzkassa.smartpos.security.HeaderUtils;
import uz.uzkassa.smartpos.security.SecurityUtils;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
public abstract class SmartposService extends BaseService {
    @Autowired
    @Lazy
    protected CompanyRepository companyRepository;
    @Autowired
    @Lazy
    protected ApplicationProperties applicationProperties;
    @Autowired
    @Lazy
    protected BranchRepository branchRepository;
    @Autowired
    @Lazy
    protected CacheManager cacheManager;
    @Autowired
    @Lazy
    protected OfdClient ofdClient;
    @Autowired
    @Lazy
    protected BranchCityRepository branchCityRepository;

    protected Long getCurrentCompanyId() {
        return SecurityUtils.getCurrentCompanyId();
    }

    protected String getCurrentCompanyTin() {
        return companyRepository.findById(getCurrentCompanyId())
                .map(Company::getInn)
                .orElse(null);
    }

    protected Long getAdminCompanyId() {
        return companyRepository.findOneByAdminIsTrue()
                .map(Company::getId)
                .orElse(null);
    }

    protected Long getMainBranchIdForAdminCompany() {
        return branchRepository.findFirstByCompanyIdAndMainBranchIsTrue(getAdminCompanyId())
                .getId();
    }

    protected Long getCurrentBranchId() {
        final String headerBranchId = getCurrentBranchIdByHeader();
        return StringUtils.isNotEmpty(headerBranchId) ? Long.valueOf(headerBranchId) : SecurityUtils.getCurrentBranchId();
    }

    protected Set<Long> getBranchesByGroup(final Set<Long> branchGroupIds, Set<Long> branchIds) {
        if (CollectionUtils.isEmpty(branchGroupIds) && CollectionUtils.isEmpty(branchIds)) return null;
        if (CollectionUtils.isEmpty(branchIds)) {
            return branchRepository.findIdsByGroups(branchGroupIds);
        }
        if (CollectionUtils.isEmpty(branchGroupIds)) {
            return branchIds;
        }
        Set<Long> branchIdsByGroup = branchRepository.findIdsByGroups(branchGroupIds);
        branchIdsByGroup.addAll(branchIds);
        return branchIdsByGroup;
    }

    protected Long getBranchCityId(Long branchId) {
        Optional<BranchCityHash> optionalBranchCityHash = branchCityRepository.findById(branchId);
        if (optionalBranchCityHash.isPresent()) {
            return optionalBranchCityHash.get().getCityId();
        } else {
            Long cityId = branchRepository.findById(branchId)
                    .map(Branch::getCityKeyId)
                    .orElseThrow(notFound(localizationService.getMessage("branch")));
            branchCityRepository.save(new BranchCityHash(branchId, cityId));
            return cityId;
        }
    }

    protected Long getCurrentUserId() {
        return SecurityUtils.getCurrentUserId();
    }

    protected String getCurrentTerminalSnByHeader() {
        return HeaderUtils.getCurrentHeaderByKey(HeaderKeys.TERMINAL_SN);
    }

    protected String getCurrentBranchIdByHeader() {
        return HeaderUtils.getCurrentHeaderByKey(HeaderKeys.BRANCH_ID);
    }

    protected boolean isCashier() {
        return SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.CASHIER);
    }

    protected boolean isOwner() {
        return SecurityUtils.isCurrentUserInRoles(AuthoritiesConstants.OWNER);
    }

    protected boolean isBranchAdmin() {
        return SecurityUtils.isCurrentUserInRoles(AuthoritiesConstants.BRANCH_ADMIN);
    }

    protected boolean isOperator() {
        return SecurityUtils.isCurrentUserInRoles(AuthoritiesConstants.SUPPORT,
                AuthoritiesConstants.SALES_MANAGER,
                AuthoritiesConstants.SUPPORT_MANAGER,
                AuthoritiesConstants.REGION_MANAGER,
                AuthoritiesConstants.SALES_DIRECTOR);
    }

    protected boolean isAdmin() {
        return SecurityUtils.isCurrentUserInRoles(AuthoritiesConstants.SMARTPOS_ADMIN, AuthoritiesConstants.ADMIN);
    }


    protected boolean hasPermissionToAdmin() {
        return SecurityUtils.isCurrentUserInRoles(AuthoritiesConstants.SUPPORT,
                AuthoritiesConstants.SALES_MANAGER,
                AuthoritiesConstants.SUPPORT_MANAGER,
                AuthoritiesConstants.REGION_MANAGER,
                AuthoritiesConstants.SALES_DIRECTOR,
                AuthoritiesConstants.SMARTPOS_ADMIN,
                AuthoritiesConstants.ADMIN);
    }


    protected void checkForAccessToCompany(final Long companyId) {
        if (!(isAdmin() || isOperator() || getCurrentCompanyId().equals(companyId))) {
            throw badRequest("У вас нет доступа к данным этой компании!!!").get();
        }
    }

    protected void clearLoginCaches(String login) {
        Objects.requireNonNull(cacheManager.getCache(CacheConstants.USERS_WITH_AUTHORITY_CACHE)).evict(login);
        Objects.requireNonNull(cacheManager.getCache(CacheConstants.USERS_BY_LOGIN_CACHE)).evict(login);
    }

    public void clearCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }

    protected boolean isAdminCompany(Company company) {
        return company.isAdmin();
    }

    protected String getOfdToken() {
        return ofdClient.getToken(applicationProperties.getOfd().getUsername(), applicationProperties.getOfd().getPassword());
    }
}
