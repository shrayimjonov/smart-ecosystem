package uz.uzkassa.cts.mappings;

import org.mapstruct.*;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.cts.domain.PaymentContract;
import uz.uzkassa.cts.dto.device.SerialNumberDTO;
import uz.uzkassa.cts.dto.payment.PaymentContractDTO;
import uz.uzkassa.cts.dto.payment.PaymentContractDetailResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentContractMapper {

    @Mapping(source = "customer.name", target = "customerName")
    @Mapping(source = "customer.tin", target = "customerTin")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "creator.fullName", target = "creatorName")
    @Mapping(source = "paymentQuote.model.name", target = "deviceModelName")
    @Mapping(source = "paymentQuote.model.id", target = "deviceModelId")
    @Mapping(target = "devices", expression = "java(devices(entity))")
    PaymentContractDetailResponseDTO toDto(PaymentContract entity);

    @Mapping(source = "customer.name", target = "customerName")
    @Mapping(source = "customer.tin", target = "customerTin")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "creator.fullName", target = "creatorName")
    @Mapping(source = "paymentQuote.model.name", target = "deviceModelName")
    @Mapping(source = "paymentQuote.model.id", target = "deviceModelId")
    @Mapping(target = "devices", expression = "java(devicesFilter(entity))")
    PaymentContractDetailResponseDTO toDtoExecute(PaymentContract entity);

    PaymentContract toEntity(PaymentContractDTO dto);

    PaymentContract toEntity(PaymentContractDTO dto, @MappingTarget PaymentContract entity);

    @AfterMapping
    default List<SerialNumberDTO> devices(final PaymentContract paymentContract) {
        if (!CollectionUtils.isEmpty(paymentContract.getDetails())) {
            return paymentContract.getDetails().stream().map(paymentContractDetail -> {
                SerialNumberDTO dto = new SerialNumberDTO();
                dto.setId(paymentContractDetail.getDevice().getId());
                dto.setSerialNumber(paymentContractDetail.getDevice().getSerialNumber());
                return dto;
            }).collect(Collectors.toList());
        }
        return null;
    }

    @AfterMapping
    default List<SerialNumberDTO> devicesFilter(final PaymentContract paymentContract) {
        if (!CollectionUtils.isEmpty(paymentContract.getDetails())) {
            return paymentContract.getDetails().stream().filter(paymentContractDetail -> paymentContractDetail.getPaymentAct() == null).map(paymentContractDetail -> {
                SerialNumberDTO dto = new SerialNumberDTO();
                dto.setId(paymentContractDetail.getDevice().getId());
                dto.setSerialNumber(paymentContractDetail.getDevice().getSerialNumber());
                return dto;
            }).collect(Collectors.toList());
        }
        return null;
    }
}
