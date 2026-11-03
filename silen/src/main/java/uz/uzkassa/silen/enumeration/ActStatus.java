package uz.uzkassa.silen.enumeration;

import lombok.Getter;
import uz.uzkassa.silen.dto.SelectItem;

import java.util.ArrayList;
import java.util.List;

@Getter
public enum ActStatus {

    DRAFT("Сохраненные"),
    SENT("Отправлено"),
    ACCEPT("Подтвержденные"),
    REJECT("Отклонено покупателем"),
    CANCEL("Отмененные");

    final String text;

    ActStatus(String text) {
        this.text = text;
    }


    public SelectItem toSelectItem() {
        return new SelectItem(this.name(), this.getText());
    }

    public static List<SelectItem> getAll() {
        List<SelectItem> items = new ArrayList<>(ActStatus.values().length);
        for (ActStatus item : ActStatus.values()) {
            items.add(item.toSelectItem());
        }
        return items;
    }
}
