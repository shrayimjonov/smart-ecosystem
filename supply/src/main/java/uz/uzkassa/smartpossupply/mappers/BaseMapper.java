package uz.uzkassa.smartpossupply.mappers;

import org.mapstruct.MappingTarget;

public interface BaseMapper<Entity, DTO, DetailDTO> {

    DTO toDTO(Entity entity);

    Entity toEntity(DTO dto);

    DetailDTO toDetailDTO(Entity entity);

    Entity merge(DTO dto, @MappingTarget Entity entity);
}
