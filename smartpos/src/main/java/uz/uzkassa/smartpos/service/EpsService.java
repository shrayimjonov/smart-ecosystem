package uz.uzkassa.smartpos.service;

import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uz.uzkassa.smartpos.constants.CacheConstants;
import uz.uzkassa.smartpos.constants.TokenType;
import uz.uzkassa.smartpos.domain.postgres.EpsServices;
import uz.uzkassa.smartpos.domain.postgres.FileUpload;
import uz.uzkassa.smartpos.dto.EpsServicesCrudDto;
import uz.uzkassa.smartpos.dto.EpsServicesDto;
import uz.uzkassa.smartpos.dto.EpsServicesLookupDto;
import uz.uzkassa.smartpos.enums.EPSServicesStatusEnum;
import uz.uzkassa.smartpos.enums.EntityType;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.exceptions.EntityNotFound;
import uz.uzkassa.smartpos.integration.apay.ApayClient;
import uz.uzkassa.smartpos.mappers.EpsServicesMapper;
import uz.uzkassa.smartpos.repository.postgres.EpsServicesRepository;
import uz.uzkassa.smartpos.repository.postgres.FileUploadRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EpsService extends SmartposService {
    private final ApayClient apayClient;
    private final EpsServicesRepository epsServicesRepository;
    private final EpsServicesMapper epsServicesMapper;
    private final ReceiptService receiptService;
    private final TokenStoreService tokenStoreService;
    private final FileUploadRepository fileUploadRepository;

    public EpsServicesDto create(EpsServicesCrudDto dto) {
        if (dto.getStatus() == null) {
            dto.setStatus(EPSServicesStatusEnum.ACTIVE);
        }
        EpsServices epsServices = epsServicesMapper.toEntity(dto);
        epsServices.setId(UUID.randomUUID());
        epsServices.setFromApay(false);
        epsServices.setCreatedDate(LocalDateTime.now());
        epsServices.setUpdatedDate(LocalDateTime.now());
        epsServicesRepository.save(epsServices);
        FileUpload fileUpload = null;
        if (epsServices.getFile() != null) {
            fileUpload = fileUploadRepository.getReferenceById(epsServices.getFileId());
        }
        if (fileUpload != null) {
            fileUpload.setEntityType(EntityType.EPS_SERVICES);
            fileUpload.setEntityId(null);
            fileUploadRepository.save(fileUpload);
        }
        clearCache(CacheConstants.EPS_PROVIDERS_LOOKUP);
        return new EpsServicesDto(epsServices);
    }

    public EpsServicesDto findById(UUID id) {
        return new EpsServicesDto(epsServicesRepository.findById(id).orElseThrow(() -> new EntityNotFound("Сервис не найден")));
    }

    public List<EpsServicesDto> findAll() {
        return epsServicesRepository.findAll().stream().map(EpsServicesDto::new).collect(Collectors.toList());
    }

    public EpsServicesDto update(UUID id, EpsServicesCrudDto dto) {
        if (dto.getStatus() == null) {
            dto.setStatus(EPSServicesStatusEnum.ACTIVE);
        }
        EpsServices epsServices = epsServicesRepository.findById(id).orElseThrow(() -> new EntityNotFound("Сервис не найден"));
        if (!epsServices.isFromApay()) {
            epsServicesMapper.partialUpdate(dto, epsServices);
        } else {
            epsServices.setPosition(dto.getPosition());
        }
        epsServices.setUpdatedDate(LocalDateTime.now());
        epsServicesRepository.save(epsServices);
        clearCache(CacheConstants.EPS_PROVIDERS_LOOKUP);
        return new EpsServicesDto(epsServices);
    }

    public void delete(UUID id) {
        EpsServices epsServices = epsServicesRepository.findById(id).orElseThrow(() -> new EntityNotFound("Сервис не найден"));
        if (epsServices.isFromApay()) throw new BadRequestException("Вы не можете удалить сервис a-pay");
        clearCache(CacheConstants.EPS_PROVIDERS_LOOKUP);
        epsServicesRepository.delete(epsServices);
    }

    @Cacheable(cacheNames = CacheConstants.EPS_PROVIDERS_LOOKUP, key = "{#branchId}")
    public List<EpsServicesLookupDto> lookup(Long branchId) {
        List<EpsServicesLookupDto> providers = receiptService.paymentProviders(branchId);
        List<EpsServices> all = epsServicesRepository.findAll();
        List<EpsServices> defaultProviders = all.stream()
                .filter(epsServices -> epsServices.isDefaultService() && EPSServicesStatusEnum.ACTIVE.equals(epsServices.getStatus()))
                .toList();
        Map<UUID, EpsServices> servicesMap = all.stream().collect(Collectors.toMap(EpsServices::getId, Function.identity()));
        providers.forEach(dto -> {
            if (servicesMap.containsKey(dto.getId())) {
                EpsServices epsServices = servicesMap.get(dto.getId());
                dto.setPosition(epsServices.getPosition());
                dto.setQrMethod(epsServices.getQrMethod());
            }
        });
        providers.addAll(defaultProviders.stream().map(EpsServicesLookupDto::new).toList());
        return providers;
    }

    @Scheduled(fixedRate = 60 * 60 * 1000, initialDelay = 0)
    public void sync() {
        Page<EpsServicesDto> providersList = apayClient.paymentProvidersList(tokenStoreService.getToken(TokenType.APAY), 0, 100);
        if (providersList == null || !providersList.hasContent()) return;
        Map<UUID, EpsServices> servicesMap = epsServicesRepository.findAll()
                .stream().collect(Collectors.toMap(EpsServices::getId, Function.identity()));
        List<EpsServices> services = providersList.map(dto -> {
            EpsServices entity = servicesMap.getOrDefault(dto.getId(), new EpsServices());
            return epsServicesMapper.partialUpdate(dto, entity);
        }).stream().collect(Collectors.toList());
        epsServicesRepository.saveAll(services);
    }
}
