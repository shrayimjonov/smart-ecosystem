package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.ContractForLeasing;
import uz.uzkassa.cts.dto.contractforleasing.response.ContractForLeasingListResponseDto;
import uz.uzkassa.cts.dto.contractforleasing.response.ContractForLeasingResponseDto;

//@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { ActOfTransmissionMapper.class })
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ContractFroLeasingMapper {

    @Mapping(source = "modelPriceHistory.name", target = "modelPriceHistoryName")
    @Mapping(source = "customer.name", target = "customerName")
    @Mapping(source = "customer.tin", target = "customerTin")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "totalPriceWithVat", target = "amount")
    @Mapping(source = "number", target = "contractNumber")
    @Mapping(source = "creator.id", target = "creatorId")
    @Mapping(source = "creator.fullName", target = "creatorName")
    ContractForLeasingListResponseDto toListDto(ContractForLeasing entity);

    @Mapping(source = "modelPriceHistory.name", target = "modelPriceHistoryName")
    @Mapping(source = "customer.name", target = "customerName")
    @Mapping(source = "customer.tin", target = "customerTin")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "totalPriceWithVat", target = "amount")
    @Mapping(source = "number", target = "contractNumber")
    @Mapping(source = "creator.id", target = "creatorId")
    @Mapping(source = "creator.fullName", target = "creatorName")
    @Mapping(source = "actOfTransmissions", target = "actOfTransmissionResponseDtoList")
    ContractForLeasingResponseDto toDto(ContractForLeasing entity);
}
