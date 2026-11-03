package uz.uzkassa.smartpossupply.service.kpi;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.kpi.KPI;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.kpi.KPIBaseDTO;
import uz.uzkassa.smartpossupply.dto.kpi.KPICalculateDTO;
import uz.uzkassa.smartpossupply.dto.kpi.KPICalculateTotalDTO;
import uz.uzkassa.smartpossupply.dto.kpi.KPIListDTO;
import uz.uzkassa.smartpossupply.dto.kpi.detail.KPIDetailDTO;
import uz.uzkassa.smartpossupply.enums.KPIStatus;
import uz.uzkassa.smartpossupply.filters.KPIFilter;
import uz.uzkassa.smartpossupply.repository.kpi.KPIRepository;
import uz.uzkassa.smartpossupply.service.kpi.calculate.KPICalculateStrategy;
import uz.uzkassa.smartpossupply.service.kpi.calculate.KpiCalculateFactory;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.List;

import uz.uzkassa.smartpossupply.service.ValidationService;

@Service
@Transactional(readOnly = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class KPIService {
    KpiServiceFactoryMap factoryMap;
    KPIRepository kpiRepository;
    KpiCalculateFactory kpiCalculateFactory;
    ValidationService validationService;

    @Transactional
    public <T extends KPIBaseDTO, E extends KPI, U extends KPIDetailDTO> Long create(T dto) {
        KPIServiceStrategy<T, E, U> strategy = factoryMap.getService(dto.getType());
        return strategy.create(dto);
    }

    @Transactional
    public <T extends KPIBaseDTO> Long update(Long id, T dto) {
        KPIServiceStrategy<KPIBaseDTO, KPI, KPIDetailDTO> strategy = factoryMap.getService(dto.getType());
        return strategy.update(id, dto);
    }

    @Transactional
    public Long delete(Long id) {
        return kpiRepository.deleteByIdAndCompanyId(id, SecurityUtils.getCompanyId());
    }

    @Transactional
    public <T extends KPIBaseDTO, E extends KPI, U extends KPIDetailDTO> void updateStatus(Long id, KPIStatus status) {
        Long companyId = SecurityUtils.getCompanyId();
        KPI kpi = kpiRepository.findByIdAndCompanyIdAndDeletedIsFalse(id, companyId).orElseThrow(validationService.entityNotFoundExceptionThrow(KPI.class.getSimpleName(), "id", id));
        if (KPIStatus.ACTIVE.equals(status)) { // Only one active kpi should exist at a time
            KPIServiceStrategy<T, E, U> strategy = factoryMap.getService(kpi.getType());
            strategy.validationUpdateStatus(kpi, status);
        }
        kpi.setStatus(status);
        kpiRepository.save(kpi);
    }

    public Page<KPIListDTO> getList(KPIFilter filter) {
        filter.setCompanyId(SecurityUtils.getCompanyId());
        ResultList<KPI> resultList = kpiRepository.getResultList(filter);
        List<KPIListDTO> result = resultList.getList().stream().map(kpi -> {
            KPIListDTO kpiListDTO = new KPIListDTO();
            kpiListDTO.setId(kpi.getId());
            kpiListDTO.setName(kpi.getName());
            kpiListDTO.setType(kpi.getType());
            kpiListDTO.setRole(kpi.getRole());
            kpiListDTO.setStatus(kpi.getStatus());
            kpiListDTO.setCreatedAt(kpi.getCreatedDate());
            kpiListDTO.setUpdatedAt(kpi.getUpdatedDate());
            kpiListDTO.setCreatedBy(kpi.getCreator().getFullName());
            kpiListDTO.setUpdatedBy(kpi.getCreator().getFullName());
            kpiListDTO.setCountEmployee(kpi.getEmployees().size());
            return kpiListDTO;
        }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public KPIListDTO getKPIInfo(Long id) {
        Long companyId = SecurityUtils.getCompanyId();
        KPI kpi = kpiRepository.findByIdAndCompanyIdAndDeletedIsFalse(id, companyId).orElseThrow(validationService.entityNotFoundExceptionThrow(KPI.class.getSimpleName(), "id", id));
        KPIListDTO kpiListDTO = new KPIListDTO();
        kpiListDTO.setId(kpi.getId());
        kpiListDTO.setName(kpi.getName());
        kpiListDTO.setType(kpi.getType());
        kpiListDTO.setRole(kpi.getRole());
        kpiListDTO.setStatus(kpi.getStatus());
        kpiListDTO.setCreatedAt(kpi.getCreatedDate());
        kpiListDTO.setUpdatedAt(kpi.getUpdatedDate());
        kpiListDTO.setCreatedBy(kpi.getCreator().getFullName());
        kpiListDTO.setUpdatedBy(kpi.getCreator().getFullName());
        kpiListDTO.setCountEmployee(kpi.getEmployees().size());
        return kpiListDTO;
    }

    public KPIDetailDTO get(Long id) {
        KPI kpi = kpiRepository.findByIdAndCompanyId(id, SecurityUtils.getCompanyId())
                .orElseThrow(validationService.entityNotFoundExceptionThrow(KPI.class.getSimpleName(), "id", id));
        KPIServiceStrategy<? extends KPIBaseDTO, ? super KPI, ? extends KPIDetailDTO> strategy = factoryMap.getService(kpi.getType());
        return strategy.toDTO(kpi);
    }

    public Page<KPICalculateDTO> calculate(KPIFilter filter) {
        filter.setCompanyId(SecurityUtils.getCompanyId());
        KPICalculateStrategy<KPIFilter, KPICalculateDTO, KPICalculateTotalDTO> strategy = kpiCalculateFactory.getService(filter.getType());
        ResultList<KPICalculateDTO> resultList = strategy.calculate(filter);
        return new PageImpl<>(resultList.getList(), filter.getSortedPageable(), resultList.getCount());
    }

    public KPICalculateTotalDTO calculateTotal(KPIFilter filter) {
        filter.setCompanyId(SecurityUtils.getCompanyId());
        KPICalculateStrategy<KPIFilter, KPICalculateDTO, KPICalculateTotalDTO> strategy = kpiCalculateFactory.getService(filter.getType());
        return strategy.calculateTotal(filter);
    }
}

