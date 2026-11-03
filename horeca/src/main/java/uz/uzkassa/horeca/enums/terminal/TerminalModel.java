package uz.uzkassa.horeca.enums.terminal;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TerminalModel {
    N5(1L),
    P10(659L),
    P100(31L),
    FM(0L);

    public final Long ofdId;
}
