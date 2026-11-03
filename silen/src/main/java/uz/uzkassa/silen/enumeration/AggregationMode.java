package uz.uzkassa.silen.enumeration;

import lombok.Getter;

@Getter
public enum AggregationMode {
    Massive("Массовый"),
    Single("Одиночный");

    private final String text;

    AggregationMode(String code) {
        this.text = code;
    }
}
