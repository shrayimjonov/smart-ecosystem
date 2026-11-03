package uz.uzkassa.silen.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uz.uzkassa.silen.dto.SelectItem;

@Getter
@AllArgsConstructor
public enum UsageType {

    USED_FOR_PRODUCTION("КМ использован на производстве"),
    VERIFIED("Нанесение КМ подтверждено"),
    PRINTED("КМ был напечатан");

    final String text;

    public SelectItem toSelectItem() {
        return new SelectItem(this.name(), this.getText());
    }

}
