package uz.uzkassa.smartpos.mappers;

import org.mapstruct.*;
import uz.uzkassa.smartpos.domain.postgres.warehouse.IncomeOfProduct;
import uz.uzkassa.smartpos.domain.postgres.warehouse.IncomeOfProductDetail;
import uz.uzkassa.smartpos.dto.integration.onec.*;
import uz.uzkassa.smartpos.dto.warehouse.v2.IncomeOfProductCreateDto;
import uz.uzkassa.smartpos.dto.warehouse.v2.IncomeOfProductDetailCreateDto;
import uz.uzkassa.smartpos.dto.warehouse.v2.IncomeOfProductDtoV2;
import uz.uzkassa.smartpos.dto.warehouse.v2.IncomeOfProductListDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING, uses = {IncomeOfProductDetailMapper.class, CommonMapper.class})
public interface IncomeOfProductMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "supplierId", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "toBranch", ignore = true)
    @Mapping(target = "facturaId", ignore = true)
    @Mapping(target = "invoice", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "incomeStatus", ignore = true)
    @Mapping(target = "stockOutId", ignore = true)
    IncomeOfProduct toEntity(IncomeOfProductCreateDto<? extends IncomeOfProductDetailCreateDto> incomeOfProductCreateDto);

    @AfterMapping
    default void linkIncomeOfProductDetails(@MappingTarget IncomeOfProduct incomeOfProduct) {
        incomeOfProduct.getIncomeOfProductDetails().forEach(incomeOfProductDetail -> incomeOfProductDetail.setIncomeOfProduct(incomeOfProduct));
    }
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "supplierId", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "toBranch", ignore = true)
    @Mapping(target = "facturaId", ignore = true)
    @Mapping(target = "invoice", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "incomeStatus", ignore = true)
    @Mapping(target = "stockOutId", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(IncomeOfProductCreateDto<IncomeOfProductDetailCreateDto> incomeOfProductCreateDto, @MappingTarget IncomeOfProduct incomeOfProduct);

    IncomeOfProductListDto toListDto(IncomeOfProduct incomeOfProduct);

    IncomeOfProductDtoV2 toDto(IncomeOfProduct incomeOfProduct);

    OneCIncomeResponse to1CDto(IncomeOfProduct incomeOfProduct);

    OneCIncomeListDto to1CListDto(IncomeOfProduct incomeOfProduct);

    @Mapping(target = "unitName", source = "unit.name")
    @Mapping(target = "product.id", source = "product.productId")
    @Mapping(target = "salePrice", source = "salesPrice")
    OneCIncomeDetailDto to1CProductDto(IncomeOfProductDetail oneCIncomeDetail);

    default IncomeOfProductCreateDto<? extends IncomeOfProductDetailCreateDto> toCreateDto(OneCIncome oneCIncome) {
        var dto = new IncomeOfProductCreateDto<IncomeOfProductDetailCreateDto>();
        dto.setToBranchId(oneCIncome.toBranchId());
        dto.setDocumentNo(oneCIncome.documentNo());
        dto.setDocumentDate(oneCIncome.documentDate());
        dto.setIncomeOfProductDetails(oneCIncome.incomeOfProductDetails().stream().map(this::toDetailDto).toList());
        return dto;
    }

    private IncomeOfProductDetailCreateDto toDetailDto(OneCIncomeDetail oneCIncomeDetails) {
        var dto = new IncomeOfProductDetailCreateDto();
        dto.setProductId(oneCIncomeDetails.productId());
        dto.setUnitId(oneCIncomeDetails.unitId());
        dto.setQty(oneCIncomeDetails.qty());
        dto.setCostPrice(oneCIncomeDetails.costPrice());
        dto.setSalesPrice(oneCIncomeDetails.salePrice());
        return dto;
    }
}