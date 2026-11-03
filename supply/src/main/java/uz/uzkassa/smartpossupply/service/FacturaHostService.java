package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.factura.FacturaHost;
import uz.uzkassa.smartpossupply.dto.factura.supply.FacturaHostDTO;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.repository.FacturaHostRepository;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FacturaHostService extends BaseService {

    FacturaHostRepository facturaHostRepository;
    CacheService cacheService;

    public List<FacturaHostDTO> getList() {
        return facturaHostRepository.findAllOrderById().stream().map(facturaHost -> {
            FacturaHostDTO facturaHostDTO = new FacturaHostDTO();
            facturaHostDTO.setId(facturaHost.getId());
            facturaHostDTO.setHost(facturaHost.getHost());
            facturaHostDTO.setActive(facturaHost.isActive());
            return facturaHostDTO;
        }).toList();
    }

    @Transactional
    public void update(Long id) {
        facturaHostRepository.updateAll();
        FacturaHost facturaHost = facturaHostRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(FacturaHost.class.getSimpleName(), "id", id));
        facturaHost.setActive(true);
        facturaHostRepository.save(facturaHost);
        cacheService.clearByEntityType(EntityTypeEnum.FACTURA_HOST);
    }
}
