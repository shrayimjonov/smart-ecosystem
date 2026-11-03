package uz.uzkassa.smartpos.mappers;

import org.mapstruct.*;
import uz.uzkassa.smartpos.domain.postgres.warehouse.OrderProduct;
import uz.uzkassa.smartpos.integration.supply.dto.CreateOrderProductDTO;
import uz.uzkassa.smartpos.integration.supply.dto.OrderProductDTO;
import uz.uzkassa.smartpos.integration.supply.dto.SupplierOrderProductDetailDTO;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderProductMapper {
    OrderProductDTO toDTO(OrderProduct entity);

    @Mapping(target = "unitId", source = "unit.id")
    @Mapping(target = "unitName", source = "unit.name")
    @Mapping(target = "unitCode", source = "unit.code")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "photos", ignore = true)
    @Mapping(target = "order", ignore = true)
    OrderProduct toEntity(SupplierOrderProductDetailDTO dto);


    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "order", ignore = true)
    void update(CreateOrderProductDTO dto, @MappingTarget OrderProduct entity);
}
