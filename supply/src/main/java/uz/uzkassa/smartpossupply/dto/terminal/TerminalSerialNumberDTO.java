package uz.uzkassa.smartpossupply.dto.terminal;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TerminalSerialNumberDTO implements Serializable {

    String serialNumber;
}
