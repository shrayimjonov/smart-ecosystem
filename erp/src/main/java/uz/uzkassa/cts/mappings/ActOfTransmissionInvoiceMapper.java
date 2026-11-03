package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.ActOfTransmissionInvoice;
import uz.uzkassa.cts.dto.actoftransmissioninvoice.response.ActOfTransmissionInvoiceListResponseDto;
import uz.uzkassa.cts.dto.actoftransmissioninvoice.response.ActOfTransmissionInvoiceResponseDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {ActOfTransmissionInvoiceDetailMapper.class})
public interface ActOfTransmissionInvoiceMapper {

    ActOfTransmissionInvoiceResponseDto toDto(ActOfTransmissionInvoice entity);

    @Mapping(source = "actOfTransmission.contract.customer.id", target = "customerId")
    @Mapping(source = "actOfTransmission.contract.customer.name", target = "customerName")
    @Mapping(source = "actOfTransmission.contract.customer.tin", target = "customerTin")
    @Mapping(source = "actOfTransmission.id", target = "actId")
    @Mapping(source = "actOfTransmission.number", target = "actNumber")
    @Mapping(source = "actOfTransmission.createdDate", target = "actDate")
    @Mapping(source = "actOfTransmission.contract.number", target = "contractNumber")
    @Mapping(source = "actOfTransmission.contract.id", target = "contractId")
    @Mapping(source = "actOfTransmission.contract.createdDate", target = "contractDate")
    ActOfTransmissionInvoiceListResponseDto toListDto(ActOfTransmissionInvoice entity);
}
