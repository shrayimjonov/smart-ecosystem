package uz.uzkassa.silen.enumeration;

import lombok.Getter;
import uz.uzkassa.silen.dto.SelectItem;

import java.util.ArrayList;
import java.util.List;

@Getter
public enum PermissionType {
    ADMIN("Админ"),
    CABINET("Кабинет");

    final String text;

    PermissionType(String text) {
        this.text = text;
    }


    public SelectItem toSelectItem() {
        return new SelectItem(this.name(), this.getText());
    }

    public static List<SelectItem> getAll() {
        List<SelectItem> items = new ArrayList<>(PermissionType.values().length);
        for (PermissionType item : PermissionType.values()) {
            items.add(item.toSelectItem());
        }
        return items;
    }
}
