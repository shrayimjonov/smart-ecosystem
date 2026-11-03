package uz.uzkassa.smartpos.mappers;

import org.mapstruct.*;
import uz.uzkassa.smartpos.domain.postgres.common.Neighborhood;
import uz.uzkassa.smartpos.dto.NeighborhoodDTO;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING)
public interface NeighborhoodMapper {
    NeighborhoodDTO toDto(Neighborhood neighborhood);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "city", ignore = true)
    void partialUpdate(NeighborhoodDTO neighborhoodDTO, @MappingTarget Neighborhood neighborhood);
}
