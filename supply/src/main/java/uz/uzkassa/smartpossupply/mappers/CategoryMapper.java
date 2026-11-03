package uz.uzkassa.smartpossupply.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.smartpossupply.domain.category.Category;
import uz.uzkassa.smartpossupply.dto.category.CategoryDTO;
import uz.uzkassa.smartpossupply.dto.category.CategoryDetailDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper extends BaseMapper<Category, CategoryDTO, CategoryDetailDTO> {
}
