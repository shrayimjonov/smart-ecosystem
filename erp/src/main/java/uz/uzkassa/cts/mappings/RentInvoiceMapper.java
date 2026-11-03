package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.RentInvoice;
import uz.uzkassa.cts.dto.rentinvoice.response.RentInvoiceResponseDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RentInvoiceMapper {

    @Mapping(source = "rentOffer.customer.id", target = "customerId")
    @Mapping(source = "rentOffer.customer.name", target = "customerName")
    @Mapping(source = "rentOffer.customer.tin", target = "customerTin")
    @Mapping(source = "rentOffer.number", target = "offerNumber")
    @Mapping(source = "rentOffer.id", target = "offerId")
    @Mapping(source = "rentOffer.createdDate", target = "offerDate")
    @Mapping(source = "number", target = "invoiceNumber")
    RentInvoiceResponseDto toDto(RentInvoice entity);
}
