package uz.uzkassa.silen.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uz.uzkassa.silen.dto.SelectItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public enum FilterMode implements Serializable {
    Production("Производено"),
    ShipmentBuyer("Отгружено"),
    Balance("Остаток");

    private final String nameRu;

    public static List<SelectItem> getAll() {
        List<SelectItem> items = new ArrayList<>(FilterMode.values().length);
        for (FilterMode item : FilterMode.values()) {
            items.add(item.toSelectItem());
        }
        return items;
    }

    public SelectItem toSelectItem() {
        return new SelectItem(this.name(), this.getNameRu());
    }
}
