package uz.uzkassa.horeca.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;
import uz.uzkassa.horeca.dto.order.OrderDTO;
import uz.uzkassa.horeca.dto.order.admin.OrderAdminDTO;
import uz.uzkassa.horeca.dto.order.cabinet.OrderCabinetDTO;
import uz.uzkassa.horeca.dto.order.mobile.OrderMobileDTO;

@Component
@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface OrderMapper extends CabinetMapper<OrderDTO, OrderCabinetDTO>, AdminMapper<OrderDTO, OrderAdminDTO>, MobileMapper<OrderDTO, OrderMobileDTO> {
}
