package uz.uzkassa.cts.mappings;

import org.mapstruct.*;
import uz.uzkassa.cts.domain.TradeInContract;
import uz.uzkassa.cts.dto.tradeincontract.request.TradeInContractRequestDto;
import uz.uzkassa.cts.dto.tradeincontract.response.TradeInContractResponseDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TradeInContractMapper {

    @Mapping(source = "customer.name", target = "customerName")
    @Mapping(source = "customer.tin", target = "customerTin")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "totalPriceWithVat", target = "amount")
    @Mapping(source = "number", target = "contractNumber")
    @Mapping(source = "creator.fullName", target = "creatorName")
    @Mapping(source = "tradeInAct.id", target = "tradeInActId")
    @Mapping(source = "tradeInInvoice.id", target = "tradeInInvoiceId")
    @Mapping(source = "tradeInCounterContract.id", target = "tradeInCounterContractId")
    @Mapping(source = "tradeInPriceHistory.deviceModel.name", target = "deviceModelName")
    @Mapping(source = "tradeInPriceHistory.deviceModel.id", target = "deviceModelId")
    @Mapping(source = "branch.name", target = "branchName")
    TradeInContractResponseDto toDto(TradeInContract entity);

    TradeInContract toEntity(TradeInContractRequestDto dto);

    TradeInContract toEntity(TradeInContractRequestDto dto, @MappingTarget TradeInContract entity);

}
