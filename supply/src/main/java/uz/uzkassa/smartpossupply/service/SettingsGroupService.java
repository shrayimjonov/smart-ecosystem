package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.settings.GenericSettings;
import uz.uzkassa.smartpossupply.domain.settings.SettingsGroup;
import uz.uzkassa.smartpossupply.dto.genericsettings.SettingsGroupAssignDTO;
import uz.uzkassa.smartpossupply.dto.genericsettings.SettingsGroupDTO;
import uz.uzkassa.smartpossupply.dto.genericsettings.SettingsGroupLookUpDTO;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.repository.GenericSettingsRepository;
import uz.uzkassa.smartpossupply.repository.SettingsGroupRepository;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class SettingsGroupService extends BaseService {

    SettingsGroupRepository settingsGroupRepository;
    GenericSettingsRepository genericSettingsRepository;
    CacheService cacheService;

    @Transactional
    public Long create(SettingsGroupDTO settingsGroupDTO) {
        Integer orderNumber = settingsGroupDTO.getOrderNumber();
        settingsGroupRepository.shiftOrderNumbersUp(orderNumber);

        SettingsGroup settingsGroup = new SettingsGroup();
        settingsGroup.setNameUz(settingsGroupDTO.getNameUz());
        settingsGroup.setNameCyrillic(settingsGroupDTO.getNameCyrillic());
        settingsGroup.setNameRu(settingsGroupDTO.getNameRu());
        settingsGroup.setOrderNumber(orderNumber);
        return settingsGroupRepository.save(settingsGroup).getId();
    }

    @Transactional
    public Long update(Long groupId, SettingsGroupDTO settingsGroupDTO) {
        SettingsGroup settingsGroup = settingsGroupRepository.findById(groupId).orElseThrow(validationService.entityNotFoundExceptionThrow(SettingsGroup.class.getSimpleName(), "id", groupId));

        Integer oldOrderNumber = settingsGroup.getOrderNumber();
        Integer newOrderNumber = settingsGroupDTO.getOrderNumber();

        if (!oldOrderNumber.equals(newOrderNumber)) {
            if (newOrderNumber > oldOrderNumber) {
                settingsGroupRepository.shiftOrderNumbersDownInRange(groupId, oldOrderNumber, newOrderNumber);
            } else {
                settingsGroupRepository.shiftOrderNumbersUpInRange(groupId, newOrderNumber, oldOrderNumber);
            }
        }

        settingsGroup.setNameUz(settingsGroupDTO.getNameUz());
        settingsGroup.setNameCyrillic(settingsGroupDTO.getNameCyrillic());
        settingsGroup.setNameRu(settingsGroupDTO.getNameRu());
        settingsGroup.setOrderNumber(newOrderNumber);
        return settingsGroupRepository.save(settingsGroup).getId();
    }


    @Transactional
    public void assign(SettingsGroupAssignDTO settingsGroupAssignDTO) {
        GenericSettings genericSettings = genericSettingsRepository.findById(settingsGroupAssignDTO.getSettingsId())
                .orElseThrow(validationService.entityNotFoundExceptionThrow(GenericSettings.class.getSimpleName(), "id", settingsGroupAssignDTO.getSettingsId()));
        genericSettings.setSettingsGroupId(settingsGroupAssignDTO.getSettingsGroupId());
        genericSettingsRepository.save(genericSettings);
        cacheService.clearByEntityType(EntityTypeEnum.GENERIC_SETTINGS);
    }

    public List<SettingsGroupLookUpDTO> lookUp() {
        return settingsGroupRepository.findAllByOrderByOrderNumberAscIdAsc()
                .stream()
                .map(settingsGroup -> {
                    SettingsGroupLookUpDTO settingsGroupLookUpDTO = new SettingsGroupLookUpDTO();
                    settingsGroupLookUpDTO.setId(settingsGroup.getId());
                    settingsGroupLookUpDTO.setName(settingsGroup.getName());
                    settingsGroupLookUpDTO.setOrderNumber(settingsGroup.getOrderNumber());
                    return settingsGroupLookUpDTO;
                })
                .toList();
    }

}
