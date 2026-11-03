package uz.uzkassa.cts.service.impl;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.BonusHistory;
import uz.uzkassa.cts.domain.CtsService;
import uz.uzkassa.cts.domain.Tariff;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.dto.ctsservice.response.ServiceResponseDto;
import uz.uzkassa.cts.dto.tariff.TariffTotalDTO;
import uz.uzkassa.cts.dto.tariff.request.TariffRequestDto;
import uz.uzkassa.cts.dto.tariff.request.TariffServiceDto;
import uz.uzkassa.cts.dto.tariff.response.TariffServiceResponseDto;
import uz.uzkassa.cts.enums.BonusType;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.EmployeeReportFilter;
import uz.uzkassa.cts.mappings.ServiceMapper;
import uz.uzkassa.cts.mappings.TariffMapper;
import uz.uzkassa.cts.repository.BonusHistoryRepository;
import uz.uzkassa.cts.repository.CtsServiceRepository;
import uz.uzkassa.cts.repository.TariffRepository;
import uz.uzkassa.cts.service.TariffService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TariffServiceImpl extends BaseServiceImpl<Tariff, Long> implements TariffService {

    private final TariffRepository repository;
    private final CtsServiceRepository serviceRepository;
    private final BonusHistoryRepository bonusHistoryRepository;
    private final ServiceMapper serviceMapper;
    private final TariffMapper tariffMapper;

    public TariffServiceImpl(TariffRepository repository,
                             CtsServiceRepository serviceRepository,
                             BonusHistoryRepository bonusHistoryRepository,
                             ServiceMapper serviceMapper,
                             TariffMapper tariffMapper) {
        super(repository);
        this.repository = repository;
        this.serviceRepository = serviceRepository;
        this.bonusHistoryRepository = bonusHistoryRepository;
        this.serviceMapper = serviceMapper;
        this.tariffMapper = tariffMapper;
    }

    @Override
    public TariffServiceResponseDto create(TariffRequestDto tariffDto) {
        Tariff tariff = tariffMapper.toEntity(tariffDto);
        List<CtsService> services = serviceRepository.findAllByIdIn(tariffDto.getServiceIds());

        for (CtsService service : services) {
            tariff.getCtsServices().add(service);
        }

        tariff = save(tariff);

        BonusHistory bonusHistory = new BonusHistory();
        bonusHistory.setBonus(tariff.getBonus());
        bonusHistory.setBonusType(BonusType.TARIFF);
        bonusHistory.setTariff(tariff);
        bonusHistory.setTariffId(tariff.getId());
        bonusHistoryRepository.save(bonusHistory);

        return tariffMapper.toDtoWithServices(tariff);
    }

    @Override
    public TariffRequestDto update(TariffRequestDto tariffDto) {
        Tariff tariff = findById(tariffDto.getId());
        boolean isBonusChanged = tariff.getBonus().compareTo(tariffDto.getBonus()) != 0;
        tariffMapper.toEntity(tariffDto, tariff);
        tariff = save(tariff);

        if (isBonusChanged) {
            BonusHistory bonusHistory = new BonusHistory();
            bonusHistory.setBonus(tariff.getBonus());
            bonusHistory.setBonusType(BonusType.TARIFF);
            bonusHistory.setTariff(tariff);
            bonusHistory.setTariffId(tariff.getId());
            bonusHistoryRepository.save(bonusHistory);
        }

        return tariffMapper.toDto(tariff);
    }

    @Override
    @Transactional(readOnly = true)
    public TariffServiceResponseDto get(Long id) {
        Tariff tariff = findById(id);
        return tariffMapper.toDtoWithServices(tariff);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TariffServiceResponseDto> list(BaseFilter filter) {
        String orderBy = filter.getOrderBy() == null ? "name" : filter.getOrderBy();
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), Sort.by(Sort.Order.asc(orderBy)));

        Specification<Tariff> specification = (root, criteriaQuery, criteriaBuilder) -> {
            Path<String> name = root.get("name");
            ArrayList<Predicate> list = new ArrayList<>();
            if (StringUtils.isNotEmpty(filter.getSearch())) {
                list.add(criteriaBuilder.equal(name, filter.getSearch()));
            }

            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };

        return repository.findAll(specification, pageable).map(tariffMapper::toDtoWithServices);
    }

    @Override
    public List<TariffTotalDTO> listByEmployee(EmployeeReportFilter filter) {
        return repository.listByEmployee(filter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceResponseDto> getTariffServices(Long id) {
        Tariff tariff = findById(id);

        return tariff.getCtsServices()
            .stream()
            .map(serviceMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public TariffServiceResponseDto addService(TariffServiceDto dto) {
        Tariff tariff = findById(dto.getTariffId());
        CtsService myServices = serviceRepository.findById(dto.getServiceId())
            .orElseThrow(validationService.entityNotFoundExceptionThrow(SERVICE));

        tariff.getCtsServices().add(myServices);
        Tariff updTariff = save(tariff);

        return tariffMapper.toDtoWithServices(updTariff);
    }

    @Override
    public TariffServiceResponseDto removeService(TariffServiceDto dto) {
        Tariff tariff = findById(dto.getTariffId());
        List<CtsService> services = tariff.getCtsServices();

        Optional<CtsService> myServices = services.stream().filter(f -> f.getId().equals(dto.getServiceId())).findFirst();
        if (myServices.isEmpty()) {
            throw validationService.entityNotFoundExceptionThrow(SERVICE).get();
        }

        services.remove(myServices.get());
        Tariff updTariff = save(tariff);

        return tariffMapper.toDtoWithServices(updTariff);
    }

    @Override
    public List<CommonCodeDTO> getItems() {
        List<Tariff> tariffs = repository.findAll();
        return tariffs.stream().map(tariff -> new CommonCodeDTO(tariff.getId(), tariff.getName(), tariff.getCode())).collect(Collectors.toList());
    }
}
