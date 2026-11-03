package uz.uzkassa.smartpos.dto.elasticsearch;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TerminalSerialNumberDto implements Serializable {
    private String serialNumber;
}
