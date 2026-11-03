package uz.uzkassa.cts.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ProviderType {
    PLAY_MOBIL,
    ETC
}
