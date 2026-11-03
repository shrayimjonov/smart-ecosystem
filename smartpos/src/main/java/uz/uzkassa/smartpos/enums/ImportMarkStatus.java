package uz.uzkassa.smartpos.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ImportMarkStatus {
    IN_PROCESS("In process"),
    FINISHED("Finished"),
    FAILED("Failed");

    private final String text;
}
