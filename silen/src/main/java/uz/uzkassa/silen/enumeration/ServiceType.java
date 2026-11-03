package uz.uzkassa.silen.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uz.uzkassa.silen.dto.SelectItem;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum ServiceType {
    E_INVOICE("СФ"),
    PRIMARY_AGGREGATION("Основной пакет аггрегация"),
    PRIMARY_MARK("Основной пакет маркировок"),
    ADDITIONAL_AGGREGATION("Дополнительный пакет аггрегация"),
    ADDITIONAL_MARK("Дополнительный пакет маркировок"),
    FREE_TRIAL("Пробный период"),
    CUSTOM_WAREHOUSE_MARK("Пакет маркировок");

    private final String nameRu;

    public SelectItem toSelectItem() {
        return new SelectItem(this.name(), this.getNameRu());
    }

    public static List<String> countableBillingPermissions() {
        return Arrays.asList(ServiceType.E_INVOICE.name(), PRIMARY_AGGREGATION.name(),CUSTOM_WAREHOUSE_MARK.name(),
            ADDITIONAL_AGGREGATION.name(), PRIMARY_MARK.name(), ADDITIONAL_MARK.name());
    }

    public static List<SelectItem> givePermissionList() {
        return Arrays.asList(PRIMARY_AGGREGATION.toSelectItem(),
            ADDITIONAL_AGGREGATION.toSelectItem(),
            PRIMARY_MARK.toSelectItem(),
            ADDITIONAL_MARK.toSelectItem());
    }


}
