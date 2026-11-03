package uz.uzkassa.smartpos.service.fashion;

import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.domain.postgres.fashion.FashionIncomeOfProduct;
import uz.uzkassa.smartpos.domain.postgres.fashion.FashionIncomeOfProductDetail;
import uz.uzkassa.smartpos.domain.postgres.fashion.FashionIncomeOfProduct_;
import uz.uzkassa.smartpos.dto.fashion.*;
import uz.uzkassa.smartpos.enums.RegisterOperation;
import uz.uzkassa.smartpos.enums.SortTypeEnum;
import uz.uzkassa.smartpos.filter.FashionIncomeFilter;
import uz.uzkassa.smartpos.mappers.fashion.FashionIncomeOfProductMapper;
import uz.uzkassa.smartpos.rabbitmq.MqConstants;
import uz.uzkassa.smartpos.rabbitmq.producer.RabbitMqProducer;
import uz.uzkassa.smartpos.repository.postgres.company.BranchRepository;
import uz.uzkassa.smartpos.repository.postgres.fashion.FashionIncomeOfProductRepository;
import uz.uzkassa.smartpos.security.SecurityUtils;
import uz.uzkassa.smartpos.service.SmartposService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FashionIncomeService extends SmartposService {
    private final FashionIncomeOfProductRepository repository;
    private final FashionIncomeOfProductMapper mapper;
    private final BranchRepository branchRepository;
    private final RabbitMqProducer rabbitMqProducer;

    @Transactional
    public Long create(FashionIncomeCreateDto dto) {
        Branch branch = branchRepository.findById(dto.branchId()).orElseThrow(notFound(localizationService.getMessage("branch")));
        FashionIncomeOfProduct entity = mapper.toEntity(dto);
        entity.setCreatedById(SecurityUtils.getCurrentUserId());
        fillCalculatedFields(branch, entity);
        return entity.getId();
    }

    @Transactional
    public void update(Long id, FashionIncomeCreateDto dto) {
        Branch branch = branchRepository.findById(dto.branchId()).orElseThrow(notFound(localizationService.getMessage("branch")));
        var entity = repository.findById(id).orElseThrow(notFound(localizationService.getMessage("income.of.product")));
        List<FashionRegisterCreateDto> fashionRegisterDtos = getRegisterListForDeletion(entity);
        mapper.partialUpdate(dto, entity);
        fillCalculatedFields(branch, entity);
        fashionRegisterDtos.forEach(fashionRegisterDto -> rabbitMqProducer.send(fashionRegisterDto, MqConstants.FASHION_REGISTER_QUEUE));
    }

    private void fillCalculatedFields(Branch branch, FashionIncomeOfProduct entity) {
        entity.setLastModifiedById(SecurityUtils.getCurrentUserId());
        entity.setCompanyId(branch.getCompanyId());
        var registerList = new ArrayList<FashionRegisterCreateDto>();
        entity.getDetails().forEach(detail -> {
            detail.setBranchId(branch.getId());
            detail.setCityId(branch.getCityKeyId());
            detail.setTotalCostPrice(detail.getCostPrice().multiply(detail.getQty()));
            detail.setTotalSalesPrice(detail.getSalesPrice().multiply(detail.getQty()));
            detail.setProfit(detail.getTotalSalesPrice().subtract(detail.getTotalCostPrice()));
            detail.setProfitPercentage(detail.getProfit().divide(detail.getTotalCostPrice(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)));
            registerList.add(new FashionRegisterCreateDto(detail, branch.getCompanyId(), RegisterOperation.INCOME));
        });
        entity.setTotalCostPrice(entity.getDetails().stream().map(FashionIncomeOfProductDetail::getTotalCostPrice).reduce(BigDecimal::add).orElseThrow());
        entity.setTotalSalesPrice(entity.getDetails().stream().map(FashionIncomeOfProductDetail::getTotalSalesPrice).reduce(BigDecimal::add).orElseThrow());
        entity.setProfit(entity.getTotalSalesPrice().subtract(entity.getTotalCostPrice()));
        entity.setProfitPercentage(entity.getProfit().divide(entity.getTotalCostPrice(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)));
        repository.save(entity);
        registerList.forEach(registerDto -> rabbitMqProducer.send(registerDto, MqConstants.FASHION_REGISTER_QUEUE));
        rabbitMqProducer.sendWithDelayTime(entity.getId(), MqConstants.UPDATE_PRODUCT_PRICES_BY_INCOME_QUEUE, 1000L);
    }

    @Transactional
    public void delete(Long id) {
        var entity = repository.findById(id).orElseThrow(notFound(localizationService.getMessage("income.of.product")));
        repository.delete(entity);
        getRegisterListForDeletion(entity).forEach(fashionRegisterDto -> rabbitMqProducer.send(fashionRegisterDto, MqConstants.FASHION_REGISTER_QUEUE));
    }

    private List<FashionRegisterCreateDto> getRegisterListForDeletion(FashionIncomeOfProduct entity) {
        var result = new ArrayList<FashionRegisterCreateDto>();
        entity.getDetails().forEach(detail -> result.add(new FashionRegisterCreateDto(detail, entity.getCompanyId(), RegisterOperation.STOCK_ADJUSTMENT)));
        return result;
    }

    public FashionIncomeDto getById(Long id) {
        FashionIncomeOfProduct entity = repository.findById(id).orElseThrow(notFound(localizationService.getMessage("income.of.product")));
        FashionIncomeDto dto = mapper.toDto(entity);
        Map<Long, FashionIncomeDetailHelperDto> detailMap = new HashMap<>();
        entity.getDetails().forEach(detail -> {
            FashionIncomeDetailHelperDto detailDto = new FashionIncomeDetailHelperDto(detail);
            detailMap.putIfAbsent(detail.getProductId(), detailDto);
            detailDto = detailMap.get(detail.getProductId());
            if (detail.getProductItem() != null) {
                FashionIncomeColorGroupDto colorGroupDto = new FashionIncomeColorGroupDto(detail);
                detailDto.getColorGroupMap().putIfAbsent(colorGroupDto.getId(), colorGroupDto);
                FashionIncomeColorGroupDto colorGroup = detailDto.getColorGroupMap().get(colorGroupDto.getId());
                colorGroup.getItems().add(new FashionIncomeProductItemDto(detail));
            }
        });
        detailMap.forEach((k, detailDto) -> {
            detailDto.getColorGroupMap().forEach((s, colorGroupDto) -> {
                colorGroupDto.setQty(colorGroupDto.getItems().stream().map(FashionIncomeProductItemDto::getQty).reduce(BigDecimal::add).orElseThrow());
                colorGroupDto.setTotalCostPrice(colorGroupDto.getItems().stream().map(FashionIncomeBaseDto::getTotalCostPrice).reduce(BigDecimal::add).orElseThrow());
                colorGroupDto.setTotalSalesPrice(colorGroupDto.getItems().stream().map(FashionIncomeBaseDto::getTotalSalesPrice).reduce(BigDecimal::add).orElseThrow());
            });
            if (!detailDto.getColorGroupMap().isEmpty()) {
                detailDto.setColorGroups(new HashSet<>(detailDto.getColorGroupMap().values()));
                detailDto.setQty(detailDto.getColorGroups().stream().map(FashionIncomeColorGroupDto::getQty).reduce(BigDecimal::add).orElseThrow());
                detailDto.setTotalCostPrice(detailDto.getColorGroups().stream().map(FashionIncomeBaseDto::getTotalCostPrice).reduce(BigDecimal::add).orElseThrow());
                detailDto.setTotalSalesPrice(detailDto.getColorGroups().stream().map(FashionIncomeBaseDto::getTotalSalesPrice).reduce(BigDecimal::add).orElseThrow());
            }
        });
        dto.setDetails(new HashSet<>(detailMap.values()));
        dto.setTotalCostPrice(dto.getDetails().stream().map(FashionIncomeBaseDto::getTotalCostPrice).reduce(BigDecimal::add).orElseThrow());
        dto.setTotalSalesPrice(dto.getDetails().stream().map(FashionIncomeBaseDto::getTotalSalesPrice).reduce(BigDecimal::add).orElseThrow());
        return dto;
    }

    public Page<FashionIncomeListDto> findAllByFilter(FashionIncomeFilter filter) {
        if (filter.getCompanyId() == null) {
            filter.setCompanyId(SecurityUtils.getCurrentCompanyId());
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIds()));
        return repository.findAll((Specification<FashionIncomeOfProduct>) (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();
            predicates.add(criteriaBuilder.equal(root.get(FashionIncomeOfProduct_.companyId), filter.getCompanyId()));
            if (CollectionUtils.isNotEmpty(filter.getBranchIds())) {
                predicates.add(root.get(FashionIncomeOfProduct_.branchId).in(filter.getBranchIds()));
            }
            if (StringUtils.isNotBlank(filter.getOrderBy())) {
                if (SortTypeEnum.asc == filter.getSortOrder()) {
                    query.orderBy(criteriaBuilder.asc(root.get(filter.getOrderBy())));
                } else {
                    query.orderBy(criteriaBuilder.desc(root.get(filter.getOrderBy())));
                }
            } else {
                query.orderBy(criteriaBuilder.desc(root.get(FashionIncomeOfProduct_.id)));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, filter.getPageable()).map(mapper::toListDto);
    }

}
