package uz.uzkassa.smartpos.mappers;

import org.mapstruct.*;
import uz.uzkassa.smartpos.domain.postgres.Instructor;
import uz.uzkassa.smartpos.dto.InstructorFileCreateDTO;
import uz.uzkassa.smartpos.dto.InstructorFileDTO;
import uz.uzkassa.smartpos.dto.InstructorFileUpdateDTO;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING)
public interface InstructorMapper {
    @Mapping(target = "parentCode", source = "type.parentCode")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "file", ignore = true)
    @Mapping(target = "image", ignore = true)
    Instructor toEntity(InstructorFileCreateDTO instructorFileCreateDTO);

    InstructorFileDTO toDto(Instructor instructor);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "parentCode", source = "type.parentCode")

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "file", ignore = true)
    @Mapping(target = "image", ignore = true)
    Instructor updateEntity(InstructorFileUpdateDTO dto, @MappingTarget Instructor instructor);
}