package uz.uzkassa.smartpos.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.smartpos.domain.postgres.warehouse.MarkingHistoryItem;
import uz.uzkassa.smartpos.dto.warehouse.MarkingHistoryItemDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING)
public interface MarkingHistoryItemMapper {
    @Mapping(target = "markingHistory", ignore = true)
    MarkingHistoryItem toEntity(MarkingHistoryItemDto itemDto);

    MarkingHistoryItemDto toDto(MarkingHistoryItem markingHistoryItem);
}
