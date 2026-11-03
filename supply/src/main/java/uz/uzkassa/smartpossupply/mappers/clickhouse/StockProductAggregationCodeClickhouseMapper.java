package uz.uzkassa.smartpossupply.mappers.clickhouse;

import org.mapstruct.*;
import uz.uzkassa.smartpossupply.domain.stock.StockProductAggregation;
import uz.uzkassa.smartpossupply.dto.clickhouse.StockProductAggregationHistoryClickhouseDTO;
import uz.uzkassa.smartpossupply.utils.DateUtils;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface StockProductAggregationCodeClickhouseMapper {

    @Mappings(
        {
            @Mapping(source = "stockProduct.stock.company.id", target = "companyId"),
            @Mapping(source = "stockProduct.stock.company.name", target = "companyName"),
            @Mapping(source = "stockProduct.stock.company.tin", target = "companyTin"),

            @Mapping(source = "product.name", target = "productName"),
            @Mapping(source = "product.barcode", target = "productBarcode"),
            @Mapping(source = "product.vatBarcode", target = "productVatBarcode"),

            @Mapping(source = "creator.fullName", target = "creatorName"),
            @Mapping(source = "updater.fullName", target = "updaterName"),

            @Mapping(source = "updatedDate", target = "updatedDate", dateFormat = DateUtils.LONG_DATE_FORMAT),
            @Mapping(source = "createdDate", target = "createdDate", dateFormat = DateUtils.LONG_DATE_FORMAT)
        }
    )
    StockProductAggregationHistoryClickhouseDTO toDTO(StockProductAggregation entity);
}
