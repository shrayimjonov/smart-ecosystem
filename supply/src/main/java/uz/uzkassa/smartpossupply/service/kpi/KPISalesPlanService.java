package uz.uzkassa.smartpossupply.service.kpi;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;
import uz.uzkassa.smartpossupply.domain.kpi.KPI;
import uz.uzkassa.smartpossupply.domain.kpi.salesplan.KPISalesPlan;
import uz.uzkassa.smartpossupply.domain.kpi.salesplan.KPISalesPlanItem;
import uz.uzkassa.smartpossupply.dto.kpi.KPISalesPlanDTO;
import uz.uzkassa.smartpossupply.dto.kpi.KPISalesPlanItemDTO;
import uz.uzkassa.smartpossupply.dto.kpi.detail.KPISalesPlanDetailDTO;
import uz.uzkassa.smartpossupply.dto.kpi.detail.KPISalesPlanInfoDetailDTO;
import uz.uzkassa.smartpossupply.enums.KPIStatus;
import uz.uzkassa.smartpossupply.enums.KPIType;
import uz.uzkassa.smartpossupply.enums.Role;
import uz.uzkassa.smartpossupply.repository.UserRepository;
import uz.uzkassa.smartpossupply.repository.kpi.KPISalesPlanRepository;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;
import uz.uzkassa.smartpossupply.web.rest.errors.NotFoundException;

import java.util.Optional;
import java.util.Set;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KPISalesPlanService implements KPIServiceStrategy<KPISalesPlanDTO, KPISalesPlan, KPISalesPlanDetailDTO> {
    KPISalesPlanRepository kpiSalesPlanRepository;
    UserRepository userRepository;

    @Override
    public KPIType getType() {
        return KPIType.SALES_PLAN;
    }

    @Override
    @Transactional
    public Long create(KPISalesPlanDTO kpiSalesPlanDTO) {
        kpiSalesPlanDTO.setCompanyId(SecurityUtils.getCompanyId());

        validateKPI(kpiSalesPlanDTO);

        KPISalesPlan kpiSalesPlan = new KPISalesPlan();
        convertFromKPICreateDTO(kpiSalesPlan, kpiSalesPlanDTO);
        if (kpiSalesPlanDTO.getItems() != null) {
            kpiSalesPlanDTO.getItems().forEach(itemDto -> {
                KPISalesPlanItem item = new KPISalesPlanItem();
                convertFromKPIItemDTO(item, itemDto);
                item.setKpiSalesPlan(kpiSalesPlan);
                kpiSalesPlan.getItems().add(item);
            });
        }
        return kpiSalesPlanRepository.save(kpiSalesPlan).getId();
    }

    @Override
    @Transactional
    public Long update(Long id, KPISalesPlanDTO kpiSalesPlanDTO) {
        KPISalesPlan kpi = kpiSalesPlanRepository.findByIdAndCompanyId(id, SecurityUtils.getCompanyId())
                .orElseThrow(() -> new NotFoundException("KPI not found"));

        kpi.getEmployees().clear();
        kpi.getItems().clear();
        kpiSalesPlanDTO.setCompanyId(kpi.getCompanyId());

        kpiSalesPlanDTO.setId(kpi.getId());
        validateKPI(kpiSalesPlanDTO);

        convertFromKPICreateDTO(kpi, kpiSalesPlanDTO);

        for (KPISalesPlanItemDTO kpiSalesPlanItemDTO : kpiSalesPlanDTO.getItems()) {
            KPISalesPlanItem kpiSalesPlanItem = new KPISalesPlanItem();
            convertFromKPIItemDTO(kpiSalesPlanItem, kpiSalesPlanItemDTO);
            kpi.getItems().add(kpiSalesPlanItem);
            kpiSalesPlanItem.setKpiSalesPlan(kpi);
        }

        return kpiSalesPlanRepository.save(kpi).getId();
    }

    @Override
    public KPISalesPlanDetailDTO toDTO(KPISalesPlan kpiSalesPlan) {
        KPISalesPlanDetailDTO kpiSalesPlanDetailDTO = new KPISalesPlanDetailDTO();
        kpiSalesPlanDetailDTO.setId(kpiSalesPlan.getId());
        kpiSalesPlanDetailDTO.setRole(kpiSalesPlan.getRole());
        kpiSalesPlanDetailDTO.setType(kpiSalesPlan.getType());
        kpiSalesPlanDetailDTO.setName(kpiSalesPlan.getName());
        kpiSalesPlanDetailDTO.setRegion(Optional.ofNullable(kpiSalesPlan.getRegion()).map(SimpleEntity::toCommonDTO).orElse(null));
        kpiSalesPlanDetailDTO.setProductAddType(kpiSalesPlan.getProductAddType());
        kpiSalesPlanDetailDTO.setSalesPlanType(kpiSalesPlan.getSalesPlanType());
        kpiSalesPlanDetailDTO.setBonusType(kpiSalesPlan.getBonusType());
        kpiSalesPlanDetailDTO.setEmployees(kpiSalesPlan.getEmployees().stream().map(SimpleEntity::toCommonDTO).toList());
        kpiSalesPlanDetailDTO.setItems(kpiSalesPlan.getItems().stream().map(item -> {
            KPISalesPlanInfoDetailDTO kpiSalesPlanInfoDetailDTO = new KPISalesPlanInfoDetailDTO();
            kpiSalesPlanInfoDetailDTO.setProduct(Optional.ofNullable(item.getProduct()).map(SimpleEntity::toCommonDTO).orElse(null));
            kpiSalesPlanInfoDetailDTO.setCategory(Optional.ofNullable(item.getCategory()).map(SimpleEntity::toCommonDTO).orElse(null));
            kpiSalesPlanInfoDetailDTO.setSalesPlanValue(item.getSalesPlanValue());
            kpiSalesPlanInfoDetailDTO.setBonusValue(item.getBonusValue());
            return kpiSalesPlanInfoDetailDTO;
        }).toList());
        return kpiSalesPlanDetailDTO;
    }

    @Override
    public void validationUpdateStatus(KPI kpi, KPIStatus status) {
    }


    private void validateKPI(KPISalesPlanDTO kpiSalesPlanDTO) {
        if (kpiSalesPlanRepository.existByFields(kpiSalesPlanDTO)) {
            throw new BadRequestException("KPI already exists");
        }
    }

    private void convertFromKPICreateDTO(KPISalesPlan kpiSalesPlan, KPISalesPlanDTO kpiSalesPlanDTO) {
        kpiSalesPlan.setRole(kpiSalesPlanDTO.getRole());
        kpiSalesPlan.setType(kpiSalesPlanDTO.getType());
        kpiSalesPlan.setName(kpiSalesPlanDTO.getName());
        kpiSalesPlan.setRegionId(kpiSalesPlanDTO.getRegionId());
        kpiSalesPlan.setProductAddType(kpiSalesPlanDTO.getProductAddType());
        kpiSalesPlan.setSalesPlanType(kpiSalesPlanDTO.getSalesPlanType());
        kpiSalesPlan.setBonusType(kpiSalesPlanDTO.getBonusType());
        kpiSalesPlan.setCompanyId(kpiSalesPlanDTO.getCompanyId());
        kpiSalesPlan.setEmployees(getEmployees(kpiSalesPlanDTO.getRole(), kpiSalesPlanDTO.getEmployeeIds()));
    }

    private void convertFromKPIItemDTO(KPISalesPlanItem kpiSalesPlanItem, KPISalesPlanItemDTO kpiSalesPlanItemDTO) {
        kpiSalesPlanItem.setProductId(kpiSalesPlanItemDTO.getProductId());
        kpiSalesPlanItem.setCategoryId(kpiSalesPlanItemDTO.getCategoryId());
        kpiSalesPlanItem.setUnit(kpiSalesPlanItem.getUnit());
        kpiSalesPlanItem.setSalesPlanValue(kpiSalesPlanItemDTO.getSalesPlanValue());
        kpiSalesPlanItem.setBonusValue(kpiSalesPlanItemDTO.getBonusValue());
    }

    private Set<User> getEmployees(Role role, Set<Long> employeeIds) {
        return userRepository.findAllByDeletedIsFalseAndRoleAndIdIn(role, employeeIds);
    }
}

