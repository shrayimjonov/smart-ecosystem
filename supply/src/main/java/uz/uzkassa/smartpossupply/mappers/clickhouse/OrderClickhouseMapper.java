package uz.uzkassa.smartpossupply.mappers.clickhouse;

import org.mapstruct.*;
import uz.uzkassa.smartpossupply.domain.order.Order;
import uz.uzkassa.smartpossupply.dto.clickhouse.OrderClickhouseDTO;
import uz.uzkassa.smartpossupply.utils.DateUtils;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface OrderClickhouseMapper {
    @Mappings(
        {
            @Mapping(source = "orderDate", target = "orderDate", dateFormat = DateUtils.LONG_DATE_FORMAT),

            @Mapping(source = "company.tin", target = "companyTin"),
            @Mapping(source = "company.name", target = "companyName"),

            @Mapping(source = "contract.contractNumber", target = "contractNumber"),
            @Mapping(source = "contract.contractDate", target = "contractDate", dateFormat = DateUtils.SHORT_DATE_FORMAT),

            @Mapping(source = "customer.tin", target = "customerTin"),
            @Mapping(source = "customer.name", target = "customerName"),

            @Mapping(source = "supplier.tin", target = "supplierTin"),
            @Mapping(source = "supplier.name", target = "supplierName"),

            @Mapping(source = "branch.name", target = "branchName"),

            @Mapping(source = "warehouse.name", target = "warehouseName"),

            @Mapping(source = "status.code", target = "status"),

            @Mapping(source = "deliveryBranch.name", target = "deliveryBranchName"),

            @Mapping(source = "copyOrder.orderNumber", target = "copyOrderNumber"),

            @Mapping(source = "priceList.priceListNumber", target = "priceListNumber"),

            @Mapping(source = "creator.fullName", target = "creatorName"),
            @Mapping(source = "updater.fullName", target = "updaterName"),

            @Mapping(source = "updatedDate", target = "updatedDate", dateFormat = DateUtils.LONG_DATE_FORMAT),
            @Mapping(source = "createdDate", target = "createdDate", dateFormat = DateUtils.LONG_DATE_FORMAT)
        }
    )
    OrderClickhouseDTO toDTO(Order entity);

}
