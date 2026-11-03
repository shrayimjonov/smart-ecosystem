package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.TradeInDevice;
import uz.uzkassa.cts.domain.TradeInDevice_;
import uz.uzkassa.cts.dto.tradeindevice.TradeInDeviceDTO;
import uz.uzkassa.cts.enums.SmartOrOldDeviceEnum;
import uz.uzkassa.cts.filter.TradeInDeviceListFilter;
import uz.uzkassa.cts.mappings.TradeInDeviceMapper;
import uz.uzkassa.cts.repository.TradeInDeviceRepository;
import uz.uzkassa.cts.service.TradeInDeviceService;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;

@Transactional
@Service
@Slf4j
public class TradeInDeviceServiceImpl extends BaseServiceImpl<TradeInDevice, Long> implements TradeInDeviceService {
    private final TradeInDeviceRepository tradeInDeviceRepository;
    private final TradeInDeviceMapper tradeInDeviceMapper;

    public TradeInDeviceServiceImpl(TradeInDeviceRepository tradeInDeviceRepository, TradeInDeviceMapper tradeInDeviceMapper) {
        super(tradeInDeviceRepository);
        this.tradeInDeviceRepository = tradeInDeviceRepository;
        this.tradeInDeviceMapper = tradeInDeviceMapper;
    }

    @Override
    public TradeInDeviceDTO create(TradeInDeviceDTO tradeInDeviceDTO) {
        TradeInDevice tradeInDevice = tradeInDeviceMapper.toEntity(tradeInDeviceDTO);
        save(tradeInDevice);
        return tradeInDeviceMapper.toDto(tradeInDevice);
    }

    @Override
    public TradeInDeviceDTO update(TradeInDeviceDTO tradeInDeviceDTO) {
        TradeInDevice tradeInDevice = findById(tradeInDeviceDTO.getId());
        tradeInDeviceMapper.toEntity(tradeInDeviceDTO, tradeInDevice);
        save(tradeInDevice);
        return tradeInDeviceMapper.toDto(tradeInDevice);
    }

    @Override
    @Transactional(readOnly = true)
    public TradeInDeviceDTO getById(Long id) {
        TradeInDevice tradeInDevice = findById(id);
        return tradeInDeviceMapper.toDto(tradeInDevice);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TradeInDeviceDTO> list(TradeInDeviceListFilter filter) {
        Specification<TradeInDevice> specification = (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);

            ArrayList<Predicate> list = new ArrayList<>();
            if (StringUtils.isNotEmpty(filter.getSearch())) {
                Path<String> name = root.get(TradeInDevice_.NAME);
                list.add(criteriaBuilder.like(criteriaBuilder.lower(name), filter.getSearchForQuery()));
            }
            if (filter.getSmartOrOldDeviceEnum() != null) {
                Path<SmartOrOldDeviceEnum> smartOrOldDeviceEnumPath = root.get(TradeInDevice_.SMART_OR_OLD_DEVICE_ENUM);
                list.add(criteriaBuilder.equal(smartOrOldDeviceEnumPath, filter.getSmartOrOldDeviceEnum()));
            }

            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
        return tradeInDeviceRepository
            .findAll(specification, filter.getOrderedPageable())
            .map(tradeInDeviceMapper::toDto);
    }
}
