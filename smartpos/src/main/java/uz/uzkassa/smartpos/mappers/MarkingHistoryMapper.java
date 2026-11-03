package uz.uzkassa.smartpos.mappers;

import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.*;
import uz.uzkassa.smartpos.domain.postgres.warehouse.MarkingHistory;
import uz.uzkassa.smartpos.dto.warehouse.MarkingHistoryCreateDto;
import uz.uzkassa.smartpos.dto.warehouse.MarkingHistoryDto;
import uz.uzkassa.smartpos.dto.warehouse.MarkingHistoryListDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR, uses = {MarkingHistoryItemMapper.class})
public interface MarkingHistoryMapper {
    MarkingHistoryDto toDto(MarkingHistory markingHistory);

    MarkingHistoryListDto toListDto(MarkingHistory markingHistory);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    MarkingHistory toEntity(MarkingHistoryCreateDto markingHistoryDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @InheritConfiguration(name = "toEntity")
    void partialUpdate(@MappingTarget MarkingHistory markingHistory, MarkingHistoryCreateDto dto);

    @AfterMapping
    default void linkMarks(@MappingTarget MarkingHistory markingHistory) {
        if (CollectionUtils.isNotEmpty(markingHistory.getMarks())) {
            markingHistory.getMarks().forEach(mark -> mark.setMarkingHistory(markingHistory));
        }
    }
}
