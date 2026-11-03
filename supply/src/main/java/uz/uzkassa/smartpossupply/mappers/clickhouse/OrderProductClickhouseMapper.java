package uz.uzkassa.smartpossupply.mappers.clickhouse;

import org.mapstruct.*;
import uz.uzkassa.smartpossupply.domain.order.OrderProduct;
import uz.uzkassa.smartpossupply.dto.clickhouse.OrderProductClickhouseDTO;
import uz.uzkassa.smartpossupply.utils.DateUtils;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface OrderProductClickhouseMapper {

    @Mappings(
            {
                    @Mapping(source = "order.orderNumber", target = "orderNumber"),
                    @Mapping(source = "order.orderDate", target = "orderDate", dateFormat = DateUtils.LONG_DATE_FORMAT),

                    @Mapping(source = "product.name", target = "productName"),
                    @Mapping(source = "product.barcode", target = "productBarcode"),
                    @Mapping(source = "product.vatBarcode", target = "productVatBarcode"),

                    @Mapping(source = "product.unit.nameRu", target = "unitName"),

            }
    )
    OrderProductClickhouseDTO toDTO(OrderProduct entity);
}
