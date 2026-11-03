package uz.uzkassa.cts.mappings;

import org.mapstruct.*;
import uz.uzkassa.cts.domain.PaymentCompanyContract;
import uz.uzkassa.cts.dto.paymentCompanyContract.PaymentCompanyContractDTO;
import uz.uzkassa.cts.dto.paymentCompanyContract.PaymentCompanyContractDetailDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentCompanyContractMapper {

    @Mapping(source = "company.name", target = "companyName")
    @Mapping(source = "company.tin", target = "companyTin")
    @Mapping(source = "companyId", target = "companyId")
    @Mapping(source = "number", target = "contractNumber")
    @Mapping(source = "creator.fullName", target = "creatorName")
    @Mapping(source = "device.model.name", target = "deviceModelName")
    @Mapping(source = "device.model.id", target = "deviceModelId")
    PaymentCompanyContractDetailDTO toDto(PaymentCompanyContract entity);

    PaymentCompanyContract toEntity(PaymentCompanyContractDTO dto);

    PaymentCompanyContract toEntity(PaymentCompanyContractDTO dto, @MappingTarget PaymentCompanyContract entity);
}
