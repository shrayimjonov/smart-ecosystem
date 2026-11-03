package uz.uzkassa.smartpos.dto.integration.launcher;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.enums.TerminalAction;

import java.io.Serializable;
import java.util.Set;


@Getter
@Setter
public class CtoActionDTO implements Serializable {
    @JsonProperty(value = "terminal_numbers")
    @NotEmpty
    private Set<String> terminalNumbers;
    private TerminalAction action;
    private String message;
    private CtoNotificationDTO notification;
}
