package uz.uzkassa.smartpos.service;

import com.google.common.collect.Sets;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpos.domain.postgres.catalog.CategoryBranch;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranch;
import uz.uzkassa.smartpos.domain.postgres.company.Branch_;
import uz.uzkassa.smartpos.domain.postgres.edocs.invoice.Invoice;
import uz.uzkassa.smartpos.domain.postgres.edocs.invoice.Invoice_;
import uz.uzkassa.smartpos.domain.postgres.warehouse.IncomeOfProduct;
import uz.uzkassa.smartpos.domain.postgres.warehouse.IncomeOfProduct_;
import uz.uzkassa.smartpos.dto.base.PageDto;
import uz.uzkassa.smartpos.dto.integration.onec.OneCIncome;
import uz.uzkassa.smartpos.dto.integration.onec.OneCIncomeListDto;
import uz.uzkassa.smartpos.dto.integration.onec.OneCIncomeResponse;
import uz.uzkassa.smartpos.dto.mq.RegisterPayload;
import uz.uzkassa.smartpos.dto.warehouse.v2.*;
import uz.uzkassa.smartpos.enums.IncomeOfProductStatus;
import uz.uzkassa.smartpos.enums.RegisterOperation;
import uz.uzkassa.smartpos.enums.SortTypeEnum;
import uz.uzkassa.smartpos.filter.IncomeOfProductFilterV2;
import uz.uzkassa.smartpos.filter.onec.OneCIncomeFilter;
import uz.uzkassa.smartpos.mappers.IncomeOfProductMapper;
import uz.uzkassa.smartpos.rabbitmq.MqConstants;
import uz.uzkassa.smartpos.rabbitmq.producer.RabbitMqProducer;
import uz.uzkassa.smartpos.repository.postgres.catalog.CategoryBranchRepository;
import uz.uzkassa.smartpos.repository.postgres.catalog.MxikRepository;
import uz.uzkassa.smartpos.repository.postgres.catalog.ProductBranchRepository;
import uz.uzkassa.smartpos.repository.postgres.common.CityRepository;
import uz.uzkassa.smartpos.repository.postgres.edocs.InvoiceRepository;
import uz.uzkassa.smartpos.repository.postgres.warehouse.IncomeOfProductDetailRepository;
import uz.uzkassa.smartpos.repository.postgres.warehouse.IncomeOfProductRepository;
import uz.uzkassa.smartpos.repository.postgres.warehouse.ProductForMarkingRepository;
import uz.uzkassa.smartpos.security.SecurityUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class IncomeOfProductServiceV2 extends SmartposService {
    private final IncomeOfProductMapper incomeOfProductMapper;
    private final IncomeOfProductRepository incomeOfProductRepository;
    private final RabbitMqProducer rabbitMqProducer;
    private final CategoryBranchRepository categoryBranchRepository;
    private final ProductBranchRepository productBranchRepository;
    private final MxikRepository mxikRepository;
    private final InvoiceRepository invoiceRepository;
    private final IdGeneratorService idGeneratorService;
    private final ProductService productService;
    private final CityRepository cityRepository;
    private final IncomeOfProductDetailRepository incomeOfProductDetailRepository;
    private final ProductForMarkingRepository productForMarkingRepository;
    private final RegisterService registerService;

    @Transactional
    public Long create(IncomeOfProductCreateDto<? extends IncomeOfProductDetailCreateDto> dto, String facturaId) {
        var branch = branchRepository.findById(dto.getToBranchId())
                .orElseThrow(notFound(localizationService.getMessage("branch")));
        var incomeOfProduct = incomeOfProductMapper.toEntity(dto);
        incomeOfProduct.getIncomeOfProductDetails().forEach(incomeOfProductDetail -> {
            incomeOfProductDetail.setBranchId(branch.getId());
            incomeOfProductDetail.setCityId(branch.getCityKeyId());
        });
        incomeOfProductRepository.save(incomeOfProduct);
        incomeOfProduct.setFacturaId(facturaId);
        return incomeOfProduct.getId();
    }

    @Transactional
    public void update(Long id, IncomeOfProductCreateDto<IncomeOfProductDetailCreateDto> dto) {
        var incomeOfProduct = incomeOfProductRepository.findById(id)
                .orElseThrow(notFound(localizationService.getMessage("income.of.product")));
        if (!IncomeOfProductStatus.DRAFT.equals(incomeOfProduct.getIncomeStatus())) {
            throw badRequest(localizationService.getMessage("income.of.product.change.not.draft")).get();
        }
        var branch = branchRepository.findById(dto.getToBranchId())
                .orElseThrow(notFound(localizationService.getMessage("branch")));
        incomeOfProductMapper.partialUpdate(dto, incomeOfProduct);
        incomeOfProduct.getIncomeOfProductDetails().forEach(incomeOfProductDetail -> {
            incomeOfProductDetail.setBranchId(branch.getId());
            incomeOfProductDetail.setCityId(branch.getCityKeyId());
        });
        incomeOfProductRepository.save(incomeOfProduct);
    }

    public Page<IncomeOfProductListDto> findAllByFilter(IncomeOfProductFilterV2 filter) {
        final boolean hasPermissionToAdmin = hasPermissionToAdmin();
        if (!hasPermissionToAdmin || filter.getCompanyId() == null) {
            filter.setCompanyId(getCurrentCompanyId());
        }
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
        return incomeOfProductRepository.findAll((Specification<IncomeOfProduct>) (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();
            if (CollectionUtils.isNotEmpty(filter.getBranchIdsWithBranchId())) {
                predicates.add(root.get(IncomeOfProduct_.toBranchId).in(filter.getBranchIdsWithBranchId()));
            } else {
                predicates.add(criteriaBuilder.equal(root.get(IncomeOfProduct_.toBranch).get(Branch_.companyId), filter.getCompanyId()));
            }
            if (filter.getFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(IncomeOfProduct_.createdDate), filter.getFrom()));
            }
            if (filter.getTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(IncomeOfProduct_.createdDate), filter.getTo()));
            }
            if (filter.getIncomeStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get(IncomeOfProduct_.status), filter.getIncomeStatus()));
            }
            if (StringUtils.isNotBlank(filter.getSearch())) {
                Join<IncomeOfProduct, Invoice> invoiceJoin = root.join(IncomeOfProduct_.invoice, JoinType.LEFT);
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(root.get(IncomeOfProduct_.id).as(String.class), filter.getSearchForQuery()),
                        criteriaBuilder.like(invoiceJoin.get(Invoice_.number), filter.getSearchForQuery())
                ));
            }
            if (StringUtils.isNotBlank(filter.getOrderBy())) {
                if (SortTypeEnum.desc.equals(filter.getSortOrder())) {
                    query.orderBy(criteriaBuilder.desc(root.get(filter.getOrderBy())));
                } else {
                    query.orderBy(criteriaBuilder.asc(root.get(filter.getOrderBy())));
                }
            } else {
                query.orderBy(criteriaBuilder.desc(root.get(IncomeOfProduct_.id)));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, filter.getPageable()).map(incomeOfProductMapper::toListDto);
    }

    public IncomeOfProductDtoV2 getById(Long id) {
        return incomeOfProductMapper.toDto(incomeOfProductRepository.findById(id)
                .orElseThrow(notFound(localizationService.getMessage("income.of.product"))));
    }

    @Transactional
    public void accept(Long id) {
        var incomeOfProduct = incomeOfProductRepository.findById(id)
                .orElseThrow(notFound(localizationService.getMessage("income.of.product")));
        incomeOfProduct.setIncomeStatus(IncomeOfProductStatus.APPROVED);
        incomeOfProductRepository.save(incomeOfProduct);
        incomeOfProduct.getIncomeOfProductDetails().forEach(incomeOfProductDetail -> rabbitMqProducer.sendWithType(new RegisterPayload(incomeOfProduct.getToBranchId(),
                null,
                incomeOfProductDetail.getProductId(),
                incomeOfProductDetail.getUnitId(),
                incomeOfProductDetail.getQty(),
                incomeOfProductDetail.getCostPrice(),
                incomeOfProductDetail.getSalesPrice(),
                incomeOfProductDetail.getMarkingCount(),
                RegisterOperation.INCOME
        ), MqConstants.REGISTER_QUEUE, "register_payload"));
        rabbitMqProducer.sendWithDelayTime(id, MqConstants.INCOME_BATCH_QUEUE, 3000L);
        Thread.startVirtualThread(() -> incomeOfProduct.getIncomeOfProductDetails().forEach(incomeOfProductDetail -> productService.setPriceExisting(incomeOfProductDetail.getSalesPrice(), incomeOfProductDetail.getProductId(), incomeOfProduct.getToBranchId())));
    }

    @Transactional
    public void cancel(Long id) {
        var incomeOfProduct = incomeOfProductRepository.findById(id)
                .orElseThrow(notFound(localizationService.getMessage("income.of.product")));
        incomeOfProduct.setIncomeStatus(IncomeOfProductStatus.CANCELED);
        incomeOfProductRepository.save(incomeOfProduct);
    }

    @Transactional
    public void delete(Long id) {
        var incomeOfProduct = incomeOfProductRepository.findById(id)
                .orElseThrow(notFound(localizationService.getMessage("income.of.product")));
        if (!IncomeOfProductStatus.DRAFT.equals(incomeOfProduct.getIncomeStatus())) {
            throw badRequest(localizationService.getMessage("income.of.product.delete.not.draft")).get();
        }
        incomeOfProductRepository.delete(incomeOfProduct);
    }

    @Transactional
    public Long createFromInvoice(IncomeOfProductCreateDto<IncomeDetailCreateFromInvoiceDto> dto) {
        final String TEMP_CATEGORY = "другие товары";
        Optional<CategoryBranch> optionalCategoryBranch = categoryBranchRepository.findFirstByBranch_IdAndNameAndEnabledIsTrue(dto.getToBranchId(), TEMP_CATEGORY);
        CategoryBranch categoryBranch;
        if (optionalCategoryBranch.isEmpty()) {
            CategoryBranch category = new CategoryBranch();
            category.setCategoryId(categoryBranchRepository.nextCategoryId());
            category.setBranchId(dto.getToBranchId());
            category.setName(TEMP_CATEGORY);
            categoryBranch = categoryBranchRepository.save(category);
        } else {
            categoryBranch = optionalCategoryBranch.get();
        }
        dto.getIncomeOfProductDetails().stream().filter(detail -> detail.getProductId() == null).forEach(detail -> {
            final InvoiceProductDto productDTO = detail.getProduct();
            ProductBranch product = new ProductBranch();
            product.setName(productDTO.getName());
            product.setExciseAmount(productDTO.getExciseAmount());
            product.setHasExcise(productDTO.getExciseAmount() != null);
            product.setBarcode(Optional.ofNullable(productDTO.getBarcode()).orElse(RandomStringUtils.randomNumeric(5)));
            product.setVatBarcode(productDTO.getVatBarcode());
            product.setUnitId(detail.getUnitId());
            product.setCategoryId(categoryBranch.getCategoryId());
            product.setBranchId(dto.getToBranchId());
            product.setCityId(getBranchCityId(dto.getToBranchId()));
            product.setProductId(idGeneratorService.generateId());
            product.setDefaultPackage(mxikRepository.findByMxik(product.getVatBarcode())
                    .orElseThrow(badRequest(localizationService.getMessage("ikpu.must.correct", product.getVatBarcode())))
                    .getDefaultPackage());
            if (CollectionUtils.isNotEmpty(detail.getMarkings())) {
                product.setHasMark(true);
            }
            product.setVatRate(detail.getProduct().getVatRate());
            product.setSalesPrice(detail.getSalesPrice() != null ? detail.getSalesPrice() : BigDecimal.ZERO);
            productBranchRepository.save(product);
            detail.setProductId(product.getProductId());
        });
        Invoice invoice = invoiceRepository.findById(dto.getInvoiceId())
                .orElseThrow(notFound(localizationService.getMessage("invoice")));
        return create(dto, invoice.getFacturaId());
    }

    @Transactional
    public void cleanup() {
        cityRepository.findAll(Sort.by("id")).forEach(city -> {
            log.info("==================================================================================");
            log.info("==================================================================================");
            log.info("Register cleanup started. City: {}, {}", city.getId(), city.getNameRu());
            try {
                incomeOfProductDetailRepository.cleanup(city.getId());
            } catch (Exception e) {
                log.error("Register cleanup failed. City: {}, {}", city.getId(), city.getNameRu());
            }
            log.info("Register cleanup finished");
            log.info("==================================================================================");
            log.info("==================================================================================");
        });
    }

    @Transactional
    public Long oneCIncome(@Valid OneCIncome oneCIncome) {
        var id = create(incomeOfProductMapper.toCreateDto(oneCIncome), null);
        accept(id);
        return id;
    }

    public OneCIncomeResponse findByIdFor1C(Long id) {
        return incomeOfProductMapper.to1CDto(incomeOfProductRepository.findById(id)
                .orElseThrow(notFound(localizationService.getMessage("income.of.product"))));
    }

    public PageDto<OneCIncomeListDto> findAllByFilter(OneCIncomeFilter filter) {
        Page<IncomeOfProduct> page = incomeOfProductRepository.findAll((Specification<IncomeOfProduct>) (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();
            predicates.add(criteriaBuilder.equal(root.get(IncomeOfProduct_.toBranch).get(Branch_.companyId), getCurrentCompanyId()));
            if (filter.getFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(IncomeOfProduct_.createdDate), filter.getFrom()));
            }
            if (filter.getTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(IncomeOfProduct_.createdDate), filter.getTo()));
            }
            if (StringUtils.isNotBlank(filter.getSearch())) {
                Join<IncomeOfProduct, Invoice> invoiceJoin = root.join(IncomeOfProduct_.invoice, JoinType.LEFT);
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(root.get(IncomeOfProduct_.id).as(String.class), filter.getSearchForQuery()),
                        criteriaBuilder.like(invoiceJoin.get(Invoice_.number), filter.getSearchForQuery())
                ));
            }
            if (StringUtils.isNotBlank(filter.getOrderBy())) {
                if (SortTypeEnum.desc.equals(filter.getSortOrder())) {
                    query.orderBy(criteriaBuilder.desc(root.get(filter.getOrderBy())));
                } else {
                    query.orderBy(criteriaBuilder.asc(root.get(filter.getOrderBy())));
                }
            } else {
                query.orderBy(criteriaBuilder.desc(root.get(IncomeOfProduct_.id)));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, filter.getPageable());
        return new PageDto<>(page.map(incomeOfProductMapper::to1CListDto));
    }


    @Transactional
    public void deleteIncomeOfProduct(Long id) {
        IncomeOfProduct incomeOfProduct = incomeOfProductRepository.findById(id).orElseThrow(notFound("Income of product not found with this id: " + id));
        productForMarkingRepository.deleteByIncomeOfProductId(id);
        incomeOfProduct.getIncomeOfProductDetails().forEach(detail -> registerService.doRegisterOperation(new RegisterPayload(
                incomeOfProduct.getToBranchId(),
                null,
                detail.getProductId(),
                detail.getUnitId(),
                detail.getQty(),
                detail.getCostPrice(),
                detail.getSalesPrice(),
                detail.getMarkingCount(),
                RegisterOperation.STOCK_ADJUSTMENT
        )));
        incomeOfProductRepository.delete(incomeOfProduct);
    }

}
