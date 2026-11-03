package uz.uzkassa.silen.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uz.uzkassa.silen.dto.SelectItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Зарегистрировать заказ от имени УОТ с
 * ролью "Оптовая торговля" и "Розничная торговля"
 * можно только с передаваемыми
 * значениями "Маркировка остатков" или "Комиссия"
 *
 * Пример запроса для ТГ <> "Табачная продукция"
 */
@Getter
@AllArgsConstructor
public enum ReleaseMethodType implements Serializable {
    PRODUCTION("Производство в стране"),
    IMPORT("Ввезен в страну (Импорт)"),
    REMAINS("Маркировка остатков"),
    COMMISSION("Комиссия");

    private final String nameRu;

    public static List<SelectItem> getAll() {
        List<SelectItem> items = new ArrayList<>(ReleaseMethodType.values().length);
        for (ReleaseMethodType item : ReleaseMethodType.values()) {
            items.add(item.toSelectItem());
        }
        return items;
    }

    public SelectItem toSelectItem() {
        return new SelectItem(this.name(), this.getNameRu());
    }
}
