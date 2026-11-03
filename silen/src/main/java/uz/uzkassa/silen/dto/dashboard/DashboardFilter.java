package uz.uzkassa.silen.dto.dashboard;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.dto.filter.OrganizationFilter;
import uz.uzkassa.silen.enumeration.OrganizationType;
import uz.uzkassa.silen.enumeration.ProductType;
import uz.uzkassa.silen.enumeration.WarehouseOperation;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DashboardFilter extends OrganizationFilter {
    String deviceId;

    String lineId;

    private ProductType productType;

    private Set<ProductType> productTypes;

    private EnumSet<WarehouseOperation> warehouseOperations;

    private boolean isConsumer = false;

    OrganizationType organizationType;

    private EnumSet<OrganizationType> organizationTypes;


    public String getWarehouseOperationsString() {
        return getWarehouseOperations()
            .stream()
            .map(WarehouseOperation::name)
            .map(s -> "'" + s + "'")
            .collect(Collectors.joining(","));
    }

    public String getProductTypesString() {
        return getProductTypes()
            .stream()
            .map(ProductType::name)
            .map(s -> "'" + s + "'")
            .collect(Collectors.joining(","));
    }

    public String getOrganizationTypesString() {
        return getOrganizationTypes()
            .stream()
            .map(OrganizationType::name)
            .map(s -> "'" + s + "'")
            .collect(Collectors.joining(","));
    }
}
