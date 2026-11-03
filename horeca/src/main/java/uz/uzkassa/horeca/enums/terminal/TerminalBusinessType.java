package uz.uzkassa.horeca.enums.terminal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
@Getter
@AllArgsConstructor
public enum TerminalBusinessType {
    OOO("OOO", "ООО", "ООО", "OOO"),
    CHP("CHP", "ЧП", "ЧП", "CHP"),
    IP("IP", "ИП", "ИП", "IP"),
    AO("AO", "АО", "АО", "AO"),
    ZAO("ZAO", "ЗАО", "ЗАО", "ZAO"),
    SP("SP", "СП", "СП", "SP"),
    DP("DP", "ДП", "ДП", "DP"),
    DX("DX", "ДХ", "ДХ", "DX"),
    FX("FX", "ФХ", "ФХ", "FX"),
    UP("UP", "УП", "УП", "UP"),
    NOU("NOU", "НОУ", "НОУ", "NOU"),
    DOU("DOU", "ДОУ", "ДОУ", "DOU"),
    CHMP("CHMP", "ЧМП", "ЧМП", "CHMP"),
    PK("PK", "ПК", "ПК", "PK"),
    GUP("GUP", "ГУП", "ГУП", "GUP"),
    TCHSJ("TCHSJ", "ТЧСЖ", "ТЧСЖ", "TCHSJ"),
    SP_OOO("SP_OOO", "СП OOO", "СП ООО", "SP OOO"),
    GU("GU", "ГУ", "ГУ", "GU"),
    IP_OOO("IP_OOO", "ИП ООО", "ИП ООО", "ИП ООО"),
    RG("RG", "РГ", "РГ", "РГ"),
    NNT("NNT", "ННО", "ННТ", "NNT"),
    ODO("ODO", "ОДО", "ОДО", "ODO");

    private final String code;

    @Getter
    private final String nameRu;

    private final String nameUzCyrillic;
    private final String nameUzLatin;

    @JsonCreator
    public static TerminalBusinessType forCodeValue(Map<String, String> value) {
        return TerminalBusinessType.valueOf(value.get("code"));
    }

    public static TerminalBusinessType getByNameRu(String nameRu) {
        for (TerminalBusinessType businessType : values()) {
            if (businessType.getCode().equalsIgnoreCase(nameRu)) {
                return businessType;
            }
            if (businessType.getNameRu().equalsIgnoreCase(nameRu)) {
                return businessType;
            }
            if (businessType.getNameUzCyrillic().equalsIgnoreCase(nameRu)) {
                return businessType;
            }
            if (businessType.getNameUzLatin().equalsIgnoreCase(nameRu)) {
                return businessType;
            }
        }
        return null;
    }

    public static TerminalBusinessType getByCode(String code) {
        return Arrays
            .stream(TerminalBusinessType.values())
            .filter(businessType -> businessType.getCode().equals(code))
            .findFirst()
            .orElse(null);
    }
}
