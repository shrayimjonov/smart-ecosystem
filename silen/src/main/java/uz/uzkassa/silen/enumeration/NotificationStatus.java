package uz.uzkassa.silen.enumeration;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uz.uzkassa.silen.dto.SelectItem;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum NotificationStatus {
    ERROR("Ошибка"),
    WARNING("Предупреждение"),
    INFO("Уведомление");

    private final String text;


    public static List<SelectItem> getAll() {
        return Stream.of(NotificationStatus.values()).map(NotificationStatus::toSelectItem).collect(Collectors.toList());
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
