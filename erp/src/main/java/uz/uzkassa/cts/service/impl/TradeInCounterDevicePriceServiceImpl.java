package uz.uzkassa.cts.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.TradeInCounterDevicePrice;
import uz.uzkassa.cts.domain.TradeInCounterDevicePrice_;
import uz.uzkassa.cts.dto.tradeIncounterdeviceprice.request.TradeInCounterDevicePriceRequestDTO;
import uz.uzkassa.cts.dto.tradeIncounterdeviceprice.response.TradeInCounterDevicePriceResponseDTO;
import uz.uzkassa.cts.enums.CustomerVatTypeEnum;
import uz.uzkassa.cts.enums.SmartOrOldDeviceEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.mappings.TradeInCounterDevicePriceMapper;
import uz.uzkassa.cts.repository.TradeInCounterDevicePriceRepository;
import uz.uzkassa.cts.service.TradeInCounterDevicePriceService;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TradeInCounterDevicePriceServiceImpl extends BaseServiceImpl<TradeInCounterDevicePrice, Long> implements TradeInCounterDevicePriceService {

    private final TradeInCounterDevicePriceRepository repository;
    private final TradeInCounterDevicePriceMapper tradeInCounterDevicePriceMapper;

    public TradeInCounterDevicePriceServiceImpl(TradeInCounterDevicePriceRepository repository, TradeInCounterDevicePriceMapper tradeInCounterDevicePriceMapper) {
        super(repository);
        this.repository = repository;
        this.tradeInCounterDevicePriceMapper = tradeInCounterDevicePriceMapper;
    }

    @Override
    @Transactional
    public TradeInCounterDevicePriceResponseDTO create(TradeInCounterDevicePriceRequestDTO dto) {
        if (CustomerVatTypeEnum.WITHOUT_VAT == dto.getCustomerVatTypeEnum()) {
            dto.setVat1pc(null);
        }

        TradeInCounterDevicePrice tradeInCounterDevicePrice = tradeInCounterDevicePriceMapper.toEntity(dto);
        unsetActiveStatusTradeInCounterDevicePrice(dto);
        tradeInCounterDevicePrice = save(tradeInCounterDevicePrice);
        return tradeInCounterDevicePriceMapper.toDto(tradeInCounterDevicePrice);
    }

    @Override
    @Transactional
    public TradeInCounterDevicePriceResponseDTO update(TradeInCounterDevicePriceRequestDTO dto) {
        TradeInCounterDevicePrice tradeInCounterDevicePrice = findById(dto.getId());

        if (CustomerVatTypeEnum.WITHOUT_VAT == dto.getCustomerVatTypeEnum()) {
            dto.setVat1pc(null);
        }

        unsetActiveStatusTradeInCounterDevicePrice(dto);
        tradeInCounterDevicePriceMapper.toEntity(dto, tradeInCounterDevicePrice);
        save(tradeInCounterDevicePrice);
        return tradeInCounterDevicePriceMapper.toDto(tradeInCounterDevicePrice);
    }

    @Override
    @Transactional(readOnly = true)
    public TradeInCounterDevicePriceResponseDTO getById(Long id) {
        return tradeInCounterDevicePriceMapper.toDto(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TradeInCounterDevicePriceResponseDTO> list(BaseFilter filter) {
        Specification<TradeInCounterDevicePrice> specification = (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);

            ArrayList<Predicate> list = new ArrayList<>();
            if (StringUtils.isNotEmpty(filter.getSearch())) {
                Path<String> smartOrOldDeviceEnum = root.get(TradeInCounterDevicePrice_.SMART_OR_OLD_DEVICE_ENUM);
                list.add(criteriaBuilder.like(criteriaBuilder.lower(smartOrOldDeviceEnum), filter.getSearchForQuery()));
            }
            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
        return repository
            .findAll(specification, filter.getOrderedPageable())
            .map(tradeInCounterDevicePriceMapper::toDto);
    }

    private void unsetActiveStatusTradeInCounterDevicePrice(TradeInCounterDevicePriceRequestDTO dto) {
        if (dto.isActive()) {
            TradeInCounterDevicePrice tradeInCounterDevicePrice = repository.findByActiveSmartOrOldDevice(dto.getSmartOrOldDeviceEnum(), dto.getCustomerVatTypeEnum());
            if (tradeInCounterDevicePrice != null && !dto.getId().equals(tradeInCounterDevicePrice.getId())) {
                tradeInCounterDevicePrice.setActive(false);
                save(tradeInCounterDevicePrice);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TradeInCounterDevicePriceResponseDTO findByActiveSmartOrOldDevice(SmartOrOldDeviceEnum deviceType, CustomerVatTypeEnum customerVatTypeEnum) {
        return tradeInCounterDevicePriceMapper.toDto(repository.findByActiveSmartOrOldDevice(deviceType, customerVatTypeEnum));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TradeInCounterDevicePriceResponseDTO> getActiveDevices() {
        return repository.getActiveDevices()
            .stream().map(tradeInCounterDevicePriceMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TradeInCounterDevicePriceResponseDTO> getActiveDevicesByDeviceType(SmartOrOldDeviceEnum deviceType) {
        return repository.getActiveDevicesByDeviceType(deviceType)
            .stream().map(tradeInCounterDevicePriceMapper::toDto)
            .collect(Collectors.toList());
    }
}
