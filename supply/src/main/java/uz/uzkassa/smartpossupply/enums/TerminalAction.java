package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by: Shuxratjon Rayimjonov
 * Date: 11.12.2024
 */

@Getter
@AllArgsConstructor
public enum TerminalAction {
    UPDATE("Обновить версию"),
    BLOCK("Блокировать"),
    UNBLOCK("Разблокировать"),
    SHOW("Показать"),
    HIDE("Скрыть"),
    CLEAR("Очистить"),
    DELETE_VERSION("Удалить версию"),
    DELETE_ALL("Удалить все"),
    SET_PASSWORD("Поставить пароль"),
    REMOVE_PASSWORD("Убрать пароль"),
    APPLY_TEMPLATE("Применить шаблон");

    private final String text;

    @JsonValue
    public String toValue() {
        return this.name();
    }

    @Override
    public String toString() {
        return this.name();
    }
}
