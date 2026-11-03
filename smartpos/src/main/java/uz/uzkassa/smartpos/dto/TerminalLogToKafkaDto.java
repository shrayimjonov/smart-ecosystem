package uz.uzkassa.smartpos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class TerminalLogToKafkaDto {
    private String serialNumber;
    @NotNull
    private LocalDateTime dateTime;
    @NotBlank
    private String level;
    private String message;
    private String tag;
    private String throwable;
}
