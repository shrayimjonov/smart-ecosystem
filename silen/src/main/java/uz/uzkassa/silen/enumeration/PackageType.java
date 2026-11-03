package uz.uzkassa.silen.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uz.uzkassa.silen.dto.SelectItem;

import java.io.Serializable;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

@Getter
@AllArgsConstructor
public enum PackageType implements Serializable {
    BOX("Коробка", 0, CodeType.AGGREGATION),
    PALLET("Паллет", 1, CodeType.AGGREGATION),
    BLOCK("Блок", 2, CodeType.BLOCK),
    BOTTLE("Бутылок", null, CodeType.MARK);

    private final String nameRu;
    private final Integer code;
    private final CodeType codeType;
    private static final List<SelectItem> SELECT_ITEMS = Arrays.asList(BOX.toSelectItem(), PALLET.toSelectItem(), BLOCK.toSelectItem());

    public static List<SelectItem> getAll() {
        return SELECT_ITEMS;
    }

    public static PackageType fromCode(Integer code) {
        if (code == null) return BOTTLE;
        for (PackageType item : PackageType.values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return BOX;
    }

    public SelectItem toSelectItem() {
        return new SelectItem(this.name(), this.getNameRu());
    }

    public boolean isMark() {
        return EnumSet.of(BOTTLE, BLOCK).contains(this);
    }

    public boolean isAggregation() {
        return EnumSet.of(BOX, PALLET).contains(this);
    }
}
