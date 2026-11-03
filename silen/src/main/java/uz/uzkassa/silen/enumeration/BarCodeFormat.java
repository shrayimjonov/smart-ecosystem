package uz.uzkassa.silen.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import uz.uzkassa.silen.dto.SelectItem;

import java.util.ArrayList;
import java.util.List;

public enum BarCodeFormat {
    UPC_A("UPC-A"),
    UPC_E("UPC-E"),
    EAN_8("EAN-8"),
    EAN_13("EAN-13"),
    CODE_39("Code 39"),
    CODE_93("Code 93"),
    CODE_128("Code 128"),
    CODABAR("Codabar");

    private String code;

    public String getCode() {
        return code;
    }

    BarCodeFormat(String code) {
        this.code = code;
    }

    public static BarCodeFormat fromString(String code) {
        if (code != null) {
            for (BarCodeFormat item : BarCodeFormat.values()) {
                if (code.equalsIgnoreCase(item.code) || code.equals(item.name())) {
                    return item;
                }
            }
        }
        return null;
    }

    @JsonCreator
    public static BarCodeFormat forValue(String value) {
        return fromString(value);
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }

    @Override
    public String toString() {
        return this.name();
    }

    public static List<SelectItem> getAll() {
        List<SelectItem> items = new ArrayList<>(FilterMode.values().length);
        for (BarCodeFormat item : BarCodeFormat.values()) {
            items.add(item.toSelectItem());
        }
        return items;
    }

    public SelectItem toSelectItem() {
        return new SelectItem(this.name(), this.getCode());
    }
}
