package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.cases.CaseFile;
import uz.uzkassa.cts.dto.casefile.response.CaseFileResponseDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CaseFileMapper {

    @Mapping(source = "creator.fullName", target = "creatorFullName")
    @Mapping(source = "lastModifier.fullName", target = "lastModifierFullName")
    CaseFileResponseDto toDto(CaseFile entity);
}
