package uz.uzkassa.silen.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uz.uzkassa.silen.dto.SelectItem;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;


@Getter
@AllArgsConstructor
public enum CodeType implements Serializable {
    AGGREGATION("Аггрегация"),

    BLOCK("Блок"),

    MARK("Маркировка");

    private final String nameRu;

    public static List<SelectItem> getAll() {
        return Arrays.asList(AGGREGATION.toSelectItem(), BLOCK.toSelectItem(), MARK.toSelectItem());
    }

    public SelectItem toSelectItem() {
        return new SelectItem(this.name(), this.getNameRu());
    }
}
