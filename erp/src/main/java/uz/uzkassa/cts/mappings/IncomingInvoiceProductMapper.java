package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.IncomingInvoiceProduct;
import uz.uzkassa.cts.dto.factura.FacturaProductDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IncomingInvoiceProductMapper {

    IncomingInvoiceProduct toEntity(FacturaProductDTO dto);
}
