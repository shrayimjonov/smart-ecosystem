package uz.uzkassa.cts.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.RepairTicketSettings;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.repairticket.RepairTicketSettingsCommonDTO;
import uz.uzkassa.cts.dto.repairticket.RepairTicketSettingsDTO;
import uz.uzkassa.cts.dto.repairticket.RepairTicketSettingsDetailDTO;
import uz.uzkassa.cts.dto.repairticket.RepairTicketSettingsListDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.RepairTicketSettingsRepository;
import uz.uzkassa.cts.service.RepairTicketSettingsService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.List;

@Service
@Transactional(readOnly = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RepairTicketSettingsServiceImpl extends BaseServiceImpl<RepairTicketSettings, Long> implements RepairTicketSettingsService {

    RepairTicketSettingsRepository repairTicketSettingsRepository;
    ValidationContext validationContext;

    public RepairTicketSettingsServiceImpl(RepairTicketSettingsRepository repairTicketSettingsRepository, ValidationContext validationContext) {
        super(repairTicketSettingsRepository);
        this.repairTicketSettingsRepository = repairTicketSettingsRepository;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Long create(RepairTicketSettingsDTO repairTicketSettingsDTO) {
        validationContext.validate(repairTicketSettingsDTO);
        RepairTicketSettings repairTicketSettings = new RepairTicketSettings();
        repairTicketSettings.setProductId(repairTicketSettingsDTO.getProductId());
        repairTicketSettings.setTypeId(repairTicketSettingsDTO.getTypeId());
        repairTicketSettings.setCode1c(repairTicketSettingsDTO.getCode1c());
        repairTicketSettings.setPartNumber(repairTicketSettingsDTO.getPartNumber());
        return repairTicketSettingsRepository.save(repairTicketSettings).getId();
    }

    @Override
    public RepairTicketSettingsDetailDTO get(Long id) {
        return repairTicketSettingsRepository.findById(id).map(repairTicketSettings -> {
            RepairTicketSettingsDetailDTO repairTicketSettingsDetailDTO = new RepairTicketSettingsDetailDTO();
            repairTicketSettingsDetailDTO.setId(repairTicketSettings.getId());
            repairTicketSettingsDetailDTO.setProduct(repairTicketSettings.getProduct().toCommonDTO());
            repairTicketSettingsDetailDTO.setType(repairTicketSettings.getType().toCommonDTO());
            repairTicketSettingsDetailDTO.setCode1c(repairTicketSettings.getCode1c());
            repairTicketSettingsDetailDTO.setPartNumber(repairTicketSettings.getPartNumber());
            repairTicketSettingsDetailDTO.setCreator(repairTicketSettings.getCreator().toCommonDTO());
            repairTicketSettingsDetailDTO.setCreatedDate(repairTicketSettings.getCreatedDate());
            return repairTicketSettingsDetailDTO;
        }).orElseThrow(() -> validationService.entityNotFoundExceptionThrow(REPAIR_TICKET).get());
    }

    @Override
    public Page<RepairTicketSettingsListDTO> getList(BaseFilter baseFilter) {
        ResultList<RepairTicketSettings> resultList = repairTicketSettingsRepository.getResultList(baseFilter);
        List<RepairTicketSettingsListDTO> list = resultList.getList()
                .stream()
                .map(repairTicketSettings -> {
                    RepairTicketSettingsListDTO repairTicketSettingsListDTO = new RepairTicketSettingsListDTO();
                    repairTicketSettingsListDTO.setId(repairTicketSettings.getId());
                    repairTicketSettingsListDTO.setProduct(repairTicketSettings.getProduct().toCommonDTO());
                    repairTicketSettingsListDTO.setType(repairTicketSettings.getType().toCommonDTO());
                    repairTicketSettingsListDTO.setCode1c(repairTicketSettings.getCode1c());
                    repairTicketSettingsListDTO.setPartNumber(repairTicketSettings.getPartNumber());
                    repairTicketSettingsListDTO.setCreator(repairTicketSettings.getCreator().toCommonDTO());
                    repairTicketSettingsListDTO.setCreatedDate(repairTicketSettings.getCreatedDate());
                    return repairTicketSettingsListDTO;
                })
                .toList();
        return new PageImpl<>(list, baseFilter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public List<RepairTicketSettingsCommonDTO> lookUp(BaseFilter filter) {
        filter.setStatus(CommonStatus.ACTIVE.getCode());
        return repairTicketSettingsRepository.getResultList(filter)
                .getList()
                .stream()
                .map(repairTicketSettings -> {
                    RepairTicketSettingsCommonDTO repairTicketSettingsCommonDTO = new RepairTicketSettingsCommonDTO();
                    repairTicketSettingsCommonDTO.setId(repairTicketSettings.getId());
                    repairTicketSettingsCommonDTO.setProductId(repairTicketSettings.getProductId());
                    repairTicketSettingsCommonDTO.setName(repairTicketSettings.getProduct().getName());
                    return repairTicketSettingsCommonDTO;
                })
                .toList();
    }
}
