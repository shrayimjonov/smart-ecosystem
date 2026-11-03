package uz.uzkassa.silen.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public enum Mode implements Serializable {
    Flushing("Промывка", 1),
    Calibration("Калибровка", 2),
    TechnologicalRun("Технологический прогон", 3),
    Production("Производство продукции", 4),
    Stop("Остановка", 5),
    Return("Приём (возврат)", 6),
    Purchase("Приём (закупка)", 7),
    Consumption("Внутренный расход", 8),
    ShipmentBuyer("Отгрузка (покупателю)", 9),
    ShipmentReturn("Отгрузка (возврат)", 10);

    private final String nameRu;
    private final Integer code;

    public static Mode forCode(Integer code){
        if(code == null){
            return null;
        }
        for(Mode mode : values()){
            if(code.equals(mode.getCode())){
                return mode;
            }
        }
        return null;
    }
}
