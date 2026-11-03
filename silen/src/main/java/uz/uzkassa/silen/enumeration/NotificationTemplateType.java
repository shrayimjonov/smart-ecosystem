package uz.uzkassa.silen.enumeration;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uz.uzkassa.silen.dto.SelectItem;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@JsonFormat(shape = JsonFormat.Shape.STRING)
@Getter
@AllArgsConstructor
public enum NotificationTemplateType {
    MODE_CHANGE("Изменение режима устройства"),
    LOGIN("Вошел в систему"),
    PRODUCT_ADD("Добавить продукт"),

    PAID_SUCCESS("Оплата выполнена успешно!"),
    TARIFF_EXPIRES("Действия тарифа заканчивается"),
    TARIFF_COST_CHANGED("Изменена стоимость тарифа"),
    INVOICE_PACKAGE("Приобретите пакет счет-фактур"),
    AGGREGATION_PACKAGE("Приобретите пакет агрегаций"),
    LINE_IN_MODE("Линия в режиме \"Остановка\""),

    SYNC_PACKAGES("Синхронизация пакетов завершена"),
    EXPORT_PRODUCT("Экспорт продукции завершён!"),
    IMPORT_PRODUCT("Импорт продукции завершён!");
//    TRIAL_ALREADY_USED("Пробный период уже был использован!");

    private final String text;

    public static List<SelectItem> getAll() {
        return Stream.of(NotificationTemplateType.values())
            .map(NotificationTemplateType::toSelectItem)
            .collect(Collectors.toList());

    }

    public SelectItem toSelectItem() {
        return new SelectItem(this.name(), this.getText());
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }

    @Override
    public String toString() {
        return this.name();
    }

}
