package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.BlackList;
import uz.uzkassa.cts.dto.blacklist.request.BlackListRequestDto;
import uz.uzkassa.cts.dto.blacklist.response.BlackListResponseDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BlackListMapper {

    BlackListResponseDto toDto(BlackList entity);

    BlackList toEntity(BlackListRequestDto dto);

    void toDomainFromDto(BlackListRequestDto dtoObject, @MappingTarget BlackList entity);
}
