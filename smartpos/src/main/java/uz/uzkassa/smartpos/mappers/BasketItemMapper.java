package uz.uzkassa.smartpos.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.smartpos.domain.postgres.warehouse.BasketItem;
import uz.uzkassa.smartpos.dto.BasketItemDTO;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING)
public interface BasketItemMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "selected", ignore = true)
    @Mapping(target = "company", ignore = true)
    BasketItem toEntity(BasketItemDTO basketItemDTO);

    BasketItemDTO toDto(BasketItem basketItem);
}