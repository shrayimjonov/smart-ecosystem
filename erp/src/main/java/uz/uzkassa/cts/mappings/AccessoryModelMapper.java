package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.AccessoryModel;
import uz.uzkassa.cts.dto.accessorymodel.request.AccessoryModelCreateRequestDto;
import uz.uzkassa.cts.dto.accessorymodel.request.AccessoryModelUpdateRequestDto;
import uz.uzkassa.cts.dto.accessorymodel.response.AccessoryModelResponseDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccessoryModelMapper {

    AccessoryModelResponseDto toDto(AccessoryModel entity);
    AccessoryModel toEntity(AccessoryModelCreateRequestDto dto);
    void toEntity(AccessoryModelUpdateRequestDto dto, @MappingTarget AccessoryModel entity);
}
