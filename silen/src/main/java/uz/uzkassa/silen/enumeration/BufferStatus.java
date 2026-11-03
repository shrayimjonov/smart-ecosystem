package uz.uzkassa.silen.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uz.uzkassa.silen.dto.SelectItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public enum BufferStatus implements Serializable {
    PENDING("Буфер КМ находится в ожидании"),
    ACTIVE("Буфер создан"),
    EXHAUSTED("Буфер и пулы не содержат больше кодов"),
    REJECTED("Буфер более не доступен для работы"),
    CLOSED("Буфер закрыт");

    private final String nameRu;

    public static List<SelectItem> getAll() {
        List<SelectItem> items = new ArrayList<>(BufferStatus.values().length);
        for (BufferStatus item : BufferStatus.values()) {
            items.add(item.toSelectItem());
        }
        return items;
    }

    public SelectItem toSelectItem() {
        return new SelectItem(this.name(), this.getNameRu());
    }
}
