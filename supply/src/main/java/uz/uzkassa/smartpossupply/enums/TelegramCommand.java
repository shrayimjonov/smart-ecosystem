package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.io.Serializable;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TelegramCommand implements Serializable {

    START("/start"),
    STOP("/stop"),
    SELECT_LANGUAGE("SELECT_LANGUAGE"),
    ENTER_NAME("ENTER_NAME"),
    SHARE_PHONE("SHARE_PHONE"),
    OPEN_WEB_APP("OPEN_WEB_APP");

    private final String command;

    TelegramCommand(String command) {
        this.command = command;
    }

    public static TelegramCommand get(String command) {
        if (command == null || command.isEmpty()) {
            return null;
        }
        for (TelegramCommand commandEnum : TelegramCommand.values()) {
            if (command.equals(commandEnum.getCommand())) {
                return commandEnum;
            }
        }
        return null;
    }
}
