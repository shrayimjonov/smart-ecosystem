package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.RentActAccessory;
import uz.uzkassa.cts.dto.rentactaccessory.response.RentActAccessoryResponseDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RentActAccessoryMapper {

    @Mapping(source = "accessory.model.name", target = "modelName")
    @Mapping(source = "accessory.model.description", target = "modelDescription")
    @Mapping(source = "accessory.name", target = "accessoryName")
    RentActAccessoryResponseDto toDto(RentActAccessory entity);
}
