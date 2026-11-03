package uz.uzkassa.smartpossupply.dto.terminal;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class TerminalLogKafkaDTO implements Serializable {

    String serialNumber;

    LocalDateTime dateTime;

    String level;

    String message;

    String tag;

    String throwable;
}
