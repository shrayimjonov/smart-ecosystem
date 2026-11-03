package uz.uzkassa.smartpos.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpos.dto.catalog.BatchUpdateDTO;
import uz.uzkassa.smartpos.repository.postgres.catalog.ProductBranchRepository;
import uz.uzkassa.smartpos.repository.postgres.catalog.ProductColorGroupRepository;
import uz.uzkassa.smartpos.repository.postgres.catalog.ProductItemRepository;
import uz.uzkassa.smartpos.repository.postgres.catalog.ProductPriceHistoryRepository;
import uz.uzkassa.smartpos.repository.postgres.common.CityRepository;
import uz.uzkassa.smartpos.repository.postgres.fashion.FashionBalanceHistoryRepository;
import uz.uzkassa.smartpos.repository.postgres.fashion.FashionRegisterRepository;
import uz.uzkassa.smartpos.repository.postgres.fashion.FashionStockAdjustmentItemRepository;
import uz.uzkassa.smartpos.repository.postgres.warehouse.BalanceHistoryRepository;
import uz.uzkassa.smartpos.repository.postgres.warehouse.RegisterRepository;
import uz.uzkassa.smartpos.repository.postgres.warehouse.StockAdjustmentItemRepository;
import uz.uzkassa.smartpos.repository.postgres.warehouse.TransferDetailRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductBranchService extends SmartposService {
    private final ProductBranchRepository productBranchRepository;
    private final RegisterRepository registerRepository;
    private final BalanceHistoryRepository balanceHistoryRepository;
    private final ProductPriceHistoryRepository productPriceHistoryRepository;
    private final StockAdjustmentItemRepository stockAdjustmentItemRepository;
    private final TransferDetailRepository transferDetailRepository;
    private final CityRepository cityRepository;
    private final FashionRegisterRepository fashionRegisterRepository;
    private final FashionBalanceHistoryRepository fashionBalanceHistoryRepository;
    private final FashionStockAdjustmentItemRepository fashionStockAdjustmentItemRepository;
    private final ProductColorGroupRepository productColorGroupRepository;
    private final ProductItemRepository productItemRepository;

    public boolean isProductChanged(LocalDateTime lastSyncDate) {
        final Long branchId = getCurrentBranchId();
        final Long cityId = getBranchCityId(branchId);
        return productBranchRepository.existsProductBranchByCityIdAndBranchIdAndLastModifiedDateAfter(cityId, branchId, lastSyncDate);
    }

    public void vatUpdate(BatchUpdateDTO dto) {
        final Long cityId = getBranchCityId(dto.getBranchId());
        if (dto.getCategoryId() != null) {
            productBranchRepository.categoryVatUpdate(cityId, dto.getCategoryId(), dto.getBranchId(), dto.getVatRate());
        } else {
            productBranchRepository.branchVatUpdate(cityId, dto.getBranchId(), dto.getVatRate());
        }
    }

    public void markUpdate(BatchUpdateDTO dto) {
        final Long cityId = getBranchCityId(dto.getBranchId());
        if (dto.getCategoryId() != null) {
            productBranchRepository.categoryMarkUpdate(cityId, dto.getCategoryId(), dto.getBranchId(), dto.isHasMark());
        } else {
            productBranchRepository.branchMarkUpdate(cityId, dto.getBranchId(), dto.isHasMark());
        }
    }

    public void committentTinUpdate(BatchUpdateDTO dto) {
        final Long cityId = getBranchCityId(dto.getBranchId());
        if (dto.getCategoryId() != null) {
            productBranchRepository.categoryCommittentUpdated(cityId, dto.getCategoryId(), dto.getBranchId(), dto.getCommittentTin());
        } else {
            productBranchRepository.branchCommittentUpdated(cityId, dto.getBranchId(), dto.getCommittentTin());
        }
    }

    public void productOriginUpdate(BatchUpdateDTO dto) {
        final Long cityId = getBranchCityId(dto.getBranchId());
        if (dto.getCategoryId() != null) {
            productBranchRepository.categoryProductOriginUpdated(cityId, dto.getCategoryId(), dto.getBranchId(), dto.getOrigin().getCode());
        } else {
            productBranchRepository.branchProductOriginUpdated(cityId, dto.getBranchId(), dto.getOrigin().getCode());
        }
    }

    public void deleteAllProductBranchReferences(Long id, Long branchId) {
        registerRepository.deleteByBranchIdAndProductId(branchId, id);
        balanceHistoryRepository.deleteByProductIdAndBranchId(id, branchId);
        productPriceHistoryRepository.deleteAllByProductIdAndBranchId(id, branchId);
        stockAdjustmentItemRepository.deleteAllByProductIdAndBranchId(id, branchId);
        transferDetailRepository.deleteAllByProductIdAndBranchId(id, branchId);
        fashionRegisterRepository.deleteByBranchIdAndProductId(branchId, id);
        fashionBalanceHistoryRepository.deleteByProductIdAndBranchId(id, branchId);
        productColorGroupRepository.deleteByProductIdAndBranchId(id, branchId);
        productItemRepository.deleteByProductIdAndBranchId(id, branchId);
        fashionStockAdjustmentItemRepository.deleteAllByProductIdAndBranchId(id, branchId);
    }

    @Transactional
    public void cleanup() {
        cityRepository.findAll(Sort.by("id")).forEach(city -> {
            log.info("==================================================================================");
            log.info("==================================================================================");
            log.info("Product Branch cleanup started. City: {}, {}", city.getId(), city.getNameRu());
            try {
                productBranchRepository.cleanup(city.getId());
            } catch (Exception e) {
                log.error("Product branch cleanup failed. City: {}, {}", city.getId(), city.getNameRu());
            }
            log.info("Product Branch cleanup finished");
            log.info("==================================================================================");
            log.info("==================================================================================");
        });
    }
}
