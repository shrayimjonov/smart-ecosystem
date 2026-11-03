package uz.uzkassa.silen.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uz.uzkassa.silen.dto.SelectItem;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum SerialNumberType implements Serializable {
    SELF_MADE("Самостоятельно"),
    OPERATOR("Оператором"),
    UPLOAD("Загрузить");

    private final String nameRu;

    //    public static List<SelectItem> getAll() {
//        List<SelectItem> items = new ArrayList<>(SerialNumberType.values().length);
//        for (SerialNumberType item : SerialNumberType.values()) {
//            items.add(item.toSelectItem());
//        }
//        return items;
//    }
    public static List<SelectItem> getAll() {
        return Arrays.asList(SerialNumberType.SELF_MADE.toSelectItem(), SerialNumberType.OPERATOR.toSelectItem());
    }


    public SelectItem toSelectItem() {
        return new SelectItem(this.name(), this.getNameRu());
    }
}
