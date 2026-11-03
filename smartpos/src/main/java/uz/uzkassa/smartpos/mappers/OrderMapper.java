package uz.uzkassa.smartpos.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.smartpos.domain.postgres.common.Address;
import uz.uzkassa.smartpos.domain.postgres.warehouse.Order;
import uz.uzkassa.smartpos.integration.supply.dto.AddressDetailDTO;
import uz.uzkassa.smartpos.integration.supply.dto.OrderDTO;
import uz.uzkassa.smartpos.integration.supply.dto.SupplierOrderDetailDTO;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING, uses = {OrderProductMapper.class, AddressMapper.class})
public interface OrderMapper {

    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "supplierId", source = "supplier.id")
    @Mapping(target = "supplierName", source = "supplier.name")
    @Mapping(target = "supplierTin", source = "supplier.tin")
    @Mapping(target = "branchId", source = "branch.id")
    @Mapping(target = "branchName", source = "branch.name")
    @Mapping(target = "deliveryBranch", ignore = true)
    @Mapping(target = "deliveryBranchId", source = "deliveryBranch.id")
    @Mapping(target = "contractId", source = "contract.id")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "warehouseId", source = "warehouse.id")
    Order toEntity(SupplierOrderDetailDTO dto);

    @Mapping(target = "customerTin", ignore = true)
    @Mapping(target = "appType", ignore = true)
    OrderDTO toDTO(Order order);

    @Mapping(target = "regionId", source = "region.id")
    @Mapping(target = "regionName", source = "region.name")
    @Mapping(target = "districtId", source = "district.id")
    @Mapping(target = "districtName", source = "district.name")
    @Mapping(target = "regionCode", ignore = true)
    @Mapping(target = "districtCode", ignore = true)
    Address toEntity(AddressDetailDTO dto);
}
