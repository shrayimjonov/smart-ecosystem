package uz.uzkassa.cts.dto.factura.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum Status {
    DRAFT("Сохраненные"),
    PENDING("Ожидающие"),
    CANCELLED("Отмененные"),
    AGENT_ACCEPTED("Подписанные агентом"),
    AGENT_REJECTED("Отклоненные агентом"),
    ACCEPTED("Подписанные"),
    REJECTED("Отказанные");

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

}
