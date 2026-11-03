package uz.uzkassa.silen.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uz.uzkassa.silen.dto.SelectItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Расширение для ТГ "Алкоголь"
 */
@Getter
@AllArgsConstructor
public enum CreateMethodType implements Serializable {
    SELF_MADE("Самостоятельно"),
    CEM("ЦЭМ"),
    CM("Контрактное производство"),
    CL("Логистический склад"),
    CA("Комиссионная площадка");

    private final String nameRu;

    public static List<SelectItem> getAll() {
        List<SelectItem> items = new ArrayList<>(CreateMethodType.values().length);
        for (CreateMethodType item : CreateMethodType.values()) {
            items.add(item.toSelectItem());
        }
        return items;
    }

    public SelectItem toSelectItem() {
        return new SelectItem(this.name(), this.getNameRu());
    }
}
