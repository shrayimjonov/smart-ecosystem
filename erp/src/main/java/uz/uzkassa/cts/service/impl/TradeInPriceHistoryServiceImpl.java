package uz.uzkassa.cts.service.impl;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.DeviceModel;
import uz.uzkassa.cts.domain.TradeInPriceHistory;
import uz.uzkassa.cts.domain.TradeInPriceHistory_;
import uz.uzkassa.cts.dto.tradeinpricehistory.request.TradeInPriceHistoryRequestDTO;
import uz.uzkassa.cts.dto.tradeinpricehistory.response.TradeInPriceHistoryResponseDTO;
import uz.uzkassa.cts.enums.ContractType;
import uz.uzkassa.cts.filter.TradeInPriceHistoryListFilter;
import uz.uzkassa.cts.mappings.TradeInPriceHistoryMapper;
import uz.uzkassa.cts.repository.TradeInPriceHistoryRepository;
import uz.uzkassa.cts.service.DeviceModelService;
import uz.uzkassa.cts.service.TradeInPriceHistoryService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uz.uzkassa.cts.utils.PredicateUtil.with;

@Slf4j
@Service
@Transactional(readOnly = true)
public class TradeInPriceHistoryServiceImpl extends BaseServiceImpl<TradeInPriceHistory, Long> implements TradeInPriceHistoryService {

    private final TradeInPriceHistoryRepository tradeInPriceHistoryRepository;
    private final DeviceModelService deviceModelService;
    private final TradeInPriceHistoryMapper tradeInPriceHistoryMapper;

    public TradeInPriceHistoryServiceImpl(TradeInPriceHistoryRepository tradeInPriceHistoryRepository,
                                          DeviceModelService deviceModelService,
                                          TradeInPriceHistoryMapper tradeInPriceHistoryMapper) {
        super(tradeInPriceHistoryRepository);
        this.tradeInPriceHistoryRepository = tradeInPriceHistoryRepository;
        this.deviceModelService = deviceModelService;
        this.tradeInPriceHistoryMapper = tradeInPriceHistoryMapper;
    }

    @Override
    @Transactional
    public TradeInPriceHistoryResponseDTO create(TradeInPriceHistoryRequestDTO tradeInPriceHistoryDTO) {
        DeviceModel model = deviceModelService.findById(tradeInPriceHistoryDTO.getDeviceModelId());
        TradeInPriceHistory tradeInPriceHistory = tradeInPriceHistoryMapper.toEntity(tradeInPriceHistoryDTO);
        unsetActiveStatusTradeInPriceHistory(tradeInPriceHistoryDTO);
        tradeInPriceHistory.setContractType(ContractType.TRADE_IN_CONTRACT);
        tradeInPriceHistory = save(tradeInPriceHistory);
        return tradeInPriceHistoryMapper.toDto(tradeInPriceHistory);
    }

    @Override
    @Transactional
    public TradeInPriceHistoryResponseDTO paymentCreate(TradeInPriceHistoryRequestDTO tradeInPriceHistoryDTO) {
        DeviceModel model = deviceModelService.findById(tradeInPriceHistoryDTO.getDeviceModelId());
        TradeInPriceHistory tradeInPriceHistory = tradeInPriceHistoryMapper.toEntity(tradeInPriceHistoryDTO);
        unsetActiveStatusTradeInPriceHistory(tradeInPriceHistoryDTO);
        tradeInPriceHistory.setContractType(ContractType.PAYMENT_CONTRACT);
        tradeInPriceHistory = save(tradeInPriceHistory);
        return tradeInPriceHistoryMapper.toDto(tradeInPriceHistory);
    }

    @Override
    @Transactional
    public TradeInPriceHistoryResponseDTO update(TradeInPriceHistoryRequestDTO tradeInPriceHistoryDTO) {
        TradeInPriceHistory tradeInPriceHistory = findById(tradeInPriceHistoryDTO.getId());
        tradeInPriceHistoryMapper.toEntity(tradeInPriceHistoryDTO, tradeInPriceHistory);
        unsetActiveStatusTradeInPriceHistory(tradeInPriceHistoryDTO);
        save(tradeInPriceHistory);
        return tradeInPriceHistoryMapper.toDto(tradeInPriceHistory);
    }

    @Override
    public TradeInPriceHistoryResponseDTO getById(Long id) {
        return tradeInPriceHistoryMapper.toDto(findById(id));
    }

    public TradeInPriceHistoryResponseDTO getActiveByDeviceModelId(Long id) {
        TradeInPriceHistory tradeInPriceHistory = tradeInPriceHistoryRepository.findFirstByActiveTrueAndDeviceModelId(id);
        return tradeInPriceHistoryMapper.toDto(tradeInPriceHistory);
    }

    @Override
    public Page<TradeInPriceHistoryResponseDTO> list(TradeInPriceHistoryListFilter filter) {
        return tradeInPriceHistoryRepository
                .findAll(this.specification(filter), filter.getOrderedPageable())
                .map(tradeInPriceHistoryMapper::toDto);
    }

    @Override
    public List<TradeInPriceHistoryResponseDTO> lookUp(TradeInPriceHistoryListFilter filter) {
        return tradeInPriceHistoryRepository
                .findAll(this.specification(filter))
                .stream()
                .map(tradeInPriceHistoryMapper::toDto).collect(Collectors.toList());
    }

    private Specification<TradeInPriceHistory> specification(TradeInPriceHistoryListFilter filter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);

            Path<String> deviceModel = root.get(TradeInPriceHistory_.DEVICE_MODEL);
            Predicate and = criteriaBuilder.and(Stream.of(
                    with(filter::getContractType, a -> criteriaBuilder.equal(root.get(TradeInPriceHistory_.CONTRACT_TYPE), a))
            ).filter(Objects::nonNull).toArray(Predicate[]::new));

            Predicate or = criteriaBuilder.or(Stream.of(
                    with(filter::getSearch, a -> criteriaBuilder.like(criteriaBuilder.lower(deviceModel), a))
            ).filter(Objects::nonNull).toArray(Predicate[]::new));

            return criteriaBuilder.or(and, or);
        };
    }

    private void unsetActiveStatusTradeInPriceHistory(TradeInPriceHistoryRequestDTO tradeInPriceHistoryDTO) {
        if (tradeInPriceHistoryDTO.isActive()) {
            TradeInPriceHistory history = tradeInPriceHistoryRepository.findFirstByActiveTrueAndDeviceModelId(tradeInPriceHistoryDTO.getDeviceModelId());
            if (history != null && (tradeInPriceHistoryDTO.getId() != null && !tradeInPriceHistoryDTO.getId().equals(history.getId()))) {
                history.setActive(false);
                save(history);
            }
        }
    }
}
