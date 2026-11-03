package uz.uzkassa.cts.mappings;

import org.mapstruct.*;
import uz.uzkassa.cts.domain.TradeInCounterContract;
import uz.uzkassa.cts.dto.tradeincountercontract.request.TradeInCounterContractRequestDto;
import uz.uzkassa.cts.dto.tradeincountercontract.response.TradeInCounterContractResponseDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TradeInCounterContractMapper {

    @Mapping(source = "customer.name", target = "customerName")
    @Mapping(source = "customer.tin", target = "customerTin")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "totalPriceWithVat", target = "amount")
    @Mapping(source = "number", target = "contractNumber")
    @Mapping(source = "creator.fullName", target = "creatorName")
    @Mapping(source = "tradeInDevice.smartOrOldDeviceEnum", target = "smartOrOldDeviceEnum")
    @Mapping(source = "tradeInDevice.name", target = "deviceName")
    TradeInCounterContractResponseDto toDto(TradeInCounterContract entity);

    TradeInCounterContract toEntity(TradeInCounterContractRequestDto dto);

    TradeInCounterContract toEntity(TradeInCounterContractRequestDto dto, @MappingTarget TradeInCounterContract entity);
}
