package uz.uzkassa.smartpossupply.dto.launcher;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LauncherTerminalRequestDto implements Serializable {

    @JsonProperty("action")
    private String action;
    @JsonProperty("message")
    private String message;
    @JsonProperty("notification")
    private LauncherNotificationDTO notification;
    @JsonProperty("terminal_numbers")
    private List<String> terminalNumbers = new ArrayList<>();
    @JsonProperty("bank_type")
    private String bankType;

    public LauncherTerminalRequestDto() {
    }


    public LauncherTerminalRequestDto(LauncherNotificationDTO notification, List<String> terminalNumbers) {
        this.notification = notification;
        this.terminalNumbers = terminalNumbers;
    }
}
