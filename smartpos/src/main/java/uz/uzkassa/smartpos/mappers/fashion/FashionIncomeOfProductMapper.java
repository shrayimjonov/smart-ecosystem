package uz.uzkassa.smartpos.mappers.fashion;

import org.mapstruct.*;
import uz.uzkassa.smartpos.domain.postgres.fashion.FashionIncomeOfProduct;
import uz.uzkassa.smartpos.dto.fashion.FashionIncomeCreateDto;
import uz.uzkassa.smartpos.dto.fashion.FashionIncomeDto;
import uz.uzkassa.smartpos.dto.fashion.FashionIncomeListDto;
import uz.uzkassa.smartpos.mappers.CommonMapper;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {CommonMapper.class, FashionIncomeOfProductDetailMapper.class})
public interface FashionIncomeOfProductMapper {
    FashionIncomeOfProduct toEntity(FashionIncomeCreateDto fashionIncomeCreateDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    FashionIncomeOfProduct partialUpdate(FashionIncomeCreateDto fashionIncomeCreateDto, @MappingTarget FashionIncomeOfProduct fashionIncomeOfProduct);

    @AfterMapping
    default void linkDetails(@MappingTarget FashionIncomeOfProduct incomeOfProduct) {
        incomeOfProduct.getDetails().forEach(detail -> detail.setIncomeOfProduct(incomeOfProduct));
    }

    @Mapping(target = "details", ignore = true)
    FashionIncomeDto toDto(FashionIncomeOfProduct fashionIncomeOfProduct);

    FashionIncomeListDto toListDto(FashionIncomeOfProduct fashionIncomeOfProduct);
}
