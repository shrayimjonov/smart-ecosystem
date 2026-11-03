package uz.uzkassa.smartpossupply.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.smartpossupply.dto.order.OrderDTO;
import uz.uzkassa.smartpossupply.dto.order.mobile.MobileOrderDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface OrderMobileMapper {

    OrderDTO toDTO(MobileOrderDTO dto);

}
