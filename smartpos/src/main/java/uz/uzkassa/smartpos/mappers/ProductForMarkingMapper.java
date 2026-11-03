package uz.uzkassa.smartpos.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.smartpos.domain.postgres.warehouse.ProductForMarking;
import uz.uzkassa.smartpos.dto.warehouse.MarkingDTO;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductForMarkingMapper {
    MarkingDTO toDto(ProductForMarking productForMarking);
}
