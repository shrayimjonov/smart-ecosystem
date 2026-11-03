package uz.uzkassa.cts.service.impl;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.modelpricehistory.ModelPriceHistoryDetailDTO;
import uz.uzkassa.cts.dto.modelpricehistory.request.ModelPriceHistoryDto;
import uz.uzkassa.cts.dto.modelpricehistory.response.ModelPriceHistoryResponseDto;
import uz.uzkassa.cts.enums.BonusType;
import uz.uzkassa.cts.enums.CompanyType;
import uz.uzkassa.cts.enums.OrderType;
import uz.uzkassa.cts.filter.ModelPriceHistoryFilter;
import uz.uzkassa.cts.mappings.ModelPriceHistoryMapper;
import uz.uzkassa.cts.repository.BonusHistoryRepository;
import uz.uzkassa.cts.repository.DeviceModelRepository;
import uz.uzkassa.cts.repository.ModelPriceHistoryRepository;
import uz.uzkassa.cts.service.ModelPriceHistoryService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class ModelPriceHistoryServiceImpl extends BaseServiceImpl<ModelPriceHistory, Long> implements ModelPriceHistoryService {

    private final ModelPriceHistoryRepository repository;
    private final DeviceModelRepository deviceModelRepository;
    private final BonusHistoryRepository bonusHistoryRepository;
    private final ModelPriceHistoryMapper modelPriceHistoryMapper;

    public ModelPriceHistoryServiceImpl(ModelPriceHistoryRepository repository,
                                        DeviceModelRepository deviceModelRepository,
                                        BonusHistoryRepository bonusHistoryRepository,
                                        ModelPriceHistoryMapper modelPriceHistoryMapper) {
        super(repository);
        this.repository = repository;
        this.deviceModelRepository = deviceModelRepository;
        this.bonusHistoryRepository = bonusHistoryRepository;
        this.modelPriceHistoryMapper = modelPriceHistoryMapper;
    }

    @Override
    public ModelPriceHistoryResponseDto update(ModelPriceHistoryDto modelPriceHistoryDto) {
        DeviceModel deviceModel = deviceModelRepository.findById(modelPriceHistoryDto.getModelId())
                .orElseThrow(validationService.entityNotFoundExceptionThrow(DEVICE_MODEL));

        ModelPriceHistory leasing = repository.findById(modelPriceHistoryDto.getId())
                .orElseThrow(validationService.entityNotFoundExceptionThrow(HISTORY));

        boolean isBonusChanged = leasing.getBonus().compareTo(modelPriceHistoryDto.getBonus()) != 0;

        leasing.setPrice(modelPriceHistoryDto.getPrice());
        leasing.setDeviceModel(deviceModel);
        leasing.setName(modelPriceHistoryDto.getName());
        leasing.setPeriod(modelPriceHistoryDto.getPeriod());
        leasing.setPeriodType(modelPriceHistoryDto.getPeriodType());
        leasing.setFirstPaymentForLeasing(modelPriceHistoryDto.getFirstPaymentForLeasing());
        leasing.setBonus(modelPriceHistoryDto.getBonus());
        leasing.setCompanyType(modelPriceHistoryDto.getCompanyType());
        leasing.setActive(modelPriceHistoryDto.getActive());
        if (modelPriceHistoryDto.getDeviceType() != null) {
            leasing.setDeviceType(modelPriceHistoryDto.getDeviceType());
        }
        leasing = save(leasing);

        if (isBonusChanged) {
            BonusHistory bonusHistory = new BonusHistory();
            bonusHistory.setLeasing(leasing);
            bonusHistory.setLeasingId(leasing.getId());
            bonusHistory.setBonus(leasing.getBonus());
            bonusHistory.setBonusType(BonusType.LEASING);
            bonusHistoryRepository.save(bonusHistory);
        }

        return modelPriceHistoryMapper.toDto(leasing);
    }

    @Override
    public ModelPriceHistoryResponseDto create(ModelPriceHistoryDto modelPriceHistoryDto) {
        DeviceModel deviceModel = deviceModelRepository.findById(modelPriceHistoryDto.getModelId()).orElseThrow(validationService.entityNotFoundExceptionThrow(DEVICE));

        ModelPriceHistory leasing = new ModelPriceHistory();
        leasing.setPrice(modelPriceHistoryDto.getPrice());
        leasing.setModelId(deviceModel.getId());
        leasing.setName(modelPriceHistoryDto.getName());
        leasing.setPeriod(modelPriceHistoryDto.getPeriod());
        leasing.setPeriodType(modelPriceHistoryDto.getPeriodType());
        leasing.setFirstPaymentForLeasing(modelPriceHistoryDto.getFirstPaymentForLeasing());
        leasing.setBonus(modelPriceHistoryDto.getBonus());
        leasing.setCompanyType(modelPriceHistoryDto.getCompanyType());
        leasing.setOrderType(OrderType.LEASING);
        leasing.setDeviceType(modelPriceHistoryDto.getDeviceType());

        // save new history
        leasing = save(leasing);

        BonusHistory bonusHistory = new BonusHistory();
        bonusHistory.setLeasing(leasing);
        bonusHistory.setLeasingId(leasing.getId());
        bonusHistory.setBonus(leasing.getBonus());
        bonusHistory.setBonusType(BonusType.LEASING);
        bonusHistoryRepository.save(bonusHistory);

        return modelPriceHistoryMapper.toDto(leasing);

    }

    @Override
    public ModelPriceHistoryResponseDto getById(Long id) {
        ModelPriceHistory history = repository.findById(id)
                .orElseThrow(validationService.entityNotFoundExceptionThrow(HISTORY));

        return modelPriceHistoryMapper.toDto(history);
    }

    @Override
    public Page<ModelPriceHistoryResponseDto> list(ModelPriceHistoryFilter filter) {

        Specification<ModelPriceHistory> specification = (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);

            ArrayList<Predicate> list = new ArrayList<>();
            if (StringUtils.isNotEmpty(filter.getSearch())) {
                Path<String> namePath = root.get(ModelPriceHistory_.NAME);
                Path<DeviceModel> deviceModelPath = root.join(ModelPriceHistory_.DEVICE_MODEL, JoinType.INNER);
                Path<String> deviceModelNamePath = deviceModelPath.get(DeviceModel_.NAME);
                Predicate namePred = criteriaBuilder.like(criteriaBuilder.upper(namePath), filter.getSearchForQuery());
                Predicate deviceModelNamePred = criteriaBuilder.like(criteriaBuilder.upper(deviceModelNamePath), filter.getSearchForQuery());
                list.add(criteriaBuilder.or(namePred, deviceModelNamePred));
            }
            if (filter.getOrderType() != null) {
                Path<OrderType> orderTypePath = root.get(ModelPriceHistory_.ORDER_TYPE);
                list.add(criteriaBuilder.equal(orderTypePath, filter.getOrderType()));
            }

            list.add(criteriaBuilder.equal(root.get(ModelPriceHistory_.active), true));

            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
        Page<ModelPriceHistory> historyResponse = repository.findAll(specification, filter.getOrderedPageable());

        return historyResponse.map(modelPriceHistoryMapper::toDto);
    }

    @Override
    public void customDeleteById(Long id) {
        try {
            deleteById(id);
        } catch (Exception e) {
            log.error("ModelPriceHistory delete exception: {}", e.getMessage());
            throw validationService.badRequestExceptionThrow(INVALID_ACTION).get();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ModelPriceHistoryDetailDTO getDetail() {
        Optional<ModelPriceHistory> modelPriceHistoryOptional = repository
                .findFirstByOrderTypeAndCompanyTypeAndActiveIsTrueOrderByIdDesc(OrderType.LEASING, CompanyType.PRIME);
        if (modelPriceHistoryOptional.isPresent()) {
            ModelPriceHistory modelPriceHistory = modelPriceHistoryOptional.get();
            return ModelPriceHistoryDetailDTO
                    .builder()
                    .firsPayment(modelPriceHistory.getFirstPaymentForLeasing())
                    .period(modelPriceHistory.getPeriod())
                    .monthlyPayment(((modelPriceHistory.getPrice().subtract(modelPriceHistory.getFirstPaymentForLeasing())).divide(BigDecimal.valueOf(modelPriceHistory.getPeriod()))))
                    .build();
        }
        return null;
    }
}
