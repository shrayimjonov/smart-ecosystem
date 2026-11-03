package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.TradeInLetterOfGuarantee;
import uz.uzkassa.cts.dto.tradeinletterofguarantee.response.LetterOfGuaranteeResponseDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TradeInLetterOfGuaranteeMapper {

    @Mapping(source = "customer.name", target = "customerName")
    @Mapping(source = "customer.tin", target = "tin")
    @Mapping(source = "tradeInDevice.name", target = "tradeInDeviceName")
    LetterOfGuaranteeResponseDto toDto(TradeInLetterOfGuarantee entity);
}
