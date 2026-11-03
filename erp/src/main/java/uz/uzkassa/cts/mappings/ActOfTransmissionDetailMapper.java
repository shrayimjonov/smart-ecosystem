package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.ActOfTransmissionDetail;
import uz.uzkassa.cts.dto.actoftransmissiondetail.response.ActOfTransmissionDetailListResponseDto;
import uz.uzkassa.cts.dto.actoftransmissiondetail.response.ActOfTransmissionDetailResponseDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ActOfTransmissionDetailMapper {

    @Mapping(source = "device.serialNumber", target = "deviceSerialNumber")
    ActOfTransmissionDetailResponseDto toDto(ActOfTransmissionDetail entity);

    @Mapping(source = "device.serialNumber", target = "deviceNumber")
    @Mapping(source = "priceWithVatFor1pc", target = "amount")
    @Mapping(source = "actOfTransmission.period", target = "period")
    @Mapping(source = "actOfTransmission.contract.customer.tin", target = "tin")
    @Mapping(source = "actOfTransmission.contract.customer.id", target = "customerId")
    @Mapping(source = "actOfTransmission.contract.customer.name", target = "customerName")
    @Mapping(source = "actOfTransmission.number", target = "actNumber")
    @Mapping(source = "actOfTransmission.id", target = "actId")
    @Mapping(source = "actOfTransmission.status", target = "status")
    @Mapping(source = "actOfTransmission.contract.id", target = "contractId")
    @Mapping(source = "actOfTransmission.contract.number", target = "contractNumber")
    @Mapping(source = "actOfTransmission.acceptDate", target = "actAcceptDate")
    @Mapping(source = "actOfTransmission.docFlowStatus", target = "docFlowStatus")
    @Mapping(source = "actOfTransmission.docFlowReason", target = "docFlowReason")
    @Mapping(source = "creator.id", target = "creatorId")
    @Mapping(source = "creator.fullName", target = "creatorName")
    ActOfTransmissionDetailListResponseDto toListDto(ActOfTransmissionDetail entity);
}
