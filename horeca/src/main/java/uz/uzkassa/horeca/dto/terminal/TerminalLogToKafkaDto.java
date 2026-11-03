package uz.uzkassa.horeca.dto.terminal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TerminalLogToKafkaDto {
    private String serialNumber;
    @NotNull
    private LocalDateTime dateTime;
    @NotBlank
    private String level;
    @NotBlank
    private String message;
    private String tag;
    private String throwable;
}
