package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.cases.CaseNote;
import uz.uzkassa.cts.dto.casenote.response.CaseNoteResponseDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CaseNoteMapper {

    @Mapping(source = "creator.fullName", target = "creatorFullName")
    @Mapping(source = "lastModifier.fullName", target = "lastModifierFullName")
//    @Mapping(source = "messageType.code", target = "messageType")
    CaseNoteResponseDto toDto(CaseNote entity);
}
