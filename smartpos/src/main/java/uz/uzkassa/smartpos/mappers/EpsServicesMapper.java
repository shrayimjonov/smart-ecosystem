package uz.uzkassa.smartpos.mappers;

import org.mapstruct.*;
import uz.uzkassa.smartpos.domain.postgres.EpsServices;
import uz.uzkassa.smartpos.dto.EpsServicesCrudDto;
import uz.uzkassa.smartpos.dto.EpsServicesDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING)
public interface EpsServicesMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "logoId", ignore = true)
    @Mapping(target = "logoUrl", ignore = true)
    @Mapping(target = "qrMethod", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "file", ignore = true)
    @Mapping(target = "fromApay", ignore = true)
    EpsServices toEntity(EpsServicesCrudDto epsServicesCrudDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @InheritConfiguration(name = "toEntity")
    void partialUpdate(EpsServicesCrudDto epsServicesCrudDto, @MappingTarget EpsServices epsServices);

    default EpsServices partialUpdate(EpsServicesDto dto, EpsServices entity) {
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setType(dto.getType());
        entity.setStatus(dto.getStatus());
        if (dto.getLogo() != null) {
            entity.setLogoId(dto.getLogo().getId());
            entity.setLogoUrl(dto.getLogo().getUrl());
        }
        entity.setQrMethod(dto.getQrMethod());
        entity.setCreatedDate(dto.getCreatedDate());
        entity.setUpdatedDate(dto.getUpdatedDate());
        entity.setFromApay(true);
        entity.setDefaultService(false);
        return entity;
    }
}
