package uz.uzkassa.smartpos.service.fashion;

import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uz.uzkassa.smartpos.domain.postgres.catalog.*;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.domain.postgres.fashion.ProductReservation;
import uz.uzkassa.smartpos.domain.postgres.fashion.ProductReservation_;
import uz.uzkassa.smartpos.dto.fashion.FashionRegisterCreateDto;
import uz.uzkassa.smartpos.dto.fashion.ProductReservationDetailDto;
import uz.uzkassa.smartpos.dto.fashion.ProductReservationDto;
import uz.uzkassa.smartpos.dto.fashion.ProductReservationListDto;
import uz.uzkassa.smartpos.dto.filter.ProductReservationFilter;
import uz.uzkassa.smartpos.enums.RegisterOperation;
import uz.uzkassa.smartpos.enums.ReservationStatus;
import uz.uzkassa.smartpos.mappers.fashion.ProductReservationMapper;
import uz.uzkassa.smartpos.repository.postgres.catalog.ProductBranchRepository;
import uz.uzkassa.smartpos.repository.postgres.catalog.ProductItemRepository;
import uz.uzkassa.smartpos.repository.postgres.company.CompanyRepository;
import uz.uzkassa.smartpos.repository.postgres.fashion.FashionRegisterRepository;
import uz.uzkassa.smartpos.repository.postgres.fashion.ProductReservationRepository;
import uz.uzkassa.smartpos.security.SecurityUtils;
import uz.uzkassa.smartpos.service.SmartposService;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductReservationService extends SmartposService {
    private final ProductReservationRepository repository;
    private final ProductReservationMapper mapper;
    private final CompanyRepository companyRepository;
    private final ProductBranchRepository productBranchRepository;
    private final ProductItemRepository productItemRepository;
    private final FashionRegisterService fashionRegisterService;
    private final FashionRegisterRepository fashionRegisterRepository;

    @Transactional
    public Long create(final ProductReservationDto dto) {
        final Company company = companyRepository.findById(SecurityUtils.getCurrentCompanyId())
                .orElseThrow(notFound(localizationService.getMessage("branch")));
        if (!company.isReservationEnabled()) {
            throw badRequest(localizationService.getMessage("reservation.not.enabled")).get();
        }
        final Long cityId = getBranchCityId(dto.getBranchId());
        ProductBranch productBranch = productBranchRepository.findById(new ProductBranchId(dto.getProductId(), dto.getBranchId(), cityId))
                .orElseThrow(notFound(localizationService.getMessage("product")));
        if (dto.getProductItemId() != null) {
            productItemRepository.findById(new ProductCompositeId(dto.getProductItemId(), dto.getBranchId()))
                    .orElseThrow(notFound(localizationService.getMessage("product")));
        }
        final ProductReservation entity = mapper.toEntity(dto);
        if (productBranch.getImage() != null) {
            entity.setImageUrl(productBranch.getImage().getUrl());
        }
        if (dto.getProductItemId() != null) {
            productItemRepository.findById(new ProductCompositeId(dto.getProductItemId(), dto.getBranchId()))
                    .ifPresentOrElse(productItem -> {
                        entity.setProductItemId(productItem.getGenericId());
                        if (productItem.getColorGroup() != null) {
                            productItem.getColorGroup().getImages().stream().filter(ColorGroupImage::isMain).findFirst().ifPresent(colorGroupImage -> {
                                if (colorGroupImage.getFile() != null) {
                                    entity.setImageUrl(colorGroupImage.getFile().getUrl());
                                }
                            });
                        }
                    }, () -> {
                        throw notFound(localizationService.getMessage("product")).get();
                    });
        }
        entity.setCityId(cityId);
        entity.setExpireDate(LocalDateTime.now().plusHours(dto.getHour()));
        repository.save(entity);
        FashionRegisterCreateDto registerDto = new FashionRegisterCreateDto();
        registerDto.setCompanyId(company.getId());
        registerDto.setBranchId(entity.getBranchId());
        registerDto.setCityId(cityId);
        registerDto.setProductId(entity.getProductId());
        registerDto.setUnitId(productBranch.getUnitId());
        registerDto.setQty(entity.getQty());
        registerDto.setCostPrice(productBranch.getSalesPrice());
        registerDto.setSalesPrice(productBranch.getSalesPrice());
        registerDto.setProductItemId(entity.getProductItemId());
        registerDto.setRegisterOperation(RegisterOperation.SALE);
        fashionRegisterService.save(registerDto);
        return entity.getId();
    }

    @Transactional
    public Long prolong(final Long id) {
        final ProductReservation entity = repository.findById(id)
                .orElseThrow(notFound("Product Reservation"));
        var status = entity.getStatus();
        entity.setHour(24);
        entity.setExpireDate(entity.getExpireDate().plusHours(24));
        entity.setStatus(ReservationStatus.RESERVED);
        repository.save(entity);
        if (status == ReservationStatus.EXPIRED) {
            FashionRegisterCreateDto registerDto = new FashionRegisterCreateDto();
            registerDto.setCompanyId(entity.getBranch().getCompanyId());
            registerDto.setBranchId(entity.getBranchId());
            registerDto.setCityId(entity.getCityId());
            registerDto.setProductId(entity.getProductId());
            registerDto.setUnitId(entity.getProductBranch().getUnitId());
            registerDto.setQty(entity.getQty());
            registerDto.setCostPrice(entity.getProductBranch().getSalesPrice());
            registerDto.setSalesPrice(entity.getProductBranch().getSalesPrice());
            registerDto.setProductItemId(entity.getProductItemId());
            registerDto.setRegisterOperation(RegisterOperation.SALE);
            fashionRegisterService.save(registerDto);
        }
        return id;
    }

    public Page<ProductReservationListDto> findAllByFilter(ProductReservationFilter filter) {
        if (filter.getCompanyId() == null) {
            filter.setCompanyId(SecurityUtils.getCurrentCompanyId());
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIds()));
        return repository.findAll((Specification<ProductReservation>) (root, query, criteriaBuilder) -> {
//            root.fetch(ProductReservation_.branch);
//            root.fetch(ProductReservation_.productBranch);
//            root.fetch(ProductReservation_.productItem);
//            root.fetch(ProductReservation_.productItem).fetch(ProductItem_.colorGroup);
            var predicates = new ArrayList<Predicate>();
            if (CollectionUtils.isNotEmpty(filter.getBranchIds())) {
                predicates.add(root.get(ProductReservation_.branchId).in(filter.getBranchIds()));
            }
            if (filter.getProductItemId() != null) {
                predicates.add(criteriaBuilder.equal(root.get(ProductReservation_.productItemId), filter.getProductItemId()));
            }
            if (filter.getProductId() != null) {
                predicates.add(criteriaBuilder.equal(root.get(ProductReservation_.productId), filter.getProductId()));
            }
            if (filter.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get(ProductReservation_.status), filter.getStatus()));
            }
            if (StringUtils.isNotBlank(filter.getSearch())) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get(ProductReservation_.phoneNumber)), filter.getSearchForQuery()),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get(ProductReservation_.productItem).get(ProductItem_.name)), filter.getSearchForQuery()),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get(ProductReservation_.productBranch).get(ProductBranch_.name)), filter.getSearchForQuery())
                ));
            }
            query.orderBy(criteriaBuilder.desc(root.get(ProductReservation_.id)));
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        }, filter.getPageable()).map(ProductReservationListDto::new);
    }

    public ProductReservationDetailDto findById(Long id) {
        ProductReservationDetailDto detailDto = repository.findById(id)
                .map(ProductReservationDetailDto::new)
                .orElseThrow(notFound("Product Reservation"));
        if (detailDto.getProductItem() != null) {
            detailDto.setStockAmount(fashionRegisterRepository.getStockAmountByBranchIdAndProductItemId(detailDto.getBranch().getId(), detailDto.getProductItem().getId()));
        } else {
            detailDto.setStockAmount(fashionRegisterRepository.getStockAmountByBranchIdAndProductId(detailDto.getBranch().getId(), detailDto.getProduct().getId()));
        }
        return detailDto;
    }

    @Scheduled(cron = "15 15 * * * *")
    public void checkExpiredReservations() {
        repository.findAll((Specification<ProductReservation>) (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.equal(root.get(ProductReservation_.status), ReservationStatus.RESERVED),
                criteriaBuilder.lessThan(root.get(ProductReservation_.expireDate), LocalDateTime.now())
        )).forEach(reservation -> {
            if (reservation.getExpireDate().isBefore(LocalDateTime.now())) {
                reservation.setStatus(ReservationStatus.EXPIRED);
                backToWarehouse(reservation);
            }
        });
    }

    @Transactional
    public void cancel(Long id) {
        final ProductReservation entity = repository.findById(id)
                .orElseThrow(notFound("Product Reservation"));
        entity.setStatus(ReservationStatus.CANCELED);
        backToWarehouse(entity);
    }

    @Transactional
    public void confirm(Long id) {
        final ProductReservation entity = repository.findById(id)
                .orElseThrow(notFound("Product Reservation"));
        entity.setStatus(ReservationStatus.CONFIRMED);
        backToWarehouse(entity);
    }

    private void backToWarehouse(ProductReservation entity) {
        repository.save(entity);
        FashionRegisterCreateDto registerDto = new FashionRegisterCreateDto();
        registerDto.setCompanyId(entity.getBranch().getCompanyId());
        registerDto.setBranchId(entity.getBranchId());
        registerDto.setCityId(entity.getCityId());
        registerDto.setProductId(entity.getProductId());
        registerDto.setUnitId(entity.getProductBranch().getUnitId());
        registerDto.setQty(entity.getQty());
        registerDto.setCostPrice(entity.getProductBranch().getSalesPrice());
        registerDto.setSalesPrice(entity.getProductBranch().getSalesPrice());
        registerDto.setProductItemId(entity.getProductItemId());
        registerDto.setRegisterOperation(RegisterOperation.BACK);
        fashionRegisterService.save(registerDto);
    }
}
