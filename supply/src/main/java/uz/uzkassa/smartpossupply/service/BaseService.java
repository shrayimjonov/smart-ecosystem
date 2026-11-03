package uz.uzkassa.smartpossupply.service;

import uz.uzkassa.smartpossupply.constants.MessageConstants;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.config.ApplicationProperties;
import uz.uzkassa.smartpossupply.constants.CacheConstants;
import uz.uzkassa.smartpossupply.constants.Constants;
import uz.uzkassa.smartpossupply.domain.Unit;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.company.CompanyAggregationInfo;
import uz.uzkassa.smartpossupply.dto.xtrace.XtraceTokenDetailDTO;
import uz.uzkassa.smartpossupply.enums.BranchType;
import uz.uzkassa.smartpossupply.enums.Role;
import uz.uzkassa.smartpossupply.mappers.AddressMapper;
import uz.uzkassa.smartpossupply.repository.BranchRepository;
import uz.uzkassa.smartpossupply.repository.CompanyAggregationInfoRepository;
import uz.uzkassa.smartpossupply.repository.CompanyRepository;
import uz.uzkassa.smartpossupply.repository.CompanySettingsRepository;
import uz.uzkassa.smartpossupply.repository.UnitRepository;
import uz.uzkassa.smartpossupply.repository.UsagePlanRepository;
import uz.uzkassa.smartpossupply.repository.UserRepository;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.text.DecimalFormat;
import java.time.LocalDateTime;

@Component
@Slf4j
@Transactional(readOnly = true)
public abstract class BaseService implements Constants, CacheConstants {

    @Autowired
    protected LocalizationService localizationService;

    @Autowired
    protected ValidationService validationService;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected CompanyRepository companyRepository;

    @Autowired
    protected BranchRepository branchRepository;

    @Autowired
    protected UnitRepository unitRepository;

    @Autowired
    protected UsagePlanRepository usagePlanRepository;

    @Autowired
    protected CompanyAggregationInfoRepository companyAggregationInfoRepository;

    @Autowired
    protected ApplicationProperties applicationProperties;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected AddressMapper addressMapper;

    @Autowired
    protected CompanySettingsRepository companySettingsRepository;

    @Autowired
    protected XtraceTokenService xtraceTokenService;

    protected static final DecimalFormat AGGREGATION_FORMAT = new DecimalFormat("0000000");

    protected static Long getCurrentCompanyId() {
        return SecurityUtils.getCurrentCompanyId();
    }

    protected static Long getCurrentUserId() {
        return SecurityUtils.getCurrentUserId();
    }

    public Company getCurrentCompanyFromDB() {
        return companyRepository
            .findById(getCurrentCompanyId())
            .orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", getCurrentCompanyId()));
    }

    public Company getCurrentCompany() {
        String tin = getCurrentCompanyTin();
        if (tin == null) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.COMPANY_NOT_REGISTERED)).get();
        }
        return companyRepository
            .findByTinAndDeletedIsFalse(tin).orElse(companyRepository.findFirstByTinAndDeletedIsFalse(tin).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "tin", tin)));
    }

    public Long getCurrentMainBranchId() {
        return branchRepository
            .getMainBranchId(getCurrentCompanyId());
    }

    protected static String getCurrentCompanyTin() {
        return SecurityUtils.getCurrentCompanyTin();
    }

    protected static String getCurrentUserLogin() {
        return SecurityUtils.getCurrentUserLogin().orElse(null);
    }

    public User getCurrentUser() {
        String login = SecurityUtils.getCurrentUserLogin().orElse(null);
        if (login == null) {
            throw validationService.badRequestExceptionThrow("Пользователь не найден").get();
        }
        return userRepository
            .findFirstByLoginAndDeletedIsFalse(login)
            .orElseThrow(validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "login", login));
    }

    public Unit getUnit() {
        return unitRepository.findFirstByCode(Unit.PC);
    }

    public boolean isFromAdmin() {
        return SecurityUtils.isFromAdmin();
    }

    public void deleteById(Long id) {

    }

    protected void validateCompany(Long companyId) {
        if (!isFromAdmin() && !companyId.equals(getCurrentCompanyId())) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ACCESS_DENIED), localizationService.localize(MessageConstants.CANNOT_UPDATE_OTHER_COMPANY_DATA)).get();
        }
    }

    public Long getWebKassaBranchId() {
        return branchRepository.getBranchIdByType(getCurrentCompanyId(), BranchType.WEB_KASSA);
    }

    protected boolean isExpeditor() {
        return SecurityUtils.isCurrentUserInRole(Role.ROLE_EXPEDITOR.getCode());
    }

    protected boolean isCashier() {
        return SecurityUtils.isCurrentUserInRole(Role.ROLE_CASHIER.getCode());
    }

    public CompanyAggregationInfo validateTuronAndGetAggregationInfo() {
        CompanyAggregationInfo companyAggregationInfo = this.getCompanyAggregationInfo();
        if (StringUtils.isAnyEmpty(companyAggregationInfo.getOmsId(), companyAggregationInfo.getTuronToken())) {
            throw validationService.badRequestExceptionThrow("В данной организации отсутствует OMS_ID или TOKEN").get();
        }
        return companyAggregationInfo;
    }

    public CompanyAggregationInfo validateAslBelgiAndGetAggregationInfo() {
        return this.getCompanyAggregationInfo();
    }

    public CompanyAggregationInfo getCompanyAggregationInfo() {
        return companyAggregationInfoRepository.findByCompanyId(getCurrentCompanyId()).map(companyAggregationInfo -> {
//            if (StringUtils.isEmpty(companyAggregationInfo.getAslBelgiXtraceToken()) || (companyAggregationInfo.getAslBelgiXtraceTokenExpireDate() != null && companyAggregationInfo.getAslBelgiXtraceTokenExpireDate().plusHours(8L).isBefore(LocalDateTime.now()))) {
//                throw badRequestExceptionThrow(localizationService.localize(MessageConstants.ERROR), localizationService.localize(MessageConstants.ASL_BELGI_TOKEN_EXPIRED_ERROR)).get();
//            }
            return companyAggregationInfo;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(CompanyAggregationInfo.class.getSimpleName(), "companyId", getCurrentCompanyId()));
    }

    public XtraceTokenDetailDTO getXTraceInfo() {
        XtraceTokenDetailDTO xtraceTokenDetailDTO = xtraceTokenService.get();
        if (xtraceTokenDetailDTO == null || StringUtils.isEmpty(xtraceTokenDetailDTO.getApiKey()) || (xtraceTokenDetailDTO.getExpireDate() != null && xtraceTokenDetailDTO.getExpireDate().isBefore(LocalDateTime.now()))) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ERROR), localizationService.localize("asl.belgi.xtrace.token.Íexpired.error")).get();
        }
        return xtraceTokenDetailDTO;
    }
}
