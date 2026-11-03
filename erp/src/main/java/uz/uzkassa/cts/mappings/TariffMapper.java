package uz.uzkassa.cts.mappings;

import org.mapstruct.*;
import uz.uzkassa.cts.domain.Tariff;
import uz.uzkassa.cts.dto.tariff.request.TariffRequestDto;
import uz.uzkassa.cts.dto.tariff.response.TariffServiceResponseDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TariffMapper {

    TariffRequestDto toDto(Tariff entity);

    @Mapping(source = "ctsServices", target = "services")
    TariffServiceResponseDto toDtoWithServices(Tariff entity);

    Tariff toEntity(TariffRequestDto dto);

    void toEntity(TariffRequestDto dto, @MappingTarget Tariff tariff);
}
