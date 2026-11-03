package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.ModelPriceHistory;
import uz.uzkassa.cts.dto.modelpricehistory.request.ModelPriceHistoryDto;
import uz.uzkassa.cts.dto.modelpricehistory.response.ModelPriceHistoryResponseDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ModelPriceHistoryMapper {

    @Mapping(source = "deviceModel.name", target = "modelName")
    ModelPriceHistoryResponseDto toDto(ModelPriceHistory entity);

    ModelPriceHistory toEntity(ModelPriceHistoryDto dto);

}
