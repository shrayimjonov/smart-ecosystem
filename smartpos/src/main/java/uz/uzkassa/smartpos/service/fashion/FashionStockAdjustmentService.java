package uz.uzkassa.smartpos.service.fashion;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranch;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranchId;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductCompositeId;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.domain.postgres.warehouse.FashionStockAdjustmentItem;
import uz.uzkassa.smartpos.domain.postgres.warehouse.FashionStockAdjustmentItem_;
import uz.uzkassa.smartpos.dto.fashion.FashionRegisterCreateDto;
import uz.uzkassa.smartpos.dto.fashion.FashionStockAdjustmentItemDto;
import uz.uzkassa.smartpos.dto.warehouse.fashion.FashionStockAdjustmentCreateDto;
import uz.uzkassa.smartpos.dto.warehouse.fashion.FashionStockAdjustmentItemCreateDto;
import uz.uzkassa.smartpos.enums.RegisterOperation;
import uz.uzkassa.smartpos.filter.FashionRegisterFilter;
import uz.uzkassa.smartpos.rabbitmq.MqConstants;
import uz.uzkassa.smartpos.rabbitmq.producer.RabbitMqProducer;
import uz.uzkassa.smartpos.repository.postgres.catalog.ProductBranchRepository;
import uz.uzkassa.smartpos.repository.postgres.catalog.ProductItemRepository;
import uz.uzkassa.smartpos.repository.postgres.common.UnitRepository;
import uz.uzkassa.smartpos.repository.postgres.company.BranchRepository;
import uz.uzkassa.smartpos.repository.postgres.fashion.FashionStockAdjustmentItemRepository;
import uz.uzkassa.smartpos.security.SecurityUtils;
import uz.uzkassa.smartpos.service.SmartposService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FashionStockAdjustmentService extends SmartposService {
    private final FashionStockAdjustmentItemRepository repository;
    private final BranchRepository branchRepository;
    private final ProductBranchRepository productBranchRepository;
    private final ProductItemRepository productItemRepository;
    private final UnitRepository unitRepository;
    private final RabbitMqProducer rabbitMqProducer;

    @Transactional
    public void create(FashionStockAdjustmentCreateDto dto) {
        Branch branch = branchRepository.findById(dto.getBranchId()).orElseThrow(notFound(localizationService.getMessage("branch")));
        List<FashionRegisterCreateDto> registerCreateDtos = new ArrayList<>();
        List<FashionStockAdjustmentItem> list = dto.getItems().stream().map(createDto -> {
            ProductBranch productBranch = productBranchRepository.findById(new ProductBranchId(createDto.getProductId(), dto.getBranchId(), branch.getCityKeyId()))
                    .orElseThrow(notFound(localizationService.getMessage("product")));
            FashionStockAdjustmentItem item = new FashionStockAdjustmentItem();
            item.setCreatedById(SecurityUtils.getCurrentUserId());
            item.setCreatedAt(LocalDateTime.now());
            item.setCompanyId(branch.getCompanyId());
            item.setProductId(createDto.getProductId());
            item.setBranchId(dto.getBranchId());
            item.setCityId(productBranch.getCityId());
            if (createDto.getProductItemId() != null) {
                productItemRepository.findById(new ProductCompositeId(createDto.getProductItemId(), dto.getBranchId()))
                        .ifPresentOrElse(productItem -> item.setProductItemId(createDto.getProductItemId()),
                                () -> notFound(localizationService.getMessage("productItem")));
            }
            unitRepository.findById(createDto.getUnitId()).ifPresentOrElse(unit -> item.setUnitId(unit.getId()),
                    () -> notFound(localizationService.getMessage("unit")).get());
            item.setQty(createDto.getQty());
            item.setReason(dto.getReason());
            registerCreateDtos.add(getFashionRegisterCreateDto(createDto, branch, productBranch));
            return item;
        }).toList();
        repository.saveAll(list);
        registerCreateDtos.forEach(registerCreateDto -> rabbitMqProducer.send(registerCreateDto, MqConstants.FASHION_REGISTER_QUEUE));
    }

    public Page<FashionStockAdjustmentItemDto> findAllByFilter(FashionRegisterFilter filter) {
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIds()));
        return repository.findAll((Specification<FashionStockAdjustmentItem>) (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();
            predicates.add(criteriaBuilder.equal(root.get(FashionStockAdjustmentItem_.companyId), SecurityUtils.getCurrentCompanyId()));
            if (CollectionUtils.isNotEmpty(filter.getBranchIds())) {
                predicates.add(root.get(FashionStockAdjustmentItem_.branchId).in(filter.getBranchIds()));
            }
            if (filter.getProductId() != null) {
                predicates.add(criteriaBuilder.equal(root.get(FashionStockAdjustmentItem_.productId), filter.getProductId()));
            }
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        }, filter.getPageable()).map(FashionStockAdjustmentItemDto::new);
    }

    @NotNull
    private FashionRegisterCreateDto getFashionRegisterCreateDto(FashionStockAdjustmentItemCreateDto dto, Branch branch, ProductBranch productBranch) {
        FashionRegisterCreateDto payload = new FashionRegisterCreateDto();
        payload.setCompanyId(branch.getCompanyId());
        payload.setBranchId(branch.getId());
        payload.setCityId(productBranch.getCityId());
        payload.setProductId(dto.getProductId());
        payload.setUnitId(dto.getUnitId());
        payload.setQty(dto.getQty());
        payload.setCostPrice(productBranch.getSalesPrice());
        payload.setSalesPrice(productBranch.getSalesPrice());
        if (dto.getProductItemId() != null) {
            payload.setProductItemId(dto.getProductItemId());
        }
        payload.setRegisterOperation(RegisterOperation.STOCK_ADJUSTMENT);
        return payload;
    }
}
