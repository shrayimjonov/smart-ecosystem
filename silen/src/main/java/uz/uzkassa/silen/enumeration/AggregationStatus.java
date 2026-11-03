package uz.uzkassa.silen.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uz.uzkassa.silen.dto.SelectItem;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;


@Getter
@AllArgsConstructor
public enum AggregationStatus {
    DRAFT("Создано"),
    ERROR("Ошибка"),
    EMPTY("Пустой"),
    SENT("Отправлено в Турон"),
    CREATED("Агрегировано"),
    DISAGGREGATED("Дизагрегировано"),
    UPDATED("Редактировано"),
    UTILIZED("Утилизировано"),
    RETURNED("Возвращен"),
    SHIPPED("Отгружено"),
    IN_PROGRESS("В процессе"),
    APPLIED("Нанесен"),
    EMITTED("Эмитирован"),
    INTRODUCED("Введен в оборот"),
    WRITTEN_OFF("Утилизирован"),
    WITHDRAWN("Выведен из оборота"),
    RESERVED_NOT_USED("Зарезервировано. Не использовать"),
    INTRODUCED_RETURNED("Возвращен в оборот"),
    WAIT_SHIPMENT("Ожидает отгрузки"),
    EXPORTED("Используется для документов эспорта"),
    APPLIED_NOT_PAID("Нанесен, не оплачен"),
    DROPOUT("Списано");

    private final String text;
    private static final List<SelectItem> SELECT_ITEMS = Arrays.asList(
            DRAFT.toSelectItem(),
            IN_PROGRESS.toSelectItem(),
            APPLIED.toSelectItem(),
            SENT.toSelectItem(),
            CREATED.toSelectItem(),
            ERROR.toSelectItem(),
            UPDATED.toSelectItem(),
            UTILIZED.toSelectItem(),
            SHIPPED.toSelectItem(),
            DISAGGREGATED.toSelectItem(),
            DROPOUT.toSelectItem(),
            RETURNED.toSelectItem()
    );
    private static final List<SelectItem> ALL_ITEMS = Arrays.asList(
            DRAFT.toSelectItem(),
            IN_PROGRESS.toSelectItem(),
            APPLIED.toSelectItem(),
            SENT.toSelectItem(),
            CREATED.toSelectItem(),
            ERROR.toSelectItem(),
            UPDATED.toSelectItem()
    );

    public static AggregationStatus fromString(String code) {
        if (code != null) {
            for (AggregationStatus item : AggregationStatus.values()) {
                if (code.equalsIgnoreCase(item.getText()) || code.equals(item.name())) {
                    return item;
                }
            }
        }
        return null;
    }

    public static List<SelectItem> getAll() {
        return SELECT_ITEMS;
    }

    public static List<SelectItem> getList() {
        return ALL_ITEMS;
    }

    public boolean checkStatusContainNotAbleToShip() {
        return EnumSet.of(
                        AggregationStatus.ERROR,
                        AggregationStatus.UPDATED,
                        AggregationStatus.UTILIZED,
                        AggregationStatus.SHIPPED,
                        AggregationStatus.DISAGGREGATED,
                        AggregationStatus.DROPOUT,
                        AggregationStatus.IN_PROGRESS
                )
                .contains(this);
    }

    public boolean checkingIfAggregationNotAbleToAggregate() {
        return EnumSet.complementOf(EnumSet.of(AggregationStatus.CREATED, AggregationStatus.RETURNED)).contains(this);
    }

    public boolean checkingIfAggregationNotAbleToUpdate() {
        return EnumSet.complementOf(EnumSet.of(AggregationStatus.CREATED, AggregationStatus.DISAGGREGATED,
                        AggregationStatus.RETURNED, AggregationStatus.SENT,
                        AggregationStatus.UPDATED, AggregationStatus.ERROR))
                .contains(this);
    }

    public boolean checkingIfMarkNotAbleToAggregate(boolean isBlock) {
        if (isBlock)
            return EnumSet.complementOf(EnumSet.of(AggregationStatus.CREATED, AggregationStatus.RETURNED)).contains(this);
        else
            return EnumSet.complementOf(EnumSet.of(AggregationStatus.DRAFT, AggregationStatus.RETURNED)).contains(this);
    }

    public SelectItem toSelectItem() {
        return new SelectItem(this.name(), this.getText());
    }

    @JsonCreator
    public static AggregationStatus forValue(String value) {
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
}
