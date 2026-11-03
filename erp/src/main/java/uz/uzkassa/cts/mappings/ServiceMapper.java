package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.CtsService;
import uz.uzkassa.cts.dto.ctsservice.request.ServiceRequestDto;
import uz.uzkassa.cts.dto.ctsservice.response.ServiceResponseDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ServiceMapper {

    ServiceResponseDto toDto(CtsService entity);

    CtsService toEntity(ServiceRequestDto dto);

    void toEntity(ServiceRequestDto dto, @MappingTarget CtsService service);
}
