package uz.uzkassa.smartpossupply.filters;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.ProductType;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
public class BaseFilter extends SimpleFilter {

    Long id;

    Long parentId;

    Long customerId;

    String customerTin;

    Long supplierId;

    String supplierTin;

    Long productId;

    Long regionId;

    Long districtId;

    Long warehouseId;

    String status;

    String excludeStatus;

    List<String> statuses;

    String tin;

    String type;

    boolean forTransfer;

    Long creatorId;

    boolean main;

    Long carId;

    Long userId;

    String creatorRole;

    List<ProductType> productTypes;

    boolean enabledSorting;
}
