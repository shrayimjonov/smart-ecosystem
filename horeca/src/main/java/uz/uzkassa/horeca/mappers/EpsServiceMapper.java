package uz.uzkassa.horeca.mappers;

import org.mapstruct.*;
import org.springframework.stereotype.Component;
import uz.uzkassa.horeca.domain.EpsServiceDomain;
import uz.uzkassa.horeca.dto.eps.EpsServiceCrudDTO;
import uz.uzkassa.horeca.dto.eps.EpsServiceDTO;

@Component
@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface EpsServiceMapper {
    EpsServiceDomain toEntity(EpsServiceCrudDTO epsServiceCrudDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(EpsServiceCrudDTO epsServiceCrudDTO, @MappingTarget EpsServiceDomain epsServiceDomain);

    default EpsServiceDomain partialUpdate(EpsServiceDTO dto, EpsServiceDomain entity) {
        entity.setId(dto.getId());
        entity.setNameRu(dto.getName());
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
