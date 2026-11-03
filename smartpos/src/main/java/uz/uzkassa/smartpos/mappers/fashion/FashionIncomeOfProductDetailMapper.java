package uz.uzkassa.smartpos.mappers.fashion;

import org.mapstruct.*;
import uz.uzkassa.smartpos.domain.postgres.fashion.FashionIncomeOfProductDetail;
import uz.uzkassa.smartpos.dto.fashion.FashionIncomeDetailCreateDto;
import uz.uzkassa.smartpos.dto.fashion.FashionIncomeDetailDto;
import uz.uzkassa.smartpos.mappers.CommonMapper;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = CommonMapper.class)
public interface FashionIncomeOfProductDetailMapper {
    FashionIncomeOfProductDetail toEntity(FashionIncomeDetailCreateDto fashionIncomeDetailCreateDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    FashionIncomeOfProductDetail partialUpdate(FashionIncomeDetailCreateDto fashionIncomeDetailCreateDto, @MappingTarget FashionIncomeOfProductDetail fashionIncomeOfProductDetail);

    FashionIncomeDetailDto toDto(FashionIncomeOfProductDetail fashionIncomeOfProductDetail);
}
