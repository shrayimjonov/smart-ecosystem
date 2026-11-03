package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.domain.settings.GenericSettings;
import uz.uzkassa.smartpossupply.dto.genericsettings.AdminGenericSettingsGroupDTO;
import uz.uzkassa.smartpossupply.dto.genericsettings.AdminGenericSettingsListDTO;
import uz.uzkassa.smartpossupply.dto.genericsettings.GenericSettingsBatchDTO;
import uz.uzkassa.smartpossupply.dto.genericsettings.GenericSettingsFieldDTO;
import uz.uzkassa.smartpossupply.dto.genericsettings.GenericSettingsFieldListDTO;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.GenericSettingsFieldType;
import uz.uzkassa.smartpossupply.enums.GenericSettingsType;
import uz.uzkassa.smartpossupply.enums.ProductGroup;
import uz.uzkassa.smartpossupply.enums.ValueType;
import uz.uzkassa.smartpossupply.enums.WidgetType;
import uz.uzkassa.smartpossupply.repository.GenericSettingsRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 07.02.2024
 */
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class GenericSettingsService extends BaseService {

    GenericSettingsRepository genericSettingsRepository;
    CacheService cacheService;

    /**
     * To add new settings, we should insert into generic_settings table with SQL patch.
     */
    @Transactional
    public void create(GenericSettingsBatchDTO settings) {
        Map<GenericSettingsType, GenericSettings> settingsMap = new HashMap<>();
        List<GenericSettings> settingsList = new ArrayList<>();
        genericSettingsRepository.findAll()
            .forEach(genericSettings -> settingsMap.put(genericSettings.getSettingsType(), genericSettings));
        if (!CollectionUtils.isEmpty(settings.getSettingsList())) {
            settings.getSettingsList()
                .forEach(genericSettingsDTO -> {
                    if (StringUtils.isEmpty(genericSettingsDTO.getSettingsType())) {
                        throw validationService.badRequestExceptionThrow("Settings type is null").get();
                    }
                    GenericSettings genericSettings = genericSettingsRepository.findFirstBySettingsType(GenericSettingsType.valueOf(genericSettingsDTO.getSettingsType()))
                        .orElseGet(GenericSettings::new);
                    genericSettings.setFields(genericSettingsDTO.getFields());
                    genericSettings.setDescription(genericSettingsDTO.getDescription());
                    genericSettings.setReadOnly(genericSettingsDTO.isReadOnly());
                    genericSettings.setForCabinet(genericSettingsDTO.isCabinet());
                    genericSettings.setSettingsType(GenericSettingsType.valueOf(genericSettingsDTO.getSettingsType()));
                    if (!CollectionUtils.isEmpty(genericSettingsDTO.getAllowedProductGroups())) {
                        genericSettings.setAllowedProductGroups(genericSettingsDTO.getAllowedProductGroups().stream().map(ProductGroup::valueOf).toList());
                    }
                    settingsList.add(genericSettings);
                    settingsMap.remove(GenericSettingsType.valueOf(genericSettingsDTO.getSettingsType()));
                });
        }
        if (!CollectionUtils.isEmpty(settingsMap.values())) {
            settingsMap.values().forEach(genericSettings -> genericSettings.setEnabled(false));
            settingsList.addAll(settingsMap.values());
        }
        if (!CollectionUtils.isEmpty(settingsList)) {
            genericSettingsRepository.saveAll(settingsList);
        }
        cacheService.clearByEntityType(EntityTypeEnum.GENERIC_SETTINGS);
    }

    public List<AdminGenericSettingsListDTO> getListForAdmin() {
        return genericSettingsRepository.findAllByOrderByOrderNumberAscIdAsc()
            .stream()
            .map(genericSettings -> {
                AdminGenericSettingsListDTO settings = new AdminGenericSettingsListDTO();
                settings.setId(genericSettings.getId());
                settings.setSettingsType(genericSettings.getSettingsType());
                settings.setReadOnly(genericSettings.isReadOnly());
                settings.setEnabled(genericSettings.isEnabled());
                settings.setOrderNumber(genericSettings.getOrderNumber());
                settings.setAllowedProductGroups(genericSettings.getAllowedProductGroups());
                settings.setDescription(genericSettings.getDescription());

                if (genericSettings.getSettingsGroup() != null) {
                    AdminGenericSettingsGroupDTO settingsGroup = new AdminGenericSettingsGroupDTO();
                    settingsGroup.setName(genericSettings.getSettingsGroup().getName());
                    settingsGroup.setOrder(Long.valueOf(genericSettings.getSettingsGroup().getOrderNumber()));
                    settingsGroup.setId(genericSettings.getSettingsGroupId());
                    settings.setSettingsGroup(settingsGroup);
                }

                settings.setAssigned(genericSettings.getSettingsGroupId() != null);
                if (!CollectionUtils.isEmpty(genericSettings.getFields())) {
                    settings.setFields(setGenericSettingsFields(genericSettings));
                }
                return settings;
            }).toList();
    }

    private List<GenericSettingsFieldListDTO> setGenericSettingsFields(GenericSettings genericSettings) {
        return genericSettings.getFields()
            .stream().map(genericSettingsFieldDTO -> {
                GenericSettingsFieldListDTO fieldListDTO = new GenericSettingsFieldListDTO();
                fieldListDTO.setFieldType(GenericSettingsFieldType.valueOf(genericSettingsFieldDTO.getFieldType()));
                fieldListDTO.setValueType(ValueType.valueOf(genericSettingsFieldDTO.getValueType()));
                fieldListDTO.setWidgetType(WidgetType.valueOf(genericSettingsFieldDTO.getWidgetType()));
                fieldListDTO.setFormat(genericSettingsFieldDTO.getFormat());
                fieldListDTO.setPlaceholder(genericSettingsFieldDTO.getPlaceholder());

                setOptionsBetweenDTOS(fieldListDTO, genericSettingsFieldDTO);

                fieldListDTO.setDefaultValue(genericSettingsFieldDTO.getDefaultValue());
                return fieldListDTO;
            }).toList();
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
}