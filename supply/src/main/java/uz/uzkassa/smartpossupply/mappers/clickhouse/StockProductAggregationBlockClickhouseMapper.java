package uz.uzkassa.smartpossupply.mappers.clickhouse;

import org.mapstruct.*;
import uz.uzkassa.smartpossupply.domain.stock.StockProductAggregationBlock;
import uz.uzkassa.smartpossupply.dto.clickhouse.StockProductAggregationBlockClickhouseDTO;
import uz.uzkassa.smartpossupply.utils.DateUtils;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface StockProductAggregationBlockClickhouseMapper {

    @Mappings(
        {
            @Mapping(source = "product.name", target = "productName"),
            @Mapping(source = "product.barcode", target = "productBarcode"),
            @Mapping(source = "product.vatBarcode", target = "productVatBarcode"),

            @Mapping(source = "creator.fullName", target = "creatorName"),
            @Mapping(source = "updater.fullName", target = "updaterName"),

            @Mapping(source = "updatedDate", target = "updatedDate", dateFormat = DateUtils.LONG_DATE_FORMAT),
            @Mapping(source = "createdDate", target = "createdDate", dateFormat = DateUtils.LONG_DATE_FORMAT)
        }
    )
    StockProductAggregationBlockClickhouseDTO toDTO(StockProductAggregationBlock entity);
}
