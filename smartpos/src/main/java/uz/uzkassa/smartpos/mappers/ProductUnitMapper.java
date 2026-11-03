package uz.uzkassa.smartpos.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductUnit;
import uz.uzkassa.smartpos.dto.catalog.ProductUnitDTO;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductUnitMapper {
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "unit", ignore = true)
    @Mapping(target = "branchId", ignore = true)
    @Mapping(target = "cityId", ignore = true)
    @Mapping(target = "productBranch", ignore = true)
    ProductUnit toEntity(ProductUnitDTO productUnitDTO);
}