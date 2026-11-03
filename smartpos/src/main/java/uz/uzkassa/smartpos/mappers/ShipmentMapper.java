package uz.uzkassa.smartpos.mappers;

import org.mapstruct.*;
import uz.uzkassa.smartpos.domain.postgres.warehouse.Shipment;
import uz.uzkassa.smartpos.dto.warehouse.ShipmentCreateDto;
import uz.uzkassa.smartpos.dto.warehouse.ShipmentDto;
import uz.uzkassa.smartpos.dto.warehouse.ShipmentListDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING, uses = {CommonMapper.class, ShipmentItemMapper.class})
public interface ShipmentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "number", ignore = true)
    @Mapping(target = "intNumber", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "invoiceId", ignore = true)
    @Mapping(target = "invoice", ignore = true)
    Shipment toEntity(ShipmentCreateDto shipmentDto);

    ShipmentDto toDto(Shipment shipment);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @InheritConfiguration(name = "toEntity")
    void partialUpdate(ShipmentCreateDto shipmentDto, @MappingTarget Shipment shipment);

    ShipmentListDto toListDto(Shipment shipment);

    @AfterMapping
    default void linkItems(@MappingTarget Shipment shipment) {
        shipment.getItems().forEach(item -> item.setShipment(shipment));
    }
}
