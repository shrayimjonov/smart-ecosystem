package uz.uzkassa.smartpos.domain.postgres;

import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReferenceMapper {
    Reference toEntity(ReferenceCreateDto referenceCreateDto);

    @Mapping(target = "code", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Reference partialUpdate(ReferenceCreateDto referenceCreateDto, @MappingTarget Reference reference);

    ReferenceAdminDto toDto(Reference reference);
}