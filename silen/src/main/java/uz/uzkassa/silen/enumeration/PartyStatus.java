package uz.uzkassa.silen.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uz.uzkassa.silen.dto.SelectItem;

@Getter
@AllArgsConstructor
public enum PartyStatus {
    EMITTED("Эмитирован"),
    APPLIED("Нанесен");

    private final String text;

    public SelectItem toSelectItem() {
        return new SelectItem(this.name(), this.getText());
    }

}
