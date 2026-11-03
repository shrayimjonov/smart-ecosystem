package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.IncomingInvoice;
import uz.uzkassa.cts.dto.factura.InvoiceDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IncomingInvoiceMapper {

    IncomingInvoice toEntity(InvoiceDTO dto);
}
