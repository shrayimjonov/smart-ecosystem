package uz.uzkassa.smartpos.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ProviderTypeEnum {
    PLAY_MOBIL,
    ETC
}
