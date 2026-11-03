package uz.uzkassa.smartpossupply.dto.launcher;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpossupply.enums.TerminalAction;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by: Shuxratjon Rayimjonov
 * Date: 11.12.2024
 */

@Getter
@Setter
public class LauncherIntegrationDTO implements Serializable {
    @JsonProperty(value = "terminal_numbers")
    @NotEmpty
    private Set<String> terminalNumbers;
    private TerminalAction action;
    private String message;
    private LauncherNotificationDTO notification;
}
