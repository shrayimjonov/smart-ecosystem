package uz.uzkassa.smartpos.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.smartpos.domain.postgres.warehouse.IncomeOfProductDetail;
import uz.uzkassa.smartpos.dto.warehouse.v2.IncomeOfProductDetailCreateDto;
import uz.uzkassa.smartpos.dto.warehouse.v2.IncomeOfProductDetailDtoV2;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING, uses = {CommonMapper.class})
public interface IncomeOfProductDetailMapper {
    @Mapping(target = "incomeOfProduct", ignore = true)
    @Mapping(target = "unit", ignore = true)
    @Mapping(target = "branchId", ignore = true)
    @Mapping(target = "cityId", ignore = true)
    @Mapping(target = "product", ignore = true)
    IncomeOfProductDetail toEntity(IncomeOfProductDetailCreateDto incomeOfProductDetailCreateDto);

    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productVatBarcode", source = "product.vatBarcode")
    @Mapping(target = "productSalesPrice", source = "product.salesPrice")
    @Mapping(target = "productVatRate", source = "product.vatRate")
    @Mapping(target = "productHasMark", source = "product.hasMark")
    @Mapping(target = "unit.nameRu", source = "unit.name")
    IncomeOfProductDetailDtoV2 toDto(IncomeOfProductDetail incomeOfProductDetail);
}