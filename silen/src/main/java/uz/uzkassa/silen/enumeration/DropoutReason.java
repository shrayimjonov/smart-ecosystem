package uz.uzkassa.silen.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uz.uzkassa.silen.dto.SelectItem;

import java.util.ArrayList;
import java.util.List;



@Getter
@AllArgsConstructor
public enum DropoutReason {
    DESTRUCTION("Уничтожение"),
    DEFECT("Брак"),
    EXPIRY("Истек срок годности"),
    QA_SAMPLES("Лабораторные образцы"),
    PRODUCT_RECALL("Отзыв с рынка"),
    COMPLAINTS("Рекламации"),
    PRODUCT_TESTING("Тестирование продукта"),
    DEMO_SAMPLES("Демонстрационные образцы"),
    OTHER("Другие причины");

    private final String nameRu;

    public static List<SelectItem> getAll() {
        List<SelectItem> items = new ArrayList<>(DropoutReason.values().length);
        for (DropoutReason item : DropoutReason.values()) {
            items.add(item.toSelectItem());
        }
        return items;
    }

    public SelectItem toSelectItem() {
        return new SelectItem(this.name(), this.getNameRu());
    }
}
