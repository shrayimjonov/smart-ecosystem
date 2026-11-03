package uz.uzkassa.smartpossupply.service.kpi;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;
import uz.uzkassa.smartpossupply.domain.kpi.KPI;
import uz.uzkassa.smartpossupply.domain.kpi.client.KPIItem;
import uz.uzkassa.smartpossupply.domain.kpi.client.KPINewClient;
import uz.uzkassa.smartpossupply.domain.planroute.PlanRoute;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.kpi.KPICalculateDTO;
import uz.uzkassa.smartpossupply.dto.kpi.KPICalculateTotalDTO;
import uz.uzkassa.smartpossupply.dto.kpi.KPINewClientDTO;
import uz.uzkassa.smartpossupply.dto.kpi.detail.KPINewClientDetailDTO;
import uz.uzkassa.smartpossupply.dto.kpi.detail.KPIPlanRouteItemDetailDTO;
import uz.uzkassa.smartpossupply.enums.KPIStatus;
import uz.uzkassa.smartpossupply.enums.KPIType;
import uz.uzkassa.smartpossupply.filters.KPIFilter;
import uz.uzkassa.smartpossupply.repository.PartnerRepository;
import uz.uzkassa.smartpossupply.repository.kpi.KPINewClientRepository;
import uz.uzkassa.smartpossupply.service.kpi.calculate.KPICalculateStrategy;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.AlreadyExistException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import uz.uzkassa.smartpossupply.service.ValidationService;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class KPINewClientService implements KPIServiceStrategy<KPINewClientDTO, KPINewClient, KPINewClientDetailDTO>, KPICalculateStrategy<KPIFilter, KPICalculateDTO, KPICalculateTotalDTO> {
    KPINewClientRepository kpiNewClientRepository;
    PartnerRepository partnerRepository;
    ValidationService validationService;

    @Override
    public KPIType getType() {
        return KPIType.NEW_CLIENT;
    }

    @Override
    @Transactional
    public Long create(KPINewClientDTO kpiNewClientDTO) {
        kpiNewClientDTO.setCompanyId(SecurityUtils.getCompanyId());
//        if (kpiNewClientRepository.existsByRoleAndTypeAndCompanyIdAndDeletedIsFalse(kpiNewClientDTO.getRole(), kpiNewClientDTO.getType(), kpiNewClientDTO.getCompanyId())) {
//            throw new AlreadyExistException("KPI already exists");
//        }
        KPINewClient kpi = new KPINewClient();
        convertFromDTO(kpi, kpiNewClientDTO);
        return kpiNewClientRepository.save(kpi).getId();
    }

    @Override
    @Transactional
    public Long update(Long id, KPINewClientDTO kpiNewClientDTO) {
        kpiNewClientDTO.setCompanyId(SecurityUtils.getCompanyId());
//        if (kpiNewClientRepository.existsByIdNotAndRoleAndTypeAndCompanyIdAndDeletedIsFalse(id, kpiNewClientDTO.getRole(), kpiNewClientDTO.getType(), kpiNewClientDTO.getCompanyId())) {
//            throw new AlreadyExistException("KPI already exists");
//        }
        KPINewClient kpiNewClient = kpiNewClientRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(KPINewClient.class.getSimpleName(), "id", id));
        kpiNewClient.getKpiItems().clear();
        convertFromDTO(kpiNewClient, kpiNewClientDTO);
        return kpiNewClientRepository.save(kpiNewClient).getId();
    }

    @Override
    public KPINewClientDetailDTO toDTO(KPINewClient kpiNewClient) {
        KPINewClientDetailDTO kpiNewClientDetailDTO = new KPINewClientDetailDTO();
        kpiNewClientDetailDTO.setId(kpiNewClient.getId());
        kpiNewClientDetailDTO.setType(kpiNewClient.getType());
        kpiNewClientDetailDTO.setRole(kpiNewClient.getRole());
        kpiNewClientDetailDTO.setName(kpiNewClient.getName());
        kpiNewClientDetailDTO.setRegion(Optional.ofNullable(kpiNewClient.getRegion()).map(SimpleEntity::toCommonDTO).orElse(null));
        kpiNewClientDetailDTO.setClientLimit(kpiNewClient.getClientLimit());
        kpiNewClientDetailDTO.setBonusAmount(kpiNewClient.getBonusAmount());
        kpiNewClientDetailDTO.setItems(kpiNewClient.getKpiItems().stream().map(kpiItem -> {
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
        return kpiNewClientDetailDTO;
    }

    @Override
    public void validationUpdateStatus(KPI kpi, KPIStatus status) {
        if (kpiNewClientRepository.updateStatus(kpi, status)) {
            throw new AlreadyExistException("KPI with active status exists", "У вас уже есть KPI со статусом «Активен»");
        }
    }

    @Override
    public ResultList<KPICalculateDTO> calculate(KPIFilter filter) {
        filter.setCompanyId(SecurityUtils.getCompanyId());
        ResultList<KPICalculateDTO> resultList = partnerRepository.calculateANewClients(filter);
        resultList.getList().forEach(kpiCalculateDTO -> {
                    Map<Long, KPINewClient> kpis = getKPI(filter);
                    if (kpis.isEmpty()) {
                        return;
                    }
                    calculateKPI(kpiCalculateDTO, kpis);
                }
        );

        return resultList;
    }

    @Override
    public KPICalculateTotalDTO calculateTotal(KPIFilter filter) {
        filter.setCompanyId(SecurityUtils.getCompanyId());
        KPICalculateTotalDTO kpiCalculateTotalDTO = partnerRepository.calculateTotal(filter);
        calculateTotal(kpiCalculateTotalDTO, filter.getCompanyId());
        return kpiCalculateTotalDTO;
    }


    private void convertFromDTO(KPINewClient kpiNewClient, KPINewClientDTO kpiNewClientDTO) {
        kpiNewClient.setRole(kpiNewClientDTO.getRole());
        kpiNewClient.setType(kpiNewClientDTO.getType());
        kpiNewClient.setName(kpiNewClientDTO.getName());
        kpiNewClient.setRegionId(kpiNewClientDTO.getRegionId());
        kpiNewClient.setClientLimit(kpiNewClientDTO.getClientLimit());
        kpiNewClient.setBonusAmount(kpiNewClientDTO.getBonusAmount());
        kpiNewClient.setCompanyId(kpiNewClientDTO.getCompanyId());
        if (!CollectionUtils.isEmpty(kpiNewClientDTO.getPlanRouteIds())) {
            kpiNewClientDTO.getPlanRouteIds().forEach(planRouteId -> {
                        KPIItem kpiItem = new KPIItem();
                        kpiItem.setPlanRouteId(planRouteId);
                        kpiItem.setKpi(kpiNewClient);
                        kpiNewClient.getKpiItems().add(kpiItem);
                    }
            );
        }
    }

    private Map<Long, KPINewClient> getKPI(KPIFilter filter) {
        Map<Long, KPINewClient> kpiNewClientMap = new HashMap<>();
        kpiNewClientRepository.findAllByCompanyIdAndDeletedIsFalseAndStatus(filter.getCompanyId(), KPIStatus.ACTIVE)
                .forEach(kpiNewClient -> {
                            if (!kpiNewClient.getEmployees().isEmpty()) {
                                kpiNewClient.getEmployees().forEach(employee -> {
                                    kpiNewClientMap.put(employee.getId(), kpiNewClient);
                                });
                            } else {
                                kpiNewClientMap.put(0L, kpiNewClient); //    it means this kpi for all customer
                            }
                        }
                );
        return kpiNewClientMap;
    }

    private void calculateKPI(KPICalculateDTO kpiCalculateDTO, Map<Long, KPINewClient> kpis) {
        KPINewClient kpi;
        if (kpis.containsKey(kpiCalculateDTO.getEmployeeId())) {
            kpi = kpis.get(kpiCalculateDTO.getEmployeeId());
        } else {
            kpi = kpis.get(0L);
        }
        if (kpiCalculateDTO.getTotalCount() >= kpi.getClientLimit()) {
            kpiCalculateDTO.setKpiAmount(kpi.getBonusAmount());
        }
    }

    private void calculateTotal(KPICalculateTotalDTO kpiCalculateTotalDTO, Long companyId) {
        kpiNewClientRepository.findByCompanyIdAndDeletedIsFalseAndStatus(companyId, KPIStatus.ACTIVE)
                .ifPresent(kpiNewClient -> {
                    if (kpiCalculateTotalDTO.getTotalCount() / kpiCalculateTotalDTO.getCountEmployees() >= kpiNewClient.getClientLimit()) {
                        kpiCalculateTotalDTO.setTotalKpiAmount(kpiNewClient.getBonusAmount());
                    }
                });

    }
}

