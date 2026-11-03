package uz.uzkassa.silen.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uz.uzkassa.silen.dto.SelectItem;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public enum MarkOrderType {
    OPERATOR("Aвтоматический"),
    SELF_MADE("Oт пользователя");

    private final String nameRu;

    public static List<SelectItem> getAll() {
        List<SelectItem> items = new ArrayList<>(MarkOrderType.values().length);
        for (MarkOrderType item : MarkOrderType.values()) {
            items.add(item.toSelectItem());
        }
        return items;
    }

    public SelectItem toSelectItem() {
        return new SelectItem(this.name(), this.getNameRu());
    }

}
