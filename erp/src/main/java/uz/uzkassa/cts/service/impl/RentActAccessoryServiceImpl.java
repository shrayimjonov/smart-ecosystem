package uz.uzkassa.cts.service.impl;

import org.springframework.stereotype.Service;
import uz.uzkassa.cts.domain.RentActAccessory;
import uz.uzkassa.cts.dto.rentactaccessory.response.RentActAccessoryResponseDto;
import uz.uzkassa.cts.mappings.RentActAccessoryMapper;
import uz.uzkassa.cts.repository.RentActAccessoryRepository;
import uz.uzkassa.cts.service.RentActAccessoryService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RentActAccessoryServiceImpl extends BaseServiceImpl<RentActAccessory, Long> implements RentActAccessoryService {

    private final RentActAccessoryRepository repository;
    private final RentActAccessoryMapper rentActAccessoryMapper;

    public RentActAccessoryServiceImpl(RentActAccessoryRepository repository, RentActAccessoryMapper rentActAccessoryMapper) {
        super(repository);
        this.repository = repository;
        this.rentActAccessoryMapper = rentActAccessoryMapper;
    }

    @Override
    public List<RentActAccessoryResponseDto> getActiveDetails() {
        return repository.findByActiveAndActOfReturnIdIsNull(true)
            .stream().map(rentActAccessoryMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<RentActAccessoryResponseDto> getAccessoriesByCustomerId(Long customerId) {
        return repository.findByActiveAndCustomerId(true, customerId)
            .stream().map(rentActAccessoryMapper::toDto)
            .collect(Collectors.toList());
    }
}
