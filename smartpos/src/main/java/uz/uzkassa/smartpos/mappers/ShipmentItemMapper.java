package uz.uzkassa.smartpos.mappers;

import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.*;
import uz.uzkassa.smartpos.domain.postgres.warehouse.ShipmentItem;
import uz.uzkassa.smartpos.dto.warehouse.ShipmentItemCreateDto;
import uz.uzkassa.smartpos.dto.warehouse.ShipmentItemDto;
import uz.uzkassa.smartpos.utils.MarkingUtils;

import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING, uses = {CommonMapper.class})
public interface ShipmentItemMapper {
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "shipment", ignore = true)
    @Mapping(target = "cityId", ignore = true)
    @Mapping(target = "unit", ignore = true)
    @Mapping(target = "product", ignore = true)
    ShipmentItem toEntity(ShipmentItemCreateDto shipmentItemDto);

    ShipmentItemDto toDto(ShipmentItem shipmentItem);

    @AfterMapping
    default void afterMapping(ShipmentItem shipmentItem) {
        if (CollectionUtils.isNotEmpty(shipmentItem.getAggregations())) {
            shipmentItem.setAggregations(shipmentItem.getAggregations().stream().map(mark -> MarkingUtils.clearWithProductGroup(mark, MarkingUtils.getGroupCode(shipmentItem.getVatBarcode()))).collect(Collectors.toSet()));
        }
        if (CollectionUtils.isNotEmpty(shipmentItem.getMarkings())) {
            shipmentItem.setAggregations(shipmentItem.getMarkings().stream().map(mark -> MarkingUtils.clearWithProductGroup(mark, MarkingUtils.getGroupCode(shipmentItem.getVatBarcode()))).collect(Collectors.toSet()));
        }
    }
}
