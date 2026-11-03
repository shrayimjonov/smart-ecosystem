package uz.uzkassa.smartpos.integration.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum DeviceStatusEnum implements Serializable {

    REGISTERED("REGISTERED", "Зарегистрированный", "Рўйхатга Олинган", "Ro'yxatga Olingan"),//IN
    NOT_REGISTERED("NOT_REGISTERED", "Не Зарегистрирован", "Рўйхатга Олинмаган", "Ro'yxatga Olinmagan"),
    UNREGISTERED("UNREGISTERED", "Снять с регистрации", "Рўйхатдан Чикарилган", "Ro'yxatdan Chiqarilgan"),
    TRANSFERRED("TRANSFERRED", "Передано", "Юборилган", "Yuborilgan"),//OUT
    FISCAL("FISCAL", "Фискальный и Неактив", "Фискальный и Неактив", "Фискальный и Неактив"),
    FISCAL_ACTIVE("FISCAL_ACTIVE", "Фискальный и Актив", "Фискал, Фаол", "Fiscal, Faol"),
    SOLD("SOLD", "Продан", "Сотилган", "Sotilgan"),
    BRON("BRON", "Брон", "Брон", "Band"),
    BLOCKED("BLOCKED", "Блокированный", "Блокланган", "Bloklangan"),
    UN_BLOCKED("UN_BLOCKED", "Разблокирован", "Блокдан Чиқарилган", "Blokdan Chiqarilgan"),
    DELETED("DELETED", "Удален", "Ўчирилган", "O'chirilgan"),
    WRONG_ADDED("WRONG_ADDED", "Неправильно добавлено", "Нотўғри қўшилган", "Noto'g'ri qo'shilgan"),
    BURNT_OUT("BURNT_OUT", "Сгорел", "Куйган", "Kuygan"),
    BROKEN("BROKEN", "Сломаны", "Синган", "Singan"),
    LOST("LOST", "Потерял", "Йўқолган", "Yo'qolgan"),
    STOLEN("Stolen", "Украден", "Ўғриланган", "O'g'rilangan"),
    OTHER("OTHER", "Other", "Other", "Другой"),
    DIS_FISCAL("DIS_FISCAL", "DIS_FISCAL", "DIS_FISCAL", "DIS_FISCAL");

    private final String nameRu;
    private final String nameUzCyrillic;
    private final String nameUzLatin;
    private final String code;

    DeviceStatusEnum(String code, String nameRu, String nameUzCyrillic, String nameUzLatin) {
        this.code = code;
        this.nameRu = nameRu;
        this.nameUzCyrillic = nameUzCyrillic;
        this.nameUzLatin = nameUzLatin;
    }

    public static DeviceStatusEnum get(String code) {
        if (code == null || code.isEmpty()) {
            return null;
        }
        return Arrays.stream(DeviceStatusEnum.values()).filter(statusEnum -> statusEnum.getCode().equals(code)).findFirst().orElse(null);
    }

    @SuppressWarnings("unused")
    @JsonCreator
    public static DeviceStatusEnum forValue(Map<String, String> value) {
        return DeviceStatusEnum.valueOf(value.get("code"));
    }
}
