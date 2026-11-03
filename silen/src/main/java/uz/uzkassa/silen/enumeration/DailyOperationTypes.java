package uz.uzkassa.silen.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uz.uzkassa.silen.dto.SelectItem;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public enum DailyOperationTypes {
    Production("Производство"),
    Shipment("Отгрузка"),
    Returned("Возврат"),
    Dropout("Списано");

    private final String nameRu;

    public SelectItem toSelectItem() {
        return new SelectItem(this.name(), this.getNameRu());
    }

    public static List<SelectItem> getAll() {
        List<SelectItem> items = new ArrayList<>(DailyOperationTypes.values().length);
        for (DailyOperationTypes item : DailyOperationTypes.values()) {
            items.add(item.toSelectItem());
        }
        return items;
    }

    public static DailyOperationTypes fromString(String name) {
        if (name != null) {
            for (DailyOperationTypes item : DailyOperationTypes.values()) {
                if (name.equals(item.name())) {
                    return item;
                }
            }
        }
        return null;
    }

}
