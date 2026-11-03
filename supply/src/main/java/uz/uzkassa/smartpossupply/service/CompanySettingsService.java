package uz.uzkassa.smartpossupply.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.domain.OneCIntegration;
import uz.uzkassa.smartpossupply.domain.billing.UsagePlan;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.settings.CompanySettings;
import uz.uzkassa.smartpossupply.domain.settings.GenericSettings;
import uz.uzkassa.smartpossupply.dto.genericsettings.CompanySettingsCreateDTO;
import uz.uzkassa.smartpossupply.dto.genericsettings.CompanySettingsListDTO;
import uz.uzkassa.smartpossupply.dto.genericsettings.GenericSettingsFieldDTO;
import uz.uzkassa.smartpossupply.dto.genericsettings.GenericSettingsFieldListDTO;
import uz.uzkassa.smartpossupply.dto.genericsettings.MobileCompanySettingsListDTO;
import uz.uzkassa.smartpossupply.dto.genericsettings.MobileGenericSettingsFieldDTO;
import uz.uzkassa.smartpossupply.dto.payload.CompanySettingsSyncPayload;
import uz.uzkassa.smartpossupply.dto.xtrace.XtraceTokenDTO;
import uz.uzkassa.smartpossupply.enums.CompanyStatus;
import uz.uzkassa.smartpossupply.enums.CountableServiceTypes;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.GenericSettingsFieldType;
import uz.uzkassa.smartpossupply.enums.GenericSettingsType;
import uz.uzkassa.smartpossupply.enums.MarketplaceSettingsStep;
import uz.uzkassa.smartpossupply.enums.ProductGroup;
import uz.uzkassa.smartpossupply.enums.ValueType;
import uz.uzkassa.smartpossupply.enums.WidgetType;
import uz.uzkassa.smartpossupply.filters.CompanyFilter;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqCompanySettingsSyncProducer;
import uz.uzkassa.smartpossupply.repository.GenericSettingsRepository;
import uz.uzkassa.smartpossupply.repository.OneCIntegrationRepository;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static lombok.AccessLevel.PRIVATE;
import static uz.uzkassa.smartpossupply.enums.GenericSettingsFieldType.ENABLE_DISABLE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 07.02.2024
 */
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class CompanySettingsService extends BaseService {

    GenericSettingsRepository genericSettingsRepository;
    OneCIntegrationRepository oneCIntegrationRepository;
    CompanyService companyService;
    RabbitMqCompanySettingsSyncProducer rabbitMqCompanySettingsSyncProducer;
    CacheService cacheService;
    EntityManager entityManager;

    @Transactional
    public void create(CompanySettingsCreateDTO settingsCreateDTO) {
        List<CompanySettings> settingsList = new ArrayList<>();
        settingsCreateDTO
                .getCompanySettingsList().forEach(companySettingsDTO -> {
                    CompanySettings companySettings = companySettingsRepository.findFirstByCompanyIdAndSettingsId(settingsCreateDTO.getCompanyId(), companySettingsDTO.getSettingsId())
                            .orElseGet(CompanySettings::new);
                    if (companySettings.getId() == null) {
                        Company company = companyRepository.findById(settingsCreateDTO.getCompanyId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", settingsCreateDTO.getCompanyId()));
                        companySettings.setCompanyId(company.getId());
                        companySettings.setCompany(company);
                        companySettings.setSettingsId(companySettingsDTO.getSettingsId());
                    }
                    companySettings.setEnabled(companySettingsDTO.isEnabled());
                    companySettings.getValues().clear();
                    companySettingsDTO.getValues().forEach(companySettings::addValue);
                    settingsList.add(companySettings);
                    if (GenericSettingsType.XTRACE_ENABLED.getCode().equals(companySettingsDTO.getSettingsType())
                            && companySettingsDTO.isEnabled()
                            && companySettingsDTO.getValues().get(GenericSettingsFieldType.XTRACE_TOKEN.getCode()) instanceof String xTraceToken
                            && StringUtils.isNotBlank(xTraceToken)) {
                        xtraceTokenService.create(new XtraceTokenDTO(xTraceToken, settingsCreateDTO.getCompanyId()));
                    } else {
                        xtraceTokenService.deleteXTraceToken(settingsCreateDTO.getCompanyId());
                    }
                    if (GenericSettingsType.WORK_WITH_MARKS.getCode().equals(companySettingsDTO.getSettingsType()) &&
                            companySettingsDTO.getValues().containsKey(GenericSettingsFieldType.PRODUCT_GROUP.getCode())) {
                        if (companySettingsDTO.isEnabled() && companySettingsDTO.getValues().get(GenericSettingsFieldType.PRODUCT_GROUP.getCode()) != null) {
                            updateProductGroup(settingsCreateDTO.getCompanyId(), ProductGroup.valueOf((String) companySettingsDTO.getValues().get(GenericSettingsFieldType.PRODUCT_GROUP.getCode())));
                        }
                        if (!companySettings.isEnabled() && companySettingsDTO.getValues().get(GenericSettingsFieldType.PRODUCT_GROUP.getCode()) == null) {
                            updateProductGroup(settingsCreateDTO.getCompanyId(), null);
                        }
                    }
                });
        if (!CollectionUtils.isEmpty(settingsList)) {
            companySettingsRepository.saveAll(settingsList);
            cacheService.clearByEntityType(EntityTypeEnum.COMPANY_SETTINGS);
        }
    }

    public List<CompanySettingsListDTO> getList() {
        return this.getList(SecurityUtils.getCurrentCompanyId());
    }

    public List<CompanySettingsListDTO> getList(Long companyId) {
        return this.getList(companyId, true);
    }

    public List<CompanySettingsListDTO> getList(Long companyId, boolean isCabinet) {
        return genericSettingsRepository.findAllByForCabinetOrderByOrderNumberAscIdAsc(isCabinet)
                .stream()
                .map(genericSettings -> {
                    CompanySettingsListDTO companySettingsListDTO = new CompanySettingsListDTO();
                    CompanySettings companySettings = companySettingsRepository.findFirstByCompanyIdAndSettingsId(companyId, genericSettings.getId()).orElseGet(CompanySettings::new);
                    companySettingsListDTO.setId(companySettings.getId());
                    companySettingsListDTO.setSettingsId(genericSettings.getId());
                    companySettingsListDTO.setSettingsType(genericSettings.getSettingsType());
                    companySettingsListDTO.setReadOnly(genericSettings.isReadOnly());
                    companySettingsListDTO.setOrderNumber(genericSettings.getOrderNumber());
                    if (genericSettings.getSettingsGroup() != null) {
                        companySettingsListDTO.setGroupId(genericSettings.getSettingsGroup().getId());
                        companySettingsListDTO.setGroupName(genericSettings.getSettingsGroup().getName());
                        companySettingsListDTO.setGroupOrderNumber(genericSettings.getSettingsGroup().getOrderNumber());
                    }
                    if (genericSettings.getDescription() != null) {
                        companySettingsListDTO.setDescription(genericSettings.getDescription(SecurityUtils.getCurrentRequestLanguageEnum()));
                    }

                    /**
                     * This settings belong to billing, if CountableServiceTypes.SERVICES is enabled in usage_plan table it should be enabled, optionally user can switch off.
                     */

                    if (GenericSettingsType.WORK_WITH_MARKS.equals(genericSettings.getSettingsType())) {
                        boolean isEnabled = false;
                        if (companySettings.getId() != null && companySettings.getValues().get(ENABLE_DISABLE) instanceof Boolean) {
                            isEnabled = (Boolean) companySettings.getValues().get(ENABLE_DISABLE);
                        }
                        if (isEnabled) {
                            UsagePlan usagePlan = usagePlanRepository.findFirstByCompanyIdAndServiceType(companyId, CountableServiceTypes.SERVICES.name()).orElse(null);
                            companySettingsListDTO.setEnabled((companySettings.getCompany() != null && companySettings.getCompany().isFreeTrial()) || (usagePlan != null && usagePlan.getEndDate() != null && LocalDateTime.now().isBefore(usagePlan.getEndDate())));
                        }
                    } else if (GenericSettingsType.WORK_WITH_AGENT.equals(genericSettings.getSettingsType())) {
                        boolean isEnabled = false;
                        if (companySettings.getId() != null && companySettings.getValues().get(ENABLE_DISABLE) instanceof Boolean) {
                            isEnabled = (Boolean) companySettings.getValues().get(ENABLE_DISABLE);
                        }
                        if (isEnabled) {
                            UsagePlan usagePlan = usagePlanRepository.findFirstByCompanyIdAndServiceType(companyId, CountableServiceTypes.AGENTS.name()).orElse(null);
                            companySettingsListDTO.setEnabled((companySettings.getCompany() != null && companySettings.getCompany().isFreeTrial()) || (usagePlan != null && usagePlan.getEndDate() != null && LocalDateTime.now().isBefore(usagePlan.getEndDate())));
                        }
                    } else if (GenericSettingsType.WORK_WITH_EXPEDITOR.equals(genericSettings.getSettingsType())) {
                        boolean isEnabled = false;
                        if (companySettings.getId() != null && companySettings.getValues().get(ENABLE_DISABLE) instanceof Boolean) {
                            isEnabled = (Boolean) companySettings.getValues().get(ENABLE_DISABLE);
                        }
                        if (isEnabled) {
                            UsagePlan usagePlan = usagePlanRepository.findFirstByCompanyIdAndServiceType(companyId, CountableServiceTypes.EXPEDITOR.name()).orElse(null);
                            companySettingsListDTO.setEnabled((companySettings.getCompany() != null && companySettings.getCompany().isFreeTrial()) || (usagePlan != null && usagePlan.getEndDate() != null && LocalDateTime.now().isBefore(usagePlan.getEndDate())));
                        }
                    } else if (GenericSettingsType.WORK_WITH_CASHIER.equals(genericSettings.getSettingsType())) {
                        boolean isEnabled = false;
                        if (companySettings.getId() != null && companySettings.getValues().get(ENABLE_DISABLE) instanceof Boolean) {
                            isEnabled = (Boolean) companySettings.getValues().get(ENABLE_DISABLE);
                        }
                        if (isEnabled) {
                            UsagePlan usagePlan = usagePlanRepository.findFirstByCompanyIdAndServiceType(companyId, CountableServiceTypes.CASHIER.name()).orElse(null);
                            companySettingsListDTO.setEnabled((companySettings.getCompany() != null && companySettings.getCompany().isFreeTrial()) || (usagePlan != null && usagePlan.getEndDate() != null && LocalDateTime.now().isBefore(usagePlan.getEndDate())));
                        }
                    } else if (GenericSettingsType.IN_MARKETPLACE == genericSettings.getSettingsType()) {
                        boolean isEnabled = false;
                        if (companySettings.getId() != null && companySettings.getValues().get(ENABLE_DISABLE) instanceof Boolean) {
                            isEnabled = (Boolean) companySettings.getValues().get(ENABLE_DISABLE);
                        }
                        if (isEnabled) {
                            UsagePlan usagePlan = usagePlanRepository.findFirstByCompanyIdAndServiceType(companyId, CountableServiceTypes.B2BMARKETPLACE.name()).orElse(null);
                            companySettingsListDTO.setEnabled((companySettings.getCompany() != null && companySettings.getCompany().isFreeTrial()) || (usagePlan != null && usagePlan.getEndDate() != null && LocalDateTime.now().isBefore(usagePlan.getEndDate())));
                        }
                    } else if (GenericSettingsType.ONLINE_KKM.equals(genericSettings.getSettingsType())) {
                        boolean isEnabled = false;
                        if (companySettings.getId() != null && companySettings.getValues().get(ENABLE_DISABLE) instanceof Boolean) {
                            isEnabled = (Boolean) companySettings.getValues().get(ENABLE_DISABLE);
                        }
                        if (isEnabled) {
                            UsagePlan usagePlan = usagePlanRepository.findFirstByCompanyIdAndServiceType(companyId, CountableServiceTypes.ONLINE_KKM.name()).orElse(null);
                            companySettingsListDTO.setEnabled((companySettings.getCompany() != null && companySettings.getCompany().isFreeTrial()) || (usagePlan != null && usagePlan.getEndDate() != null && LocalDateTime.now().isBefore(usagePlan.getEndDate())));
                        }
                    } else if (GenericSettingsType.ONE_C_SYNC_ENABLED.equals(genericSettings.getSettingsType())) {
                        boolean isEnabled = false;
                        if (companySettings.getId() != null && companySettings.getValues().get(ENABLE_DISABLE) instanceof Boolean) {
                            isEnabled = (Boolean) companySettings.getValues().get(ENABLE_DISABLE);
                        }
                        if (isEnabled) {
                            UsagePlan usagePlan = usagePlanRepository.findFirstByCompanyIdAndServiceType(companyId, CountableServiceTypes.INTEGRATION.name()).orElse(null);
                            companySettingsListDTO.setEnabled((companySettings.getCompany() != null && companySettings.getCompany().isFreeTrial()) || (usagePlan != null && usagePlan.getEndDate() != null && LocalDateTime.now().isBefore(usagePlan.getEndDate())));
                        }
                    } else {
                        boolean enabled = companySettings.isEnabled();
                        if (enabled && !CollectionUtils.isEmpty(genericSettings.getAllowedProductGroups()) && getCurrentCompany() != null) {
                            ProductGroup currentCompanyProductGroup = getCurrentCompany().getProductGroup();
                            enabled = genericSettings.getAllowedProductGroups().contains(currentCompanyProductGroup);
                        }
                        companySettingsListDTO.setEnabled(enabled);
                    }
                    if (companySettings.getSettings() != null) {
                        companySettingsListDTO.setAllowedProductGroups(companySettings.getSettings().getAllowedProductGroups());
                    }
                    if (!CollectionUtils.isEmpty(genericSettings.getFields())) {
                        genericSettings.getFields().forEach(genericSettingsFieldDTO -> {
                            GenericSettingsFieldListDTO fieldListDTO = new GenericSettingsFieldListDTO();
                            fieldListDTO.setFieldType(GenericSettingsFieldType.valueOf(genericSettingsFieldDTO.getFieldType()));
                            fieldListDTO.setWidgetType(WidgetType.valueOf(genericSettingsFieldDTO.getWidgetType()));
                            fieldListDTO.setValueType(ValueType.valueOf(genericSettingsFieldDTO.getValueType()));
                            fieldListDTO.setFormat(genericSettingsFieldDTO.getFormat());
                            fieldListDTO.setMaxSize(genericSettingsFieldDTO.getMaxSize());
                            fieldListDTO.setDefaultValue(genericSettingsFieldDTO.getDefaultValue());
                            fieldListDTO.setPlaceholder(genericSettingsFieldDTO.getPlaceholder());

                            setOptionsBetweenDTOS(fieldListDTO, genericSettingsFieldDTO);

                            if (ENABLE_DISABLE.getCode().equals(genericSettingsFieldDTO.getFieldType())) {
                                fieldListDTO.setValue(companySettingsListDTO.isEnabled());
                            } else {
                                fieldListDTO.setValue(companySettings.getId() != null ? companySettings.getValues().get(GenericSettingsFieldType.valueOf(genericSettingsFieldDTO.getFieldType())) : genericSettingsFieldDTO.getDefaultValue());
                            }
                            if (GenericSettingsType.WORK_WITH_MARKS.getCode().equals(genericSettings.getSettingsType().getCode()) &&
                                    GenericSettingsFieldType.PRODUCT_GROUP.getCode().equals(genericSettingsFieldDTO.getFieldType())) {
                                fieldListDTO.setValue(resolveCompanyProductGroup(companySettings, companyId));
                                fieldListDTO.setOptions(ProductGroup.getProductGroupList());
                            }
                            companySettingsListDTO.add(GenericSettingsFieldType.valueOf(genericSettingsFieldDTO.getFieldType()), fieldListDTO);
                        });
                    }
                    return companySettingsListDTO;
                }).toList();
    }

    public List<MobileCompanySettingsListDTO> getListForMobile() {
        return this.getList()
                .stream()
                .map(genericSettings -> {
                    MobileCompanySettingsListDTO companySettingsListDTO = new MobileCompanySettingsListDTO();
                    companySettingsListDTO.setSettingsType(genericSettings.getSettingsType());
                    genericSettings.getValues().forEach((fieldType, genericSettingsFieldListDTO) -> {
                        MobileGenericSettingsFieldDTO fieldListDTO = new MobileGenericSettingsFieldDTO();
                        GenericSettingsFieldDTO fieldDTO = new GenericSettingsFieldDTO();
                        fieldDTO.setFieldType(genericSettingsFieldListDTO.getFieldType().getCode());
                        fieldDTO.setWidgetType(genericSettingsFieldListDTO.getWidgetType().name());
                        fieldDTO.setValueType(genericSettingsFieldListDTO.getValueType().getCode());
                        fieldDTO.setFormat(genericSettingsFieldListDTO.getFormat());
                        fieldDTO.setMaxSize(genericSettingsFieldListDTO.getMaxSize());
                        fieldDTO.setDefaultValue(genericSettingsFieldListDTO.getDefaultValue());
                        fieldListDTO.setField(fieldDTO);

                        setOptionsBetweenDTOS(genericSettingsFieldListDTO, fieldDTO);

                        fieldListDTO.setValue(String.valueOf(genericSettingsFieldListDTO.getValue()));
                        companySettingsListDTO.getProperties().add(fieldListDTO);
                    });
                    companySettingsListDTO.setEnabled(genericSettings.isEnabled());
                    return companySettingsListDTO;
                }).toList();
    }

    public List<CompanySettingsListDTO> getListForAdmin(Long companyId) {
        return genericSettingsRepository.findAllByOrderByOrderNumberAscIdAsc()
                .stream()
                .map(genericSettings -> {
                    CompanySettingsListDTO companySettingsListDTO = new CompanySettingsListDTO();
                    CompanySettings companySettings = companySettingsRepository.findFirstByCompanyIdAndSettingsId(companyId, genericSettings.getId()).orElseGet(CompanySettings::new);
                    companySettingsListDTO.setId(companySettings.getId());
                    companySettingsListDTO.setSettingsId(genericSettings.getId());
                    companySettingsListDTO.setSettingsType(genericSettings.getSettingsType());
                    companySettingsListDTO.setReadOnly(genericSettings.isReadOnly());
                    companySettingsListDTO.setOrderNumber(genericSettings.getOrderNumber());
                    if (genericSettings.getSettingsGroup() != null) {
                        companySettingsListDTO.setGroupId(genericSettings.getSettingsGroup().getId());
                        companySettingsListDTO.setGroupName(genericSettings.getSettingsGroup().getName());
                        companySettingsListDTO.setGroupOrderNumber(genericSettings.getSettingsGroup().getOrderNumber());
                    }
                    if (companySettings.getSettings() != null) {
                        companySettingsListDTO.setAllowedProductGroups(companySettings.getSettings().getAllowedProductGroups());
                    }
                    if (genericSettings.getDescription() != null) {
                        companySettingsListDTO.setDescription(genericSettings.getDescription(SecurityUtils.getCurrentRequestLanguageEnum()));
                    }
                    /**
                     * This settings belong to billing, if CountableServiceTypes.SERVICES is enabled in usage_plan table it should be enabled, optionally user can switch off.
                     */
                    if (GenericSettingsType.WORK_WITH_MARKS.equals(genericSettings.getSettingsType())) {
                        UsagePlan usagePlan = usagePlanRepository.findFirstByCompanyIdAndServiceType(companyId, CountableServiceTypes.SERVICES.name()).orElse(null);
                        companySettingsListDTO.setEnabled((companySettings.getCompany() != null && companySettings.getCompany().isFreeTrial()) || (usagePlan != null && usagePlan.getEndDate() != null && LocalDateTime.now().isBefore(usagePlan.getEndDate())));
                    } else {
                        companySettingsListDTO.setEnabled(companySettings.isEnabled());
                    }
                    if (!CollectionUtils.isEmpty(genericSettings.getFields())) {
                        genericSettings.getFields().forEach(genericSettingsFieldDTO -> {
                            GenericSettingsFieldListDTO fieldListDTO = new GenericSettingsFieldListDTO();
                            fieldListDTO.setFieldType(GenericSettingsFieldType.valueOf(genericSettingsFieldDTO.getFieldType()));
                            fieldListDTO.setWidgetType(WidgetType.valueOf(genericSettingsFieldDTO.getWidgetType()));
                            fieldListDTO.setValueType(ValueType.valueOf(genericSettingsFieldDTO.getValueType()));
                            fieldListDTO.setFormat(genericSettingsFieldDTO.getFormat());
                            fieldListDTO.setDefaultValue(genericSettingsFieldDTO.getDefaultValue());
                            fieldListDTO.setPlaceholder(genericSettingsFieldDTO.getPlaceholder());

                            setOptionsBetweenDTOS(fieldListDTO, genericSettingsFieldDTO);

                            fieldListDTO.setValue(companySettings.getId() != null ? companySettings.getValues().get(GenericSettingsFieldType.valueOf(genericSettingsFieldDTO.getFieldType())) : genericSettingsFieldDTO.getDefaultValue());
                            if (GenericSettingsType.WORK_WITH_MARKS.getCode().equals(genericSettings.getSettingsType().getCode()) &&
                                    GenericSettingsFieldType.PRODUCT_GROUP.getCode().equals(genericSettingsFieldDTO.getFieldType())) {
                                fieldListDTO.setValue(resolveCompanyProductGroup(companySettings, companyId));
                                fieldListDTO.setOptions(ProductGroup.getProductGroupList());
                            }
                            companySettingsListDTO.add(GenericSettingsFieldType.valueOf(genericSettingsFieldDTO.getFieldType()), fieldListDTO);
                        });
                    }
                    return companySettingsListDTO;
                }).toList();
    }

    private ProductGroup resolveCompanyProductGroup(CompanySettings companySettings, Long companyId) {
        Company company = companySettings.getCompany();
        if (company != null) {
            return company.getProductGroup();
        }
        log.warn("CompanySettings with null company encountered while resolving PRODUCT_GROUP. companySettingsId={}, companyId={}, settingsId={}", companySettings.getId(), companyId, companySettings.getSettingsId());
        return companyRepository.findById(companyId)
                .map(Company::getProductGroup)
                .orElse(null);
    }

    public void sync() {
        rabbitMqCompanySettingsSyncProducer.sendToQueue(new CompanySettingsSyncPayload());
    }

    @Transactional
    public void sync(CompanySettingsSyncPayload payload) {
        CompanyFilter filter = new CompanyFilter();
        filter.setStatus(CompanyStatus.ACTIVE.getCode());
        filter.setSize(COMMON_BATCH_SIZE);
        AtomicInteger count = new AtomicInteger();
        companyService.getResultList(filter)
                .getList()
                .forEach(company -> {
                    log.info("===================Company: {} ===================", company.getTin());
                    //one_c syn
                    OneCIntegration oneCIntegration = oneCIntegrationRepository.findByCompanyId(company.getId()).orElse(null);
                    if (oneCIntegration != null) {
                        GenericSettings genericSettings = genericSettingsRepository.findBySettingsType(GenericSettingsType.ONE_C_SYNC_ENABLED).orElse(null);
                        if (genericSettings != null) {
                            log.info("===================SETTINGS: {} ===================", genericSettings.getSettingsType());
                            CompanySettings companySettings = companySettingsRepository.findFirstByCompanyIdAndSettingsId(company.getId(), genericSettings.getId()).orElseGet(CompanySettings::new);
                            companySettings.setCompanyId(company.getId());
                            companySettings.setSettingsId(genericSettings.getId());
                            companySettings.setEnabled(company.isOneCEnabled());
                            companySettings.addValue(GenericSettingsFieldType.URL.getCode(), oneCIntegration.getUrl());
                            companySettings.addValue(GenericSettingsFieldType.LOGIN.getCode(), oneCIntegration.getUsername());
                            companySettings.addValue(GenericSettingsFieldType.LOGIN.getCode(), oneCIntegration.getUsername());
                            companySettings.addValue(GenericSettingsFieldType.PASSWORD.getCode(), oneCIntegration.getPassword());
                            companySettingsRepository.save(companySettings);
                        }
                    }
                    //allow invoice sync
                    GenericSettings genericSettings = genericSettingsRepository.findBySettingsType(GenericSettingsType.ALLOW_INVOICE_SYNC).orElse(null);
                    if (genericSettings != null) {
                        log.info("===================SETTINGS: {} ===================", genericSettings.getSettingsType());
                        CompanySettings companySettings = companySettingsRepository.findFirstByCompanyIdAndSettingsId(company.getId(), genericSettings.getId()).orElseGet(CompanySettings::new);
                        companySettings.setCompanyId(company.getId());
                        companySettings.setSettingsId(genericSettings.getId());
                        companySettings.setEnabled(company.isAllowInvoiceSync());
                        companySettings.addValue(ENABLE_DISABLE.getCode(), company.isAllowInvoiceSync());
                        companySettings.setEnabled(company.isAllowInvoiceSync());
                        companySettingsRepository.save(companySettings);
                    }
                    // has-contract

                    GenericSettings contractGenericSettings = genericSettingsRepository.findBySettingsType(GenericSettingsType.HAS_CONTRACT).orElse(null);
                    if (contractGenericSettings != null) {
                        log.info("===================SETTINGS: {} ===================", contractGenericSettings.getSettingsType());
                        CompanySettings companySettings = companySettingsRepository.findFirstByCompanyIdAndSettingsId(company.getId(), contractGenericSettings.getId()).orElseGet(CompanySettings::new);
                        companySettings.setCompanyId(company.getId());
                        companySettings.setSettingsId(contractGenericSettings.getId());
                        companySettings.setEnabled(company.isAllowInvoiceSync());
                        companySettings.addValue(ENABLE_DISABLE.getCode(), company.isHasContract());
                        companySettingsRepository.save(companySettings);
                    }

                    //IN_MARKETPLACE
                    GenericSettings marketplaceGenericSettings = genericSettingsRepository.findBySettingsType(GenericSettingsType.IN_MARKETPLACE).orElse(null);
                    if (marketplaceGenericSettings != null) {
                        log.info("===================SETTINGS: {} ===================", marketplaceGenericSettings.getSettingsType());
                        CompanySettings companySettings = companySettingsRepository.findFirstByCompanyIdAndSettingsId(company.getId(), marketplaceGenericSettings.getId()).orElseGet(CompanySettings::new);
                        boolean inMarketplace = MarketplaceSettingsStep.COMPLETED == company.getMarketplaceSettingsStep();
                        companySettings.setCompanyId(company.getId());
                        companySettings.setSettingsId(marketplaceGenericSettings.getId());
                        companySettings.addValue(ENABLE_DISABLE.getCode(), inMarketplace);
                        companySettings.setEnabled(inMarketplace);
                        companySettingsRepository.save(companySettings);
                    }

                    //CUSTOMER_LIST_ENABLE_FOR_AGENT
                    GenericSettings customerListGenericSettings = genericSettingsRepository.findBySettingsType(GenericSettingsType.WORK_WITH_AGENT).orElse(null);
                    if (customerListGenericSettings != null) {
                        log.info("===================SETTINGS: {} ===================", customerListGenericSettings.getSettingsType());
                        CompanySettings companySettings = companySettingsRepository.findFirstByCompanyIdAndSettingsId(company.getId(), customerListGenericSettings.getId()).orElseGet(CompanySettings::new);
                        companySettings.setCompanyId(company.getId());
                        companySettings.setSettingsId(customerListGenericSettings.getId());
                        companySettings.addValue(ENABLE_DISABLE.getCode(), false);
                        companySettings.setEnabled(false);
                        companySettingsRepository.save(companySettings);
                    }

                    //WORK_WITH_MARKS
                    GenericSettings markGenericSettings = genericSettingsRepository.findBySettingsType(GenericSettingsType.WORK_WITH_MARKS).orElse(null);
                    if (markGenericSettings != null) {
                        log.info("===================SETTINGS: {} ===================", markGenericSettings.getSettingsType());
                        CompanySettings companySettings = companySettingsRepository.findFirstByCompanyIdAndSettingsId(company.getId(), markGenericSettings.getId()).orElseGet(CompanySettings::new);
                        companySettings.setCompanyId(company.getId());
                        companySettings.setSettingsId(markGenericSettings.getId());
                        companySettings.addValue(ENABLE_DISABLE.getCode(), true);
                        companySettings.setEnabled(true);
                        companySettingsRepository.save(companySettings);
                    }
                    count.getAndIncrement();
                });
        cacheService.clearByEntityType(EntityTypeEnum.COMPANY_SETTINGS);
        log.info("========================== {} COMPANIES UPDATED =======================", count);
    }


    private void updateProductGroup(Long companyId, ProductGroup productGroup) {
        companyService.updateForProductGroup(companyId, productGroup);
    }


    private void setOptionsBetweenDTOS(GenericSettingsFieldListDTO fieldListDTO, GenericSettingsFieldDTO genericSettingsFieldDTO) {
        if (!CollectionUtils.isEmpty(genericSettingsFieldDTO.getOptions())) {
            fieldListDTO.setOptions(genericSettingsFieldDTO.getOptions().stream().map(GenericSettingsFieldType::valueOf)
                    .map(type -> {
                        Map<String, String> map = new HashMap<>();
                        map.put("code", type.name());
                        map.put("name", type.getName());
                        return map;
                    })
                    .toList());
        }
    }

    public boolean is1CSyncEnabled(Long companyId) {
        return companySettingsRepository
                .findFirstByCompanyIdAndSettings_SettingsType(companyId, GenericSettingsType.ONE_C_SYNC_ENABLED)
                .map(companySettings -> {
                    entityManager.refresh(companySettings);
                    boolean isEnabled = false;

                    if (companySettings.getId() != null && companySettings.getValues().get(ENABLE_DISABLE) instanceof Boolean) {
                        isEnabled = (Boolean) companySettings.getValues().get(ENABLE_DISABLE);
                    }

                    if (isEnabled) {
                        UsagePlan usagePlan = usagePlanRepository.findFirstByCompanyIdAndServiceType(companyId, CountableServiceTypes.INTEGRATION.name()).orElse(null);
                        return (companySettings.getCompany() != null && companySettings.getCompany().isFreeTrial())
                                || (usagePlan != null && usagePlan.getEndDate() != null && LocalDateTime.now().isBefore(usagePlan.getEndDate()));
                    }

                    return false;
                })
                .orElse(false);
    }

}