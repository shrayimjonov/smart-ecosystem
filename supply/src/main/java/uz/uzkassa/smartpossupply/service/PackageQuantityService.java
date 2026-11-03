package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.domain.EntityCompositable;
import uz.uzkassa.smartpossupply.domain.EntityCompositeId;
import uz.uzkassa.smartpossupply.domain.PackageQuantity;
import uz.uzkassa.smartpossupply.domain.stock.StockProduct;
import uz.uzkassa.smartpossupply.dto.reconciliationact.PackageQuantityDTO;
import uz.uzkassa.smartpossupply.repository.PackageQuantityRepository;
import uz.uzkassa.smartpossupply.repository.StockProductRepository;

import java.util.List;
import java.util.Objects;

import static lombok.AccessLevel.PROTECTED;

@Slf4j
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PROTECTED, makeFinal = true)
@RequiredArgsConstructor
public class PackageQuantityService extends BaseService {
    PackageQuantityRepository packageQuantityRepository;
    StockProductRepository stockProductRepository;

    @Transactional
    public void create(EntityCompositable entityCompositable, List<PackageQuantityDTO> quantityDTOS) {
        for (PackageQuantityDTO packageQuantityDTO : quantityDTOS) {
            PackageQuantity packageQuantity = new PackageQuantity();
            packageQuantity.setId(entityCompositable.getCompositeId());
            packageQuantity.setQuantity(packageQuantityDTO.getQuantity());
            packageQuantity.setPackageType(packageQuantityDTO.getPackageType());
            packageQuantity.setQuantityInPackage(packageQuantityDTO.getQuantityInPackage());
            packageQuantityRepository.save(packageQuantity);
        }
    }

    public List<PackageQuantityDTO> getByCompositeId(Long id) {
        StockProduct stockProduct = stockProductRepository.findById(id).orElseThrow(validationService.notFoundExceptionThrow("1"));
        return getByCompositeId(stockProduct);
    }

    public List<PackageQuantityDTO> getByCompositeId(EntityCompositable entityCompositable) {
        EntityCompositeId compositeId = entityCompositable.getCompositeId();
        return packageQuantityRepository.findAllById(compositeId)
                .stream()
                .filter(Objects::nonNull)
                .filter(packageQuantity -> packageQuantity.getId() != null)
                .map(packageQuantity -> {
                    PackageQuantityDTO packageQuantityDTO = new PackageQuantityDTO();
                    packageQuantityDTO.setPackageType(packageQuantity.getPackageType());
                    packageQuantityDTO.setQuantity(packageQuantity.getQuantity());
                    packageQuantityDTO.setQuantityInPackage(packageQuantity.getQuantityInPackage());
                    return packageQuantityDTO;
                }).toList();
    }

    @Transactional
    public void deleteByCompositeId(EntityCompositable entityCompositable) {
        EntityCompositeId compositeId = entityCompositable.getCompositeId();
        List<PackageQuantity> allByIdEntityIdAndIdEntityType = packageQuantityRepository.findAllById(compositeId);
        if (!CollectionUtils.isEmpty(allByIdEntityIdAndIdEntityType)) {
            packageQuantityRepository.deleteAll(allByIdEntityIdAndIdEntityType);
        }
    }

    @Transactional
    public void saveTest(Long id, List<PackageQuantityDTO> quantityDTOS) {
        StockProduct stockProduct = stockProductRepository.findById(id).orElseThrow(validationService.notFoundExceptionThrow("1"));
        create(stockProduct, quantityDTOS);
    }

}
