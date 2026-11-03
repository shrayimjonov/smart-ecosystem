package uz.uzkassa.smartpossupply.mappers.clickhouse;

import org.mapstruct.*;
import uz.uzkassa.smartpossupply.domain.stock.StockProduct;
import uz.uzkassa.smartpossupply.dto.clickhouse.StockProductClickhouseDTO;
import uz.uzkassa.smartpossupply.utils.DateUtils;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface StockProductClickhouseMapper {

    @Mappings(
        {
            @Mapping(source = "product.name", target = "productName"),
            @Mapping(source = "product.barcode", target = "productBarcode"),
            @Mapping(source = "product.vatBarcode", target = "productVatBarcode"),
            @Mapping(source = "stock.stockNumber", target = "stockNumber"),
            @Mapping(source = "stock.transferDate", target = "stockTransferDate", dateFormat = DateUtils.LONG_DATE_FORMAT),

            @Mapping(source = "creator.fullName", target = "creatorName"),
            @Mapping(source = "updater.fullName", target = "updaterName"),

            @Mapping(source = "updatedDate", target = "updatedDate", dateFormat = DateUtils.LONG_DATE_FORMAT),
            @Mapping(source = "createdDate", target = "createdDate", dateFormat = DateUtils.LONG_DATE_FORMAT)
        }
    )
    StockProductClickhouseDTO toDTO(StockProduct entity);
}
