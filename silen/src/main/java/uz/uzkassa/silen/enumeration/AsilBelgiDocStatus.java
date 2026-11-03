package uz.uzkassa.silen.enumeration;

import lombok.Getter;
import uz.uzkassa.silen.dto.SelectItem;

import java.util.ArrayList;
import java.util.List;

@Getter
public enum AsilBelgiDocStatus {
    IN_PROGRESS("CHECKED_OK"),
    CHECKED_OK("Оформлен"),
    CHECKED_NOT_OK("Ошибка при проверке"),
    PROCESSING_ERROR("Ошибка при обработке"),
//    UNDEFINED("Не определён"),
    CANCELLED("Документ отменён"),
//    WAIT_ACCEPTANCE("Ожидание приемку"),
//    WAIT_PARTICIPANT_REGISTRATION("Ожидает регистрации участника в Системе")
    ;

    final String text;

    AsilBelgiDocStatus(String text) {
        this.text = text;
    }


    public SelectItem toSelectItem() {
        return new SelectItem(this.name(), this.getText());
    }

    public static List<SelectItem> getAll() {
        List<SelectItem> items = new ArrayList<>(AsilBelgiDocStatus.values().length);
        for (AsilBelgiDocStatus item : AsilBelgiDocStatus.values()) {
            items.add(item.toSelectItem());
        }
        return items;
    }
}
