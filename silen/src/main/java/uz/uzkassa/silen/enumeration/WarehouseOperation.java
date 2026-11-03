package uz.uzkassa.silen.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WarehouseOperation {
    Production("Производство продукции", 4),
    ShipmentBuyer("Отгрузка (покупателю)", 9),
    TransferOut("Переобразование от", 11),
    TransferIn("Переобразование на", 12),
    Income("Приход", 13),
    Returned("Возврат продукцию", 14),
    Utilized("Утилизировано", 15),
    Disaggregated("Дизагрегировано", 16),
    //    Child_box_returned("Коробка возврашено", 17),
    Updated("Редактировано", 18),
    Dropout("Списано", 19);

    private final String nameRu;
    private final Integer code;

    public static WarehouseOperation forCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (WarehouseOperation mode : values()) {
            if (code.equals(mode.getCode())) {
                return mode;
            }
        }
        return null;
    }

    public boolean isIncrease() {
        return this.equals(Production)
                || this.equals(TransferIn)
                || this.equals(Income)
                || this.equals(Returned);
    }
}
