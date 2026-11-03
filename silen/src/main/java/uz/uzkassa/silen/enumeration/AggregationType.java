package uz.uzkassa.silen.enumeration;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.silen.dto.SelectItem;

import java.util.Arrays;
import java.util.List;

@Getter
public enum AggregationType {
    AGGREGATION("Сканирование"),
    PRINTING("Печать"),
    STATION("Станция"),
    UPDATE("Редактирование"),
    LINE("Линия производства"),
    SINGLE("Одиночный"),
    BATCH("Массовый");


    private final String text;

    AggregationType(String text) {
        this.text = text;
    }

    public static List<SelectItem> getAll() {
        return Arrays.asList(
                AggregationType.AGGREGATION.toSelectItem(),
                AggregationType.PRINTING.toSelectItem(),
                AggregationType.STATION.toSelectItem(),
                AggregationType.LINE.toSelectItem(),
                AggregationType.SINGLE.toSelectItem(),
                AggregationType.BATCH.toSelectItem()
        );
    }

    public SelectItem toSelectItem() {
        return new SelectItem(this.name(), this.getText());
    }

    public static AggregationType fromString(String code) {
        if (StringUtils.isBlank(code)) return AggregationType.AGGREGATION;

        for (AggregationType item : AggregationType.values()) {
            if (code.equals(item.name())) {
                return item;
            }
        }
        return AggregationType.AGGREGATION;
    }
}
