package uz.uzkassa.smartpossupply.dto.launcher;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class LauncherTerminalResponseDto implements Serializable {

    @JsonProperty("status")
    private String status;
    @JsonProperty("message")
    private String message;
}
