package uz.uzkassa.horeca.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.horeca.domain.EpsServiceDomain;
import uz.uzkassa.horeca.dto.eps.EpsServiceCrudDTO;
import uz.uzkassa.horeca.dto.eps.EpsServiceDTO;
import uz.uzkassa.horeca.dto.eps.TerminalEpsServicesLookupDto;
import uz.uzkassa.horeca.enums.terminal.EpsServiceStatusEnum;
import uz.uzkassa.horeca.integration.ApayClient;
import uz.uzkassa.horeca.mappers.EpsServiceMapper;
import uz.uzkassa.horeca.repository.EpsServiceRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class EpsService extends BaseService {

    EpsServiceRepository epsServiceRepository;
    EpsServiceMapper epsServiceMapper;
    ApayClient apayClient;

    @Transactional
    public EpsServiceDTO create(EpsServiceCrudDTO dto) {
        if (dto.getStatus() == null) {
            dto.setStatus(EpsServiceStatusEnum.ACTIVE);
        }
        EpsServiceDomain epsServiceDomain = epsServiceMapper.toEntity(dto);
        epsServiceDomain.setId(UUID.randomUUID());
        epsServiceDomain.setFromApay(false);
        epsServiceDomain.setCreatedDate(LocalDateTime.now());
        epsServiceDomain.setUpdatedDate(LocalDateTime.now());
        epsServiceRepository.save(epsServiceDomain);
        return new EpsServiceDTO(epsServiceDomain);
    }

    public EpsServiceDTO findById(UUID id) {
        return new EpsServiceDTO(epsServiceRepository.findById(id).orElseThrow(notFoundExceptionThrow("Сервис не найден")));
    }

    public List<EpsServiceDTO> getList(boolean forCabinet) {
        if (forCabinet) {
            return epsServiceRepository.findAllByStatusOrderByPositionAsc(EpsServiceStatusEnum.ACTIVE).stream().filter(EpsServiceDomain::isForCabinet).map(EpsServiceDTO::new).collect(Collectors.toList());
        }
        return epsServiceRepository.findAllByStatusOrderByPositionAsc(EpsServiceStatusEnum.ACTIVE).stream().map(EpsServiceDTO::new).collect(Collectors.toList());
    }

    public List<EpsServiceDTO> getList() {
        return this.getList(true);
    }

    @Transactional
    public EpsServiceDTO update(UUID id, EpsServiceCrudDTO dto) {
        if (dto.getStatus() == null) {
            dto.setStatus(EpsServiceStatusEnum.ACTIVE);
        }
        EpsServiceDomain epsServiceDomain = epsServiceRepository.findById(id).orElseThrow(notFoundExceptionThrow("Сервис не найден"));
        if (!epsServiceDomain.isFromApay()) {
            epsServiceMapper.partialUpdate(dto, epsServiceDomain);
        } else {
            epsServiceDomain.setPosition(dto.getPosition());
        }
        epsServiceDomain.setUpdatedDate(LocalDateTime.now());
        epsServiceRepository.save(epsServiceDomain);
        return new EpsServiceDTO(epsServiceDomain);
    }

    @Transactional
    public void delete(UUID id) {
        EpsServiceDomain epsServiceDomain = epsServiceRepository.findById(id).orElseThrow(notFoundExceptionThrow("Сервис не найден"));
        if (epsServiceDomain.isFromApay()) {
            throw badRequestExceptionThrow("Вы не можете удалить сервис a-pay").get();
        }
        epsServiceRepository.delete(epsServiceDomain);
    }

    public List<TerminalEpsServicesLookupDto> lookup(Long branchId) {
        return epsServiceRepository.findAll()
                .stream().sorted(Comparator.comparing(EpsServiceDomain::getPosition))
                .map(TerminalEpsServicesLookupDto::new)
                .toList();
    }

    @Scheduled(fixedRate = 10 * 60 * 1000, initialDelay = 10 * 1000)
    @Transactional
    public void sync() {
        List<EpsServiceDTO> providersList = apayClient.getPaymentProviders(0, 100);
        if (CollectionUtils.isEmpty(providersList)) {
            return;
        }
        try {
            log.info("EPS LIST: {}", objectMapper.writeValueAsString(providersList));
        } catch (JsonProcessingException e) {
            log.info("Writing object error: {}", e.getMessage());
        }

        Map<UUID, EpsServiceDomain> servicesMap = epsServiceRepository
            .findAll()
            .stream()
            .collect(Collectors.toMap(EpsServiceDomain::getId, Function.identity()));

        List<EpsServiceDomain> services = providersList
                .stream()
                .map(dto -> {
                    EpsServiceDomain entity = servicesMap.getOrDefault(dto.getId(), new EpsServiceDomain());
                    servicesMap.remove(dto.getId());
                    return epsServiceMapper.partialUpdate(dto, entity);
                })
                .collect(Collectors.toList());
        epsServiceRepository.saveAll(services);
        epsServiceRepository.deleteAll(servicesMap.values().stream().filter(EpsServiceDomain::isFromApay).collect(Collectors.toList()));
    }
}
