package uz.uzkassa.cts.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.BonusHistory;
import uz.uzkassa.cts.domain.CtsService;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.ctsservice.request.ServiceRequestDto;
import uz.uzkassa.cts.dto.ctsservice.response.ServiceResponseDto;
import uz.uzkassa.cts.enums.BonusType;
import uz.uzkassa.cts.enums.Role;
import uz.uzkassa.cts.enums.ServiceRegistrationType;
import uz.uzkassa.cts.enums.ServiceType;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.mappings.ServiceMapper;
import uz.uzkassa.cts.repository.BonusHistoryRepository;
import uz.uzkassa.cts.repository.CtsServiceRepository;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.CtsServiceService;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CtsServiceServiceImpl extends BaseServiceImpl<CtsService, Long> implements CtsServiceService {

    private final CtsServiceRepository repository;
    private final BonusHistoryRepository bonusHistoryRepository;
    private final ServiceMapper serviceMapper;


    public CtsServiceServiceImpl(CtsServiceRepository repository, BonusHistoryRepository bonusHistoryRepository, ServiceMapper serviceMapper) {
        super(repository);
        this.repository = repository;
        this.bonusHistoryRepository = bonusHistoryRepository;
        this.serviceMapper = serviceMapper;
    }

    @Override
    public ServiceResponseDto create(ServiceRequestDto dto) {
        CtsService ctsService = serviceMapper.toEntity(dto);
        ctsService = save(ctsService);

        BonusHistory bonusHistory = new BonusHistory();
        bonusHistory.setCtsService(ctsService);
        bonusHistory.setCtsServiceId(ctsService.getId());
        bonusHistory.setBonus(ctsService.getBonus());
        bonusHistory.setBonusType(BonusType.SERVICE);
        bonusHistoryRepository.save(bonusHistory);

        return serviceMapper.toDto(ctsService);
    }

    @Override
    public ServiceResponseDto update(ServiceRequestDto dto) {
        CtsService ctsService = findById(dto.getId());
        boolean isBonusChanged = ctsService.getBonus().compareTo(dto.getBonus()) != 0;
        serviceMapper.toEntity(dto, ctsService);
        ctsService = save(ctsService);

        if (isBonusChanged) {
            BonusHistory bonusHistory = new BonusHistory();
            bonusHistory.setCtsService(ctsService);
            bonusHistory.setCtsServiceId(ctsService.getId());
            bonusHistory.setBonus(ctsService.getBonus());
            bonusHistory.setBonusType(BonusType.SERVICE);
            bonusHistoryRepository.save(bonusHistory);
        }
        return serviceMapper.toDto(ctsService);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResponseDto get(Long id) {
        CtsService services = findById(id);
        return serviceMapper.toDto(services);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceResponseDto> list(BaseFilter filter) {
        String orderBy = filter.getOrderBy() == null ? "name" : filter.getOrderBy();
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), Sort.by(Sort.Order.asc(orderBy)));

        ArrayList<Predicate> list = new ArrayList<>();
        Specification<CtsService> specification = (root, criteriaQuery, criteriaBuilder) -> {
            if (StringUtils.isNotEmpty(filter.getSearch())) {
                Path<String> name = root.get("name");
                list.add(criteriaBuilder.like(name, "%" + filter.getSearch() + "%"));
            }

            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };

        Page<CtsService> ctsServices = repository.findAll(specification, pageable);
        return ctsServices.map(serviceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceResponseDto> listNotPaged() {
        return findAll().stream().map(serviceMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<ServiceResponseDto> oneTimeList(BaseFilter filter) {
        if (SecurityUtils.isCurrentUserInRoles(Role.OPERATOR.getRoleCode(), Role.EMPLOYEE.getRoleCode())) {
            return repository.findByRegistrationTypeAndServiceTypeAndDeletedOrderByIdAsc(ServiceRegistrationType.ONE_TIME, ServiceType.SERVICE, false)
                .stream().map(serviceMapper::toDto)
                .collect(Collectors.toList());
        }
        return repository.findByRegistrationTypeAndDeletedOrderByIdAsc(ServiceRegistrationType.ONE_TIME, false)
            .stream().map(serviceMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public Page<CommonDTO> lookUp(BaseFilter filter) {
        ResultList<CommonDTO> resultList = repository.getLookUp(filter);
        return new PageImpl<>
                (resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public void customDeleteById(Long id) {
        CtsService service = findById(id);
        service.setDeleted(true);
        save(service);
    }

    @Override
    public List<CommonDTO> getBillingServices() {
        return repository
            .findAllByBillingIdIsNotNullAndDeletedIsFalse()
            .stream()
            .map(CtsService::toCommonDTO)
            .collect(Collectors.toList());
    }
}
