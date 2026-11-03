package uz.uzkassa.smartpos.mappers.fashion;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.smartpos.domain.postgres.fashion.ProductReservation;
import uz.uzkassa.smartpos.dto.fashion.ProductReservationDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductReservationMapper {
    ProductReservation toEntity(ProductReservationDto dto);
}
