package uz.uzkassa.smartpos.service;

import com.google.common.collect.Sets;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.domain.postgres.warehouse.StockAdjustment;
import uz.uzkassa.smartpos.domain.postgres.warehouse.StockAdjustmentItem;
import uz.uzkassa.smartpos.dto.filter.StockAdjustmentFilter;
import uz.uzkassa.smartpos.dto.integration.agreement.ServiceType;
import uz.uzkassa.smartpos.dto.warehouse.StockAdjustmentDTO;
import uz.uzkassa.smartpos.dto.warehouse.StockAdjustmentItemDTO;
import uz.uzkassa.smartpos.enums.RegisterOperation;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.repository.postgres.warehouse.StockAdjustmentItemRepository;
import uz.uzkassa.smartpos.repository.postgres.warehouse.StockAdjustmentRepository;
import uz.uzkassa.smartpos.security.SecurityUtils;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockAdjustmentService extends SmartposService {
    private final StockAdjustmentRepository stockAdjustmentRepository;
    private final StockAdjustmentItemRepository stockAdjustmentItemRepository;
    private final RegisterService registerService;

    @Transactional
    public StockAdjustmentDTO createNew(@Valid StockAdjustmentDTO stockAdjustmentDTO) {
        final Long branchId = stockAdjustmentDTO.getBranch() != null ?
                stockAdjustmentDTO.getBranch().getId() : getCurrentBranchId();

        StockAdjustment stockAdjustment = new StockAdjustment();
        stockAdjustment.setBranchId(branchId);
        stockAdjustment.setReason(stockAdjustmentDTO.getReason());
        stockAdjustment.setDescription(stockAdjustmentDTO.getDescription());
        stockAdjustment.setUserId(getCurrentUserId());
        stockAdjustmentRepository.save(stockAdjustment);

        for (StockAdjustmentItemDTO adjustmentItemDTO : stockAdjustmentDTO.getStockAdjustmentItems()) {
            StockAdjustmentItem adjustmentItem = new StockAdjustmentItem();
            adjustmentItem.setQty(adjustmentItemDTO.getQty());
            adjustmentItem.setUnitId(adjustmentItemDTO.getUnitId());
            if (adjustmentItemDTO.getProduct() != null) {
                adjustmentItem.setProductId(adjustmentItemDTO.getProduct().getId());
            }
            adjustmentItem.setBranchId(branchId);
            adjustmentItem.setCityId(getBranchCityId(branchId));
            adjustmentItem.setStockAdjustmentId(stockAdjustment.getId());
            stockAdjustment.getStockAdjustmentItems().add(adjustmentItem);
        }

        stockAdjustmentRepository.save(stockAdjustment);

        registerService.decreaseAmount(branchId, null, stockAdjustment.getStockAdjustmentItems(), RegisterOperation.STOCK_ADJUSTMENT);
        stockAdjustmentDTO.setId(stockAdjustment.getId());

        return stockAdjustmentDTO;
    }

    public StockAdjustmentDTO getStockAdjustment(Long id) {
        StockAdjustment stockAdjustment = stockAdjustmentRepository.findById(id)
                .orElseThrow(notFound(localizationService.getMessage("stock.adjustment"), id));
        if (stockAdjustment.getBranch() != null) {
            checkForAccessToCompany(stockAdjustment.getBranch().getCompanyId());
        }
        return stockAdjustment.getDTO();
    }

    public Page<StockAdjustmentItemDTO> getAllAdjustmentItems(StockAdjustmentFilter filter) {
        final boolean hasPermissionToAdmin = hasPermissionToAdmin();
        if (!hasPermissionToAdmin || filter.getCompanyId() == null) {
            filter.setCompanyId(getCurrentCompanyId());
        }
        checkWarehousePermission(filter.getCompanyId());
        if (!hasPermissionToAdmin) {
            if (!isOwner() && !isBranchAdmin()) {
                filter.setBranchIds(Sets.newHashSet(getCurrentBranchId()));
            }
            if (isBranchAdmin() && CollectionUtils.isEmpty(filter.getBranchIdsWithBranchId())) {
                SecurityUtils.getCurrentBranchGroupId()
                        .ifPresentOrElse(groupId -> filter.setBranchGroupIds(Set.of(groupId)),
                                () -> filter.setBranchIds(Set.of(SecurityUtils.getCurrentBranchId()))
                        );
            }
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIdsWithBranchId()));
        return stockAdjustmentItemRepository.findAllByFilter(filter).map(StockAdjustmentItem::getDTO);
    }

    public void checkWarehousePermission(Long companyId) {
        Company company = companyRepository.findById(companyId).orElseThrow(notFound(localizationService.getMessage("company")));
        if ((!company.isFreeTrial() && !company.getPermissions().getOrDefault(ServiceType.WAREHOUSE, LocalDateTime.MIN).isAfter(LocalDateTime.now())) && !isAdmin() && !isOperator()) {
            throw new BadRequestException("Пожалуйста, купите и активируйте складскую услугу", 400);
        }
    }

    @Transactional
    public void cleanup() {
        log.info("==================================================================================");
        log.info("==================================================================================");
        log.info("Stock adjustment cleanup started");
        stockAdjustmentItemRepository.cleanup();
        log.info("Stock adjustment cleanup finished");
        log.info("==================================================================================");
        log.info("==================================================================================");
    }
}
