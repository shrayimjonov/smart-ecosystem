package uz.uzkassa.silen.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uz.uzkassa.silen.dto.SelectItem;


@Getter
@AllArgsConstructor
public enum DocumentType {
    INVOICE("Счет-фактура"),
    CONTRACT("Договор"),
    EMPOWERMENT("Доверенность"),
    ACCEPTANCE_TRANSFER_ACT("Акты приема-передачи"),
    WAYBILL("ТТН");

    private final String ruText;

    public static Status fromString(String code) {
        if (code != null) {
            for (Status item : Status.values()) {
                if (code.equalsIgnoreCase(item.getRuText()) || code.equals(item.name())) {
                    return item;
                }
            }
        }
        return null;
    }

    @JsonCreator
    public static Status forValue(String value) {
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

    public SelectItem toSelectItem() {
        return new SelectItem(this.name(),this.getRuText());
    }
}
