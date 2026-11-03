package uz.uzkassa.smartpossupply.mappers.clickhouse;

import org.mapstruct.*;
import uz.uzkassa.smartpossupply.domain.stock.Stock;
import uz.uzkassa.smartpossupply.dto.clickhouse.StockClickhouseDTO;
import uz.uzkassa.smartpossupply.utils.DateUtils;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface StockClickhouseMapper {
    @Mappings(
        {
            @Mapping(source = "transferDate", target = "transferDate", dateFormat = DateUtils.LONG_DATE_FORMAT),

            @Mapping(source = "contract.contractNumber", target = "contractNumber"),
            @Mapping(source = "contract.contractDate", target = "contractDate", dateFormat = DateUtils.SHORT_DATE_FORMAT),

            @Mapping(source = "warehouse.name", target = "warehouseName"),

            @Mapping(source = "customer.tin", target = "customerTin"),
            @Mapping(source = "customer.name", target = "customerName"),

            @Mapping(source = "supplier.tin", target = "supplierTin"),
            @Mapping(source = "supplier.name", target = "supplierName"),

            @Mapping(source = "company.tin", target = "companyTin"),
            @Mapping(source = "company.name", target = "companyName"),

            @Mapping(source = "branch.name", target = "branchName"),

            @Mapping(source = "factura.facturaId", target = "facturaNicId"),
            @Mapping(source = "factura.facturaDate", target = "facturaDate"),
            @Mapping(source = "factura.facturaNo", target = "facturaNo"),

            @Mapping(source = "party.partyNumber", target = "partyNumber"),

            @Mapping(source = "order.orderNumber", target = "orderNumber"),
            @Mapping(source = "order.orderDate", target = "orderDate", dateFormat = DateUtils.LONG_DATE_FORMAT),

            @Mapping(source = "oldStock.stockNumber", target = "oldStockNumber"),
            @Mapping(source = "oldStock.transferDate", target = "oldStockTransferDate", dateFormat = DateUtils.LONG_DATE_FORMAT),

            @Mapping(source = "stockIn.stockNumber", target = "stockInNumber"),
            @Mapping(source = "stockIn.transferDate", target = "stockInTransferDate", dateFormat = DateUtils.LONG_DATE_FORMAT),

            @Mapping(source = "stockTransfer.transferNumber", target = "stockTransferNumber"),
            @Mapping(source = "stockTransfer.transferDate", target = "stockTransferDate", dateFormat = DateUtils.LONG_DATE_FORMAT),

            @Mapping(source = "priceList.priceListNumber", target = "priceListNumber"),

            @Mapping(source = "creator.fullName", target = "creatorName"),
            @Mapping(source = "updater.fullName", target = "updaterName"),

            @Mapping(source = "updatedDate", target = "updatedDate", dateFormat = DateUtils.LONG_DATE_FORMAT),
            @Mapping(source = "createdDate", target = "createdDate", dateFormat = DateUtils.LONG_DATE_FORMAT)
        }
    )
    StockClickhouseDTO toDTO(Stock entity);

}
