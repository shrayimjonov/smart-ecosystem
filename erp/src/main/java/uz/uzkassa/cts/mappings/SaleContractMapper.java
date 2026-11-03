package uz.uzkassa.cts.mappings;

import org.mapstruct.*;
import uz.uzkassa.cts.domain.SaleContract;
import uz.uzkassa.cts.dto.sale.contract.SaleContractDTO;
import uz.uzkassa.cts.dto.sale.contract.SaleContractResponseDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SaleContractMapper {

    @Mapping(source = "customer.name", target = "customerName")
    @Mapping(source = "customer.tin", target = "customerTin")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "creator.fullName", target = "creatorName")
    @Mapping(source = "number", target = "contractNumber")
    @Mapping(source = "saleAct.id", target = "saleActId")
    @Mapping(source = "saleInvoice.id", target = "saleInvoiceId")
    SaleContractResponseDTO toDto(SaleContract entity);

    SaleContract toEntity(SaleContractDTO dto);

    SaleContract toEntity(SaleContractDTO dto, @MappingTarget SaleContract entity);
}
