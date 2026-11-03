package uz.uzkassa.smartpos.mappers;

import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.*;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranch;
import uz.uzkassa.smartpos.dto.catalog.Product1CCreateDto;
import uz.uzkassa.smartpos.dto.catalog.ProductCreateDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING, uses = {ProductUnitMapper.class})
public interface ProductBranchMapper {
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "branchId", ignore = true)
    @Mapping(target = "branch", ignore = true)
    @Mapping(target = "cityId", ignore = true)
    @Mapping(target = "city", ignore = true)
    @Mapping(target = "unit", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "exciseAmount", ignore = true)
    @Mapping(target = "hasExcise", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "creditEnabled", ignore = true)
    @Mapping(target = "creditSettings", ignore = true)
    @Mapping(target = "advanceEnabled", ignore = true)
    @Mapping(target = "advanceSettings", ignore = true)
    ProductBranch toEntity(ProductCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @InheritConfiguration(name = "toEntity")
    void partialUpdate(ProductCreateDto dto, @MappingTarget ProductBranch productBranch);

    @AfterMapping
    default void linkProductUnits(@MappingTarget ProductBranch productBranch) {
        if (CollectionUtils.size(productBranch.getUnits()) > 1) {
            productBranch.getUnits().forEach(productUnit -> {
                productUnit.setBranchId(productBranch.getBranchId());
                productUnit.setCityId(productBranch.getCityId());
            });
        } else {
            if (CollectionUtils.isNotEmpty(productBranch.getUnits())) {
                productBranch.getUnits().clear();
            }
        }
    }

    @Mapping(target = "unitId", source = "unit.id")
    @Mapping(target = "categoryId", source = "categoryDTO.id")
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "attributes", ignore = true)
    @Mapping(target = "image", ignore = true)
    ProductCreateDto toCreateDto(Product1CCreateDto dto);
}