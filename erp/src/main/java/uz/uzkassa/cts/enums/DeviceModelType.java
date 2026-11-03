package uz.uzkassa.cts.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeviceModelType {
    N5(1L),
    P10(659L),
    P100(31L),
    SW(0L);

    public final Long ofdId;
}
