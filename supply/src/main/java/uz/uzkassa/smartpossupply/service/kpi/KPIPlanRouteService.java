package uz.uzkassa.smartpossupply.service.kpi;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;
import uz.uzkassa.smartpossupply.domain.kpi.KPI;
import uz.uzkassa.smartpossupply.domain.kpi.client.KPIItem;
import uz.uzkassa.smartpossupply.domain.kpi.planroute.KPIPlanRoute;
import uz.uzkassa.smartpossupply.domain.kpi.planroute.KPIPlanRouteBonus;
import uz.uzkassa.smartpossupply.domain.planroute.PlanRoute;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.kpi.KPICalculateTotalDTO;
import uz.uzkassa.smartpossupply.dto.kpi.KPIPlanRouteCalculateDTO;
import uz.uzkassa.smartpossupply.dto.kpi.KPIPlanRouteDTO;
import uz.uzkassa.smartpossupply.dto.kpi.PlanRouteBonusDTO;
import uz.uzkassa.smartpossupply.dto.kpi.detail.KPIPlanRouteDetailDTO;
import uz.uzkassa.smartpossupply.dto.kpi.detail.KPIPlanRouteItemDetailDTO;
import uz.uzkassa.smartpossupply.enums.KPIStatus;
import uz.uzkassa.smartpossupply.enums.KPIType;
import uz.uzkassa.smartpossupply.filters.KPIFilter;
import uz.uzkassa.smartpossupply.repository.PlanRouteCustomerBranchRepository;
import uz.uzkassa.smartpossupply.repository.kpi.KPIPlanRouteRepository;
import uz.uzkassa.smartpossupply.service.kpi.calculate.KPICalculateStrategy;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.AlreadyExistException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import uz.uzkassa.smartpossupply.service.ValidationService;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KPIPlanRouteService implements KPIServiceStrategy<KPIPlanRouteDTO, KPIPlanRoute, KPIPlanRouteDetailDTO>, KPICalculateStrategy<KPIFilter, KPIPlanRouteCalculateDTO, KPICalculateTotalDTO> {

    KPIPlanRouteRepository kpiPlanRouteRepository;
    PlanRouteCustomerBranchRepository planRouteCustomerBranchRepository;
    ValidationService validationService;

    @Override
    public KPIType getType() {
        return KPIType.PLAN_ROUTE;
    }

    @Override
    @Transactional
    public Long create(KPIPlanRouteDTO kpiPlanRouteDTO) {
        kpiPlanRouteDTO.setCompanyId(SecurityUtils.getCompanyId());
//        if (kpiPlanRouteRepository.existsByRoleAndTypeAndCompanyIdAndDeletedIsFalse(kpiPlanRouteDTO.getRole(), kpiPlanRouteDTO.getType(), kpiPlanRouteDTO.getCompanyId())) {
//            throw new BadRequestException("KPI already exists");
//        }
        KPIPlanRoute kpiPlanRoute = new KPIPlanRoute();
        convertFromDTO(kpiPlanRoute, kpiPlanRouteDTO);
        return kpiPlanRouteRepository.save(kpiPlanRoute).getId();
    }

    @Override
    @Transactional
    public Long update(Long id, KPIPlanRouteDTO kpiPlanRouteDTO) {
        kpiPlanRouteDTO.setCompanyId(SecurityUtils.getCompanyId());
        KPIPlanRoute kpiPlanRoute = kpiPlanRouteRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(KPI.class.getSimpleName(), "id", id));
        kpiPlanRoute.getBonuses().clear();
        kpiPlanRoute.getKpiItems().clear();
//        if (kpiPlanRouteRepository.existsByIdNotAndRoleAndTypeAndCompanyIdAndDeletedIsFalse(id, kpiPlanRouteDTO.getRole(), kpiPlanRouteDTO.getType(), kpiPlanRoute.getCompanyId())) {
//            throw new BadRequestException("KPI already exists");
//        }
        convertFromDTO(kpiPlanRoute, kpiPlanRouteDTO);
        return kpiPlanRouteRepository.save(kpiPlanRoute).getId();
    }

    @Override
    public KPIPlanRouteDetailDTO toDTO(KPIPlanRoute kpiPlanRoute) {
        KPIPlanRouteDetailDTO kpiPlanRouteDetailDTO = new KPIPlanRouteDetailDTO();
        kpiPlanRouteDetailDTO.setId(kpiPlanRoute.getId());
        kpiPlanRouteDetailDTO.setRole(kpiPlanRoute.getRole());
        kpiPlanRouteDetailDTO.setType(kpiPlanRoute.getType());
        kpiPlanRouteDetailDTO.setName(kpiPlanRoute.getName());
        kpiPlanRouteDetailDTO.setRegion(Optional.ofNullable(kpiPlanRoute.getRegion())
                .map(SimpleEntity::toCommonDTO)
                .orElse(null));
        kpiPlanRouteDetailDTO.setBonuses(kpiPlanRoute.getBonuses().stream().map(bonus -> {
            PlanRouteBonusDTO bonusDTO = new PlanRouteBonusDTO();
            bonusDTO.setAmount(bonus.getAmount());
            bonusDTO.setPercentage(bonus.getPercentage());
            return bonusDTO;
        }).toList());
        kpiPlanRouteDetailDTO.setItems(kpiPlanRoute.getKpiItems().stream().map(kpiItem -> {
            KPIPlanRouteItemDetailDTO kpiPlanRouteItemDetailDTO = new KPIPlanRouteItemDetailDTO();
            kpiPlanRouteItemDetailDTO.setId(kpiItem.getPlanRoute().getId());
            kpiPlanRouteItemDetailDTO.setName(kpiItem.getPlanRoute().getName());
            kpiPlanRouteItemDetailDTO.setRegion(Optional.ofNullable(kpiItem.getPlanRoute())
                    .map(PlanRoute::getRegion)
                    .map(SimpleEntity::toCommonDTO)
                    .orElse(null));
            kpiPlanRouteItemDetailDTO.setEmployee(Optional.ofNullable(kpiItem.getPlanRoute().getAgent())
                    .map(SimpleEntity::toCommonDTO)
                    .orElse(null));
            return kpiPlanRouteItemDetailDTO;
        }).toList());
        return kpiPlanRouteDetailDTO;
    }

    @Override
    public void validationUpdateStatus(KPI kpi, KPIStatus status) {
        if (kpiPlanRouteRepository.updateStatus(kpi, status)) {
            throw new AlreadyExistException("KPI with active status exists", "У вас уже есть KPI со статусом «Активен»");
        }
    }

    @Override
    public ResultList<KPIPlanRouteCalculateDTO> calculate(KPIFilter filter) {
        ResultList<KPIPlanRouteCalculateDTO> resultList = planRouteCustomerBranchRepository.calculatePlanRouteCount(filter);
        Map<Long, KPIPlanRoute> kpiPlanRouteMap = new HashMap<>();
        kpiPlanRouteRepository.findKPIPlanRouteByStatusAndCompanyIdAndDeletedIsFalse(KPIStatus.ACTIVE, filter.getCompanyId())
                .forEach(kpiPlanRoute -> {
                    if (!kpiPlanRoute.getEmployees().isEmpty()) {
                        kpiPlanRoute.getEmployees().forEach(employee -> kpiPlanRouteMap.put(employee.getCompanyId(), kpiPlanRoute));
                    } else {
                        kpiPlanRouteMap.put(0L, kpiPlanRoute);
                    }
                });
        resultList.getList()
                .stream()
                .filter(kpiCalculateDTO -> kpiCalculateDTO.getKpiPercentage().compareTo(BigDecimal.ZERO) > 0)
                .forEach(kpiCalculateDTO -> calculateKpiForSpecificalEmployees(kpiPlanRouteMap, kpiCalculateDTO));
        return resultList;
    }

    @Override
    public KPICalculateTotalDTO calculateTotal(KPIFilter filter) {
        kpiPlanRouteRepository.findKPIPlanRouteByStatusAndCompanyIdAndDeletedIsFalse(KPIStatus.ACTIVE, filter.getCompanyId());
        return planRouteCustomerBranchRepository.calculatePlanRouteTotal(filter);
    }

    private void convertFromDTO(KPIPlanRoute kpiPlanRoute, KPIPlanRouteDTO kpiPlanRouteDTO) {
        kpiPlanRoute.setCompanyId(kpiPlanRouteDTO.getCompanyId());
        kpiPlanRoute.setRole(kpiPlanRouteDTO.getRole());
        kpiPlanRoute.setType(kpiPlanRouteDTO.getType());
        kpiPlanRoute.setRegionId(kpiPlanRouteDTO.getRegionId());
        kpiPlanRoute.setName(kpiPlanRouteDTO.getName());
        kpiPlanRouteDTO.getBonuses().forEach(planRouteBonusDTO -> {
            KPIPlanRouteBonus bonus = new KPIPlanRouteBonus();
            bonus.setPercentage(planRouteBonusDTO.getPercentage());
            bonus.setAmount(planRouteBonusDTO.getAmount());
            bonus.setKpiPlanRoute(kpiPlanRoute);
            kpiPlanRoute.getBonuses().add(bonus);
        });

        kpiPlanRouteDTO.getPlanRouteIds().forEach(planRouteId -> {
            KPIItem kpiItem = new KPIItem();
            kpiItem.setKpi(kpiPlanRoute);
            kpiItem.setPlanRouteId(planRouteId);
            kpiPlanRoute.getKpiItems().add(kpiItem);
        });
    }

    private void calculateKpiForSpecificalEmployees(Map<Long, KPIPlanRoute> kpiPlanRouteMap, KPIPlanRouteCalculateDTO kpiPlanRouteCalculateDTO) {
        if (kpiPlanRouteMap.containsKey(kpiPlanRouteCalculateDTO.getEmployeeId())) {
            KPIPlanRoute kpiPlanRoute = kpiPlanRouteMap.get(kpiPlanRouteCalculateDTO.getEmployeeId());
            kpiPlanRoute.getBonuses().forEach(kpiPlanRouteBonus -> {
                if (kpiPlanRouteCalculateDTO.getKpiPercentage().compareTo(kpiPlanRouteBonus.getPercentage()) >= 0) {
                    kpiPlanRouteCalculateDTO.setKpiAmount(kpiPlanRouteBonus.getAmount());
                }
            });
        }
    }
}